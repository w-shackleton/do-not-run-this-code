#ifndef CL_MANDEL_H
#define CL_MANDEL_H

#include <complex>
#include <iostream>

#include <arpa/inet.h>

#include "cl.hpp"
#include "clUtil.h"

#include "bignum.hpp"

#include "cairomm/surface.h"

namespace mandel {
	namespace comp {
		// The ImageSurfaces used here have an underlying array that MUST
		// be deleted by hand.
		Cairo::RefPtr<Cairo::ImageSurface> imageToSurface(cl::CommandQueue &queue, cl::Image2D &img, int width, int height, std::vector<cl::Event> *events = NULL);
		Cairo::RefPtr<Cairo::ImageSurface> imageToSurface(guint8 *data, int width, int height);

		inline void htonlArray(uint32_t *array, int size) {
			for(int i = 0; i < size; i++) {
				array[i] = htonl(array[i]);
			}
		}

		// N is the number type used.
		template<class N>
		class Fractal {
				struct clComplex {
					N re, im;
				};
			public:
				Fractal<N>(Computations &worker) :
					worker(worker) { }

				Cairo::RefPtr<Cairo::ImageSurface> generateFractal(std::complex<N> centre, std::complex<N> size, size_t width = 600, size_t height = 600, bool onHost = false, cl_int iterations = 50) {
					if(onHost) return generateFractalOnHost(centre, size, width, height, iterations);
					cl::ImageFormat format { CL_ARGB, CL_UNSIGNED_INT8 };
					// cl::ImageFormat format { CL_RGBA, CL_UNSIGNED_INT8 };
					cl_int err;
					cl::Image2D image(worker.context,
							CL_MEM_WRITE_ONLY | CL_MEM_ALLOC_HOST_PTR,
							format,
							width, height,
							0, NULL,
							&err);
					CHECK_ERROR(err);

					cl::Kernel kernel(worker.program, getKernel().c_str(), &err);
					CHECK_ERROR(err);

					clComplex clCentre {centre.real(), centre.imag()};
					clComplex clSize {size.real(), size.imag()};
					err = kernel.setArg(0, clCentre);
					CHECK_ERROR(err);
					err = kernel.setArg(1, clSize);
					CHECK_ERROR(err);
					err = kernel.setArg(2, iterations);
					CHECK_ERROR(err);
					err = kernel.setArg(3, image);
					CHECK_ERROR(err);

					cl::NDRange global(width, height);
					cl::NDRange local(1, 1);

					cl::Event event;
					err = worker.renderQueue.enqueueNDRangeKernel(
							kernel,
							cl::NullRange,
							global,
							local,
							NULL,
							&event);
					std::vector<cl::Event> waitFor(1, event);
					return imageToSurface(worker.renderQueue,
								image,
								width, height,
								&waitFor);
				}

				/*
				 * Implementation to test C++ bignum implementation
				 */
				Cairo::RefPtr<Cairo::ImageSurface> generateFractalOnHost(std::complex<N> centre, std::complex<N> size, size_t width = 200, size_t height = 200, cl_int iters = 50) {
					std::cout << "Creating image" << std::endl;
					guint8 *image = new guint8[width * height * 4 * sizeof(uint8_t)];

					for(int x = 0; x < width; x++) {
						for(int y = 0; y < height; y++) {
							int iterations = iters;
							std::complex<N> pos(
								N((float)x / (float)width - 0.5f) *
								size.real() + centre.real(),
								N((float)y / (float)height - 0.5) *
								size.imag() + centre.imag()
								);
							image[(y*height+x)*4+0] = 0;
							image[(y*height+x)*4+1] = 0;
							image[(y*height+x)*4+2] = 0;
							image[(y*height+x)*4+3] = 255;

							std::complex<N> z;
							while(iterations--) {
								z = z*z + pos;
								if(std::norm(z) > N(4)) {
									image[(y*width+x)*4+0] =
										iterations * 175 % 225;
									image[(y*width+x)*4+1] =
										iterations * 53 % 225;
									image[(y*width+x)*4+2] =
										iterations * 89 % 225;
									break;
								}
							}
						}
					}

					// Function takes ownership of data here
					return imageToSurface(image, width, height);
				}
			protected:
				virtual Glib::ustring getKernel() = 0;
			private:
				Computations &worker;
		};

		class FloatFractal : public Fractal<cl_float> {
			public:
				FloatFractal(Computations &worker) :
					Fractal(worker) { }
			protected:
				inline Glib::ustring getKernel() {
					return "mandelFloat";
				}
		};

		class BN2Fractal : public Fractal<bignum::BN4_2> {
			public:
				BN2Fractal(Computations &worker) :
					Fractal(worker) { }
			protected:
				inline Glib::ustring getKernel() {
					return "mandel_bn_2";
				}
		};
		class BN4Fractal : public Fractal<bignum::BN4_4> {
			public:
				BN4Fractal(Computations &worker) :
					Fractal(worker) { }
			protected:
				inline Glib::ustring getKernel() {
					return "mandel_bn_4";
				}
		};
	}
}

#endif
