// This is the header for the OpenCL bignum library.
// This file shouldn't be included directly - it defines
// The implementations for each specific bignum using macros.

// The following must be defined before inclusion:

// N - the width of the bignum (1, 2, 4, 8 etc.)
// H - N / 2 (not needed when N=1)
// D - N * 2

// If not included by bignum.cl, do nothing
#ifdef BIGNUM_H

// Variable naming system
// This macro defines the name of a function using the current
// bignum width - think templates.
// eg. N=4
// NAME(bn_add) -> bn_add_4
// HNAME(bn_add) -> bn_add_2
// DNAME(bn_add) -> bn_add_8
#define NAME_HELP1(_x, _num) _x##_##_num
#define NAME_HELP2(_x, _num) NAME_HELP1(_x, _num)
#define NAME(_x) NAME_HELP2(_x, N)
#define HNAME(_x) NAME_HELP2(_x, H)
#define DNAME(_x) NAME_HELP2(_x, D)

//#define VEC_HELP1(_x, _num) _x##_num
//#define VEC_HELP2(_x, _num) VEC_HELP1(_x, _num)
//#if (N == 1)
//#define VEC(_x) struct { _x x; }
//#else
//#define VEC(_x) VEC_HELP2(_x, N)
//#endif

// Current BN size is BN_N, half is BN_H, double is BN_D
#define BNdata_N NAME(BNdata)
#define BNdata_H HNAME(BNdata)
#define BNdata_D DNAME(BNdata)

#define BN_N NAME(BN)
#define BN_H HNAME(BN)
#define BN_D DNAME(BN)

#if (N == 1)
// DEFINITIONS FOR BN1

// Constructor
inline BN_N NAME(bn_make)(float val) {
	BN_N result;
	result.negative = val < 0;
	val = fabs(val);
	result.s[0] = val;
	return result;
}
inline float NAME(bn_val)(BN_N in) {
	float val = in.s[0];
	if(in.negative) val = -val;
	return val;
}
#else
// DEFINITIONS FOR ALL OTHER BNs

// Constructor
inline BN_N NAME(bn_make)(float val) {
	BN_N result;
	result.negative = val < 0;
	val = fabs(val);
	for(int i = 0; i < N; i++) {
		if(i == 0)
			result.s[i] = (T)val & mask;
		else
			result.s[i] = val - (float)(result.s[i-1] * maximumValue);
		val *= (float) maximumValue;
	}
	return result;
}
inline float NAME(bn_val)(BN_N in) {
	float result = 0;
	float multiplier = 1;
	for(int i = 0; i < N; i++) {
		result += ((float) in.s[i]) * multiplier;
		multiplier /= (float) maximumValue;
	}
	if(in.negative) result = -result;
	return result;
}
#endif

// Adds two BNs. Does no checking of signs
inline BN_N NAME(bn_internal_add)(BN_N l, BN_N r) {
	BN_N dest;
	dest.negative = false;
	int i = N;
	L carry = 0;
	while(i--) {
		carry += (L)l.s[i] + (L)r.s[i];
		dest.s[i] = carry & mask;
		carry >>= bitsPerValue;
	}
	return dest;
}

inline BNdata_N NAME(bn_internal_add_data)(BNdata_N l, BNdata_N r) {
	BNdata_N dest;
	int i = N;
	L carry = 0;
	while(i--) {
		carry += (L)l.s[i] + (L)r.s[i];
		dest.s[i] = carry & mask;
		carry >>= bitsPerValue;
	}
	return dest;
}

// Subtracts two BNs. Does no checking of signs
inline BN_N NAME(bn_internal_sub)(BN_N l, BN_N r) {
	BN_N dest;
	dest.negative = false;

	// Determine whether l or r is greater
	// if r, swap and negate
	for(int i = 0; i < N; i++) {
		if(l.s[i] > r.s[i]) {
			break;
		} else if(l.s[i] < r.s[i]) {
			BN_N tmp = l;
			l = r;
			r = tmp;
			dest.negative = true;
			break;
		}
	}

	int i = N;
	L carry = 0;
	while(i--) {
		// Hopefully the carry here is only -1, 0 or 1
		carry = (L)l.s[i] - (L)r.s[i] + (char)carry;
		dest.s[i] = carry & mask;
		carry >>= bitsPerValue;
	}
	return dest;
}

inline BN_N NAME(bn_add)(BN_N l, BN_N r) {
	int sign = 0;
	// Check signs for switch
	sign |= l.negative ? 1 : 0;
	sign |= r.negative ? 2 : 0;
	switch(sign) {
		case 0: // l+r
			return NAME(bn_internal_add)(l, r);
		case 1: // -l+r
			return NAME(bn_internal_sub)(r, l);
		case 2: // l-r
			return NAME(bn_internal_sub)(l, r);
		case 3: // -l-r
			{ BN_N result = NAME(bn_internal_add)(l, r);
			result.negative = true;
			return result; }
	}
	BN_N errval;
	return errval;
}

inline BN_N NAME(bn_sub)(BN_N l, BN_N r) {
	int sign = 0;
	// Check signs for switch
	sign |= l.negative ? 1 : 0;
	sign |= r.negative ? 2 : 0;
	switch(sign) {
		case 0: // l-r
			return NAME(bn_internal_sub)(l, r);
		case 1: // -l-r
			{ BN_N result = NAME(bn_internal_add)(l, r);
			result.negative = true;
			return result; }
		case 2: // l+r
			return NAME(bn_internal_add)(l, r);
		case 3: // -l+r
			return NAME(bn_internal_sub)(r, l);
	}
	BN_N errval;
	return errval;
}

inline BNdata_D NAME(bn_internal_expand)(BNdata_N val, int offset) {
	BNdata_D dest;
	for(int i = 0; i < D; i++) {
		dest.s[i] = 0;
	}
	for(int i = 0; i < N; i++) {
		if(i+offset<0) continue;
		if(i+offset >= N*2) break;
		dest.s[i+offset] = val.s[i];
	}
	return dest;
}

#ifndef BN_END_CASE

#if (N == 1)
inline BNdata_D NAME(bn_internal_mul_up)(BNdata_N l, BNdata_N r) {
	BNdata_D dest;
	L result = (L)l.s[0] * (L)r.s[0];
	dest.s[1] = result & mask;
	dest.s[0] = (result >> bitsPerValue) & mask;
	return dest;
}
#else
inline BNdata_D DNAME(bn_internal_add_data)(BNdata_D l, BNdata_D r);

inline BNdata_D NAME(bn_internal_mul_up)(BNdata_N l, BNdata_N r) {
				BNdata_H x1 = l.high;
				BNdata_H x0 = l.low;
				BNdata_H y1 = r.high;
				BNdata_H y0 = r.low;

				BNdata_N z2 = HNAME(bn_internal_mul_up)(x1, y1);
				BNdata_N z0 = HNAME(bn_internal_mul_up)(x0, y0);
				BNdata_N z1_1 = HNAME(bn_internal_mul_up)(x1, y0);
				BNdata_N z1_2 = HNAME(bn_internal_mul_up)(x0, y1);
				BNdata_N z1 = NAME(bn_internal_add_data)(z1_1, z1_2);
				
				BNdata_D z2_t = NAME(bn_internal_expand)(z2, 0 * H);
				BNdata_D z1_t = NAME(bn_internal_expand)(z1, 1 * H);
				BNdata_D z0_t = NAME(bn_internal_expand)(z0, 2 * H);

				BNdata_D acc = DNAME(bn_internal_add_data)(z0_t, z1_t);
				acc = DNAME(bn_internal_add_data)(acc, z2_t);
				return acc;
}
#endif

inline BN_N NAME(bn_mul)(BN_N l, BN_N r) {
	// If either is negative, flip sign
	bool sign = l.negative ^ r.negative;

	BNdata_D result = NAME(bn_internal_mul_up)(l.data, r.data);
	BN_N ret;
	ret.negative = sign;
	ret.data = result.mul_result;

	return ret;
}

#endif

#undef NAME_HELP1
#undef NAME_HELP2
#undef NAME
#undef HNAME
#undef DNAME

#undef VEC_HELP1
#undef VEC_HELP2
#undef VEC

#undef BN_N
#undef BN_H
#undef BN_D

#endif // ifdef BIGNUM_H
