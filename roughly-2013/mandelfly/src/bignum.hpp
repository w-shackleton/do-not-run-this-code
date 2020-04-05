/**
 * This is a set of bignum functions that use opencl vectors as data storage
 */
#ifndef MANDEL_BIGNUM_H
#define MANDEL_BIGNUM_H

#include <cmath>
#include <iostream>
#include <vector>

/*
 * The numbers here are contained in vectors (of sort)
 * the first number describes the integer digits, going down from there (ie. stored little-endian
 */

namespace mandel {
	namespace bignum {
		template <typename T> int sgn(T val) {
			    return (T(0) < val) - (val < T(0));
		}
		template <typename T> int min(T x, T y) {
			    return x < y ? x : y;
		}
		template <typename T> int max(T x, T y) {
			    return x > y ? x : y;
		}
		template<class T, int N>
			struct BNVec {
				char negative;
				union {
					T s[N];
					T x;
				};
			};
		template<class T>
			struct BNVec<T, 2> {
				char negative;
				union {
					T s[2];
					struct {
						T x, y;
					};
				};
			};
		template<class T>
			struct BNVec<T, 4> {
				char negative;
				union {
					T s[4];
					struct {
						T x, y, z, w;
					};
				};
			};

		template<class T, class L, int N>
			class BN;

		template<class T, class L, int N>
			BN<T, L, N*2> multiplyUp(BN<T, L, N> l, BN<T, L, N> r);

		// T - unsigned number type to use
		// L - unsigned number of twice the width
		// N - number of digits to store
		template<class T, class L, int N>
			class BN : public BNVec<T, N> {
				public:
					typedef BNVec<T, N> P;
					// This has to be one less as we occasionally need to split a number
					// into two smaller vectors
					static constexpr int bitsPerValue = sizeof(T) * 8; // - 1
					static constexpr L maximumValue = pow(2, bitsPerValue);
					static constexpr L mask = maximumValue - 1;

					// The size of half the digits in this number.
					static constexpr int halfSizeL = N/2;
					static constexpr int halfSizeR = N-N/2;

					BN() {
						P::negative = false;
						for(int i = 0; i < N; i++) {
							P::s[i] = 0;
						}
					}

					BN(std::vector<T> vals) {
						P::negative = false;
						for(int i = 0; i < N; i++) {
							P::s[i] = vals[i];
						}
					}

					BN(long double val) {
						P::negative = val < 0;
						val = std::abs(val);
						for(int i = 0; i < N; i++) {
							if(i == 0)
								P::s[i] = (T)val & mask;
							else
								P::s[i] = val - (long double)(P::s[i-1] * maximumValue);
//							std::cout << "Val: " << N << ", " << i << ", " << val << std::endl;
							val *= (long double) maximumValue;
						}
					}
					// To avoid accidental conversion this is a method, not an operator
					long double toFloat() const {
						long double result = 0;
						long double multiplier = 1;
						for(int i = 0; i < N; i++) {
							result += ((long double) P::s[i]) * multiplier;
							multiplier /= (long double) maximumValue;
						}
						if(isNegative()) result = -result;
						return result;
					}

					void set(BN &val) {
						P::negative = val.negative;
						for(int i = 0; i < N; i++)
							P::s[i] = val.s[i];
					}
					void set(BN val) {
						P::negative = val.negative;
						for(int i = 0; i < N; i++)
							P::s[i] = val.s[i];
					}

					inline bool isNegative() const {
						return P::negative;
					}
					
					inline void setNegative(bool negative = true) {
						P::negative = negative;
					}
					inline void setPositive() {
						P::negative = false;
					}
					inline void flipSign() {
						P::negative = !P::negative;
					}

					// Returns the left and right halfs of the vector
					BN<T, L, halfSizeL> getHigh() {
						BN<T, L, halfSizeL> dest;
						for(int i = 0; i < halfSizeL; i++) {
							dest.s[i] = P::s[i];
						}
						dest.setNegative(isNegative());
						return dest;
					}
					BN<T, L, halfSizeR> getLow() {
						BN<T, L, halfSizeR> dest;
						for(int i = 0; i < halfSizeR; i++) {
							dest.s[i] = P::s[halfSizeL+i];
						}
						dest.setNegative(isNegative());
						return dest;
					}

					BN(const BN&) = default;

					BN operator-() {
						flipSign();
						return *this;
					}

				protected:
					// Adds two BNs. Assumes both are already positive.
					BN add(BN r) {
						BN dest;
						dest.setNegative(false);
						int i = N;
						L carry = 0;
						while(i--) {
							carry += (L)this->s[i] + (L)r.s[i];
							dest.s[i] = carry & mask;
							carry >>= bitsPerValue;
						}
						return dest;
					}
					// Subtracts two BNs. Assumes both are already positive.
					// Surprisingly, two's complement is nice to us;
					// this 'just works' if l > r
					// TODO: find a better solution when r > l
					BN subtract(BN r) {
						BN dest;
						dest.setNegative(false);
						// Determine whether l or r is greater
						for(int i = 0; i < N; i++) {
							if(this->s[i] > r.s[i]) {
								break;
							} else if(this->s[i] < r.s[i]) {
								// Flip arguments
								// std::cout << "Flipping arguments" << std::endl;
								return -(r.subtract(*this));
							}
						}
						int i = N;
						L carry = 0;
						while(i--) {
							// std::cout << "Carry a " << carry << std::endl;
							// Hopefully the carry here is only -1, 0 or 1
							carry = (L)this->s[i] - (L)r.s[i] + (int8_t)carry;
							// std::cout << "Carry b " << carry << std::endl;
							dest.s[i] = carry & mask;
							carry >>= bitsPerValue;
						}
						return dest;
					}

					BN multiply(BN r) {
						// See bignum-mul.txt for reasoning for -1 offset here
						return multiplyUp(*this, r).halvePrecision(-1);
					}

				public:
					// Note here the lack of reference operator - copy is changed
					BN operator+(BN r) {
						BN l = *this;
						int sign = 0;
						// If either is negative, flip sign
						// and make value positive.
						if(l.isNegative()) {
							l.setPositive();
							sign |= 1;
						}
						if(r.isNegative()) {
							r.setPositive();
							sign |= 2;
						}
						switch(sign) {
							case 0: // l+r
								return l.add(r);
							case 1: // -l+r
								return r.subtract(l);
							case 2: // l-r
								return l.subtract(r);
							case 3: // -l-r
								return -(l.add(r));
						}
					}
					BN operator-(BN r) {
						BN l = *this;
						int sign = 0;
						// If either is negative, flip sign
						// and make value positive.
						if(l.isNegative()) {
							l.setPositive();
							sign |= 1;
						}
						if(r.isNegative()) {
							r.setPositive();
							sign |= 2;
						}
						switch(sign) {
							case 0: // l-r std::cout << "(0)" << std::endl;
								return l.subtract(r);
							case 1: // -l-r std::cout << "(1)" << std::endl;
								return -(l.add(r));
							case 2: // l-(-r) std::cout << "(2)" << std::endl;
								return l.add(r);
							case 3: // -l-(-r) = r-l std::cout << "(3)" << std::endl;
								return r.subtract(l);
						}
					}
					// Not needed for CL
					BN &operator+=(BN r) {
						set(*this + r);
						return *this;
					}
					BN &operator-=(BN r) {
						set(*this - r);
						return *this;
					}

					BN operator*(BN r) const {
						BN l = *this;
						// If either is negative, flip sign
						// and make value positive.
						int sign = 0;
						if(l.isNegative()) {
							l.setPositive();
							sign ^= 1;
						}
						if(r.isNegative()) {
							r.setPositive();
							sign ^= 1;
						}
						if(sign) return -(l.multiply(r));
						return l.multiply(r);
					}

					BN operator*(BN r) {
						BN l = *this;
						// If either is negative, flip sign
						// and make value positive.
						int sign = 0;
						if(l.isNegative()) {
							l.setPositive();
							sign ^= 1;
						}
						if(r.isNegative()) {
							r.setPositive();
							sign ^= 1;
						}
						if(sign) return -(l.multiply(r));
						return l.multiply(r);
					}

					// Expands a BN into the next power of 2 up.
					// If offset is how many elements to pad
					// with at the beginning (0--N).
					// If outside this range, data will be lost!
					BN<T, L, N*2> expand(int offset = 0) {
						BN<T, L, N*2> dest(0);
						dest.setNegative(isNegative());
						for(int i = 0; i < N; i++) {
							if(i+offset<0) continue;
							if(i+offset >= N*2) break;
							dest.s[i+offset] = P::s[i];
						}
						return dest;
					}
					BN<T, L, N> shift(int offset = 0) {
						BN dest(0);
						dest.setNegative(isNegative());
						for(int i = 0; i < N; i++) {
							if(i+offset<0) continue;
							if(i+offset >= N) break;
							dest.s[i+offset] = P::s[i];
						}
						return dest;
					}

					BN<T, L, N/2> halvePrecision(int offset = 0) {
						BN<T, L, N/2> dest(0);
						dest.setNegative(isNegative());
						for(int i = 0; i < N; i++) {
							if(i+offset<0) continue;
							if(i+offset >= N/2) break;
							dest.s[i+offset] = P::s[i];
						}
						return dest;
					}
			};

		/**
		 * Multiplies two numbers of n width to give a number of m width
		 */
		template<class T, class L>
			BN<T, L, 2> multiplyUp(BN<T, L, 1> l, BN<T, L, 1> r) {
				BN<T, L, 2> dest(0);
				L result = (L)l.x * (L)r.x;
				dest.s[1] = result & BN<T, L, 1>::mask;
				dest.s[0] = (result >> BN<T, L, 1>::bitsPerValue) & BN<T, L, 1>::mask;
				return dest;
			}

		template<class T, class L, int N>
			BN<T, L, N*2> multiplyUp(BN<T, L, N> x, BN<T, L, N> y) {
				// TODO: Use Gtest?
				// TODO: Rename BN_T to BN_D, to avoid confusion
				// with the template arg T
				if(x.isNegative() || y.isNegative())
					std::cerr << "WARNING: Negative numbers detected in multiplyUp!" << std::endl;
				typedef BN<T, L, N/2> BN_H; // Half
				typedef BN<T, L, N> BN_N;
				typedef BN<T, L, N*2> BN_T; // 'top' size - double precision of x and y
				BN_H x1 = x.getHigh();
				BN_H x0 = x.getLow();
				BN_H y1 = y.getHigh();
				BN_H y0 = y.getLow();
//				std::cout << "x1: " << x1 << std::endl;
//				std::cout << "x0: " << x0 << std::endl;
//				std::cout << "y1: " << y1 << std::endl;
//				std::cout << "y0: " << y0 << std::endl;

				// Remember, don't use '+' in OCL!
				BN_N z2 = multiplyUp(x1, y1);
				BN_N z0 = multiplyUp(x0, y0);
				BN_N z1 = multiplyUp(x0, y1) + multiplyUp(x1, y0);
				
//				std::cout << "z2: " << z2 << std::endl;
//				std::cout << "z1: " << z1 << std::endl;
//				std::cout << "z0: " << z0 << std::endl;

				BN_T z0_t = z2.expand(0 * N/2);
				BN_T z1_t = z1.expand(1 * N/2);
				BN_T z2_t = z0.expand(2 * N/2);

//				std::cout << "z2_t: " << z2_t << std::endl;
//				std::cout << "z1_t: " << z1_t << std::endl;
//				std::cout << "z0_t: " << z0_t << std::endl;

				// No sign-checking needed in any adds here - straight additions
				return z0_t + z1_t + z2_t;
			}

		template<class T, class L, int N>
			std::ostream& operator<<(std::ostream& out, const BN<T, L, N>& val) {
				if(val.isNegative()) out << "-";
				out << "(";
				for(int i = 0; i < N; i++) {
					out << val.s[i] << ",";
				}
				out << ")";
				out << val.toFloat();
				return out;
			}

		template<class T, class L, int N>
			bool operator>(const BN<T, L, N>& l, const BN<T, L, N>& r) {
				// return memcmp(&l, &r, sizeof(BN<T, L, N>)) == 0;
				// Only checks first significant place - rounding errors etc.
				if(!l.negative && r.negative) return true;
				for(int i = 0; i < N; i++) {
					if(l.s[i] > r.s[i]) return true;
					if(l.s[i] < r.s[i]) return false;
				}
				return false;
			}
		template<class T, class L, int N>
			bool operator>=(const BN<T, L, N>& l, const BN<T, L, N>& r) {
				// return memcmp(&l, &r, sizeof(BN<T, L, N>)) == 0;
				// Only checks first significant place - rounding errors etc.
				if(!l.negative && r.negative) return true;
				for(int i = 0; i < N; i++) {
					if(l.s[i] > r.s[i]) return true;
					if(l.s[i] < r.s[i]) return false;
				}
				return true;
			}

		template<class T, class L, int N>
			bool operator==(const BN<T, L, N>& l, const BN<T, L, N>& r) {
				// return memcmp(&l, &r, sizeof(BN<T, L, N>)) == 0;
				// Only checks first significant place - rounding errors etc.
				if(l.negative != r.negative) return false;
				for(int i = 0; i < N; i++) {
					if(l.s[i] != r.s[i]) return false;
					// Only check first sig fig
					if(l.s[i] != 0) return true;
				}
				return true;
			}

		// The first number is the byte size of each element,
		// the second is the number of elements.
		typedef BN<uint32_t, uint64_t, 4> BN4_4;
		typedef BN<uint32_t, uint64_t, 2> BN4_2;
		typedef BN<uint32_t, uint64_t, 1> BN4_1;
	}
}

#endif
