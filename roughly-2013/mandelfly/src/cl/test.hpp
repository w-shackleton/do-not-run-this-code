/**
 * This module defines the basic OpenCL interactivity and initialisation
 */
#ifndef MANDEL_CL_TEST_H
#define MANDEL_CL_TEST_H

#include "clhead.hpp"

#include <cl.hpp>
#include "mem.hpp"
#include "clUtil.h"

#include <iostream>
#include<vector>

#include <gtkmm/window.h>

namespace mandel {
	namespace comp { namespace test {
			class NumberTest {
				public:
					NumberTest(mandel::comp::Computations &worker);
					void testFloats();
					void testFloatVectors();
					template <class T>
					void runPerformanceTest(Glib::ustring name, Glib::ustring kernel, std::function<T(int)> generator) {
						const int size = 400000;
						cl_int err;
						std::cout << "Loading memory" << std::endl;
						cl::Buffer inBuffer = createSampleData(generator, size);

						cl::Buffer outBuffer(worker.context, CL_MEM_WRITE_ONLY | CL_MEM_ALLOC_HOST_PTR, size * sizeof(T), NULL, &err);
						CHECK_ERROR(err);
						cl::Event testFinished;
						std::cout << "Loaded memory" << std::endl;
						worker.runTest(kernel, size, inBuffer, outBuffer, NULL, &testFinished);

						testFinished.wait();

						std::cout << "Test finished for " << name << std::endl;
						uint64_t start, end;
						err = testFinished.getProfilingInfo(
								CL_PROFILING_COMMAND_START,
								&start);
						CHECK_ERROR(err);
						err = testFinished.getProfilingInfo(
								CL_PROFILING_COMMAND_END,
								&end);
						CHECK_ERROR(err);
						std::cout << "Took " << ((double)(end - start) / 1000000000L) << "sec." << std::endl;
}
				private:
					mandel::comp::Computations &worker;

					template <class T> cl::Buffer createSampleData(std::function<T(int)> generator, size_t size = 1000000) {
						cl_int err;
						T *data = (T*)calloc(size, sizeof(T));
						for(int i = 0; i < size; i++) {
							data[i] = generator(i);
						}
						cl::Buffer buffer(worker.context, CL_MEM_READ_ONLY | CL_MEM_USE_HOST_PTR, size * sizeof(T), data, &err);
						CHECK_ERROR(err);
						buffer.setDestructorCallback(&mandel::mem::freeCMem, data);
						return buffer;
					}
			};

			typedef cl_uint2 bn2;

			class BignumTest {
				public:
					BignumTest(mandel::comp::Computations &worker);

					void testBignumMultiply();
				private:
					mandel::comp::Computations &worker;
			};

			class FractalTest {
				public:
					inline FractalTest(mandel::comp::Computations &worker) :
						worker(worker) { }

					void testGenerateFractal();
				private:
					mandel::comp::Computations &worker;
			};
		}
	}
}

#endif
