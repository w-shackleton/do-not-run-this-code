#ifndef FRACTAL_MANAGER_H
#define FRACTAL_MANAGER_H

#include "cl/mandel.hpp"
#include "cl.hpp"
#include <complex>

namespace mandel {
	template<class F, typename N>
	class FractalInstance : public F {
			public:
				FractalInstance(comp::Computations &worker) :
					F(worker),
					pos(N(-0.5), N(0)),
					size(N(10), N(10))
				{}
				// Holds the parameters for the current position,
				// at the current precision
				std::complex<N> pos;
				std::complex<N> size;
		};

	class FractalInstances {
		public:
			FractalInstances(comp::Computations &worker);
			FractalInstance<comp::FloatFractal, cl_float> bn1;
			FractalInstance<comp::BN2Fractal, bignum::BN4_2> bn2;
			FractalInstance<comp::BN4Fractal, bignum::BN4_4> bn4;
	};

	class Fractal2D;
	class FractalManager : private FractalInstances {
		friend class Fractal2D;
		public:
			FractalManager(mandel::comp::Computations &worker);

			inline void setShowDetails(bool showDetails) {
				this->showDetails = showDetails;
			}

			Cairo::RefPtr<Cairo::ImageSurface> redraw(float x, float y, float cx, float cy, int width, int height);
			// Adjusts the aspect ratio to be correct agian.
			void adjustAspectRatio(int width, int height);

			// TODO: Make some sort of switching mechanism -
			// between instances

			/**
			 * Resizes the viewing window to a fraction of the current -
			 * each parameter should be between 0 and 1
			 */
			void selectArea(float sizeX, float sizeY, float centreX, float centreY);

			inline std::complex<bignum::BN4_4> getPos() {
				switch(activeInstance) {
					case INSTANCE_BN4:
						return bn4.pos;
					case INSTANCE_BN2: {
						auto x = bn2.pos.real().expand();
						auto y = bn2.pos.imag().expand();
						return std::complex<bignum::BN4_4>(x, y); }
					case INSTANCE_BN1: {
						auto x = bignum::BN4_4(bn1.pos.real());
						auto y = bignum::BN4_4(bn1.pos.imag());
						return std::complex<bignum::BN4_4>(x, y); }
				}
			}
			inline std::complex<bignum::BN4_4> getSize() {
				switch(activeInstance) {
					case INSTANCE_BN4:
						return bn4.size;
					case INSTANCE_BN2: {
						auto x = bn2.size.real().expand();
						auto y = bn2.size.imag().expand();
						return std::complex<bignum::BN4_4>(x, y); }
					case INSTANCE_BN1: {
						auto x = bignum::BN4_4(bn1.size.real());
						auto y = bignum::BN4_4(bn1.size.imag());
						return std::complex<bignum::BN4_4>(x, y); }
				}
			}

			void increasePrecision();
			void decreasePrecision();

			int iterations;
		private:
			int activeInstance;
			enum {
				INSTANCE_BN1,
				INSTANCE_BN2,
				INSTANCE_BN4,
			};
			mandel::comp::Computations &worker;
			bool showDetails;
			bool generateOnHost;

			mandel::comp::FloatFractal floatFractal;
	};
}

#endif
