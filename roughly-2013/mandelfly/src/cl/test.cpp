#include "test.hpp"

#include <stdint.h>
#include "cl/mandel.hpp"

using namespace mandel::comp::test;
using namespace mandel::comp;
using namespace std;

NumberTest::NumberTest(mandel::comp::Computations &worker) :
	worker(worker)
{
}

template <class T>
vector<T> singleton(T t) {
	vector<T> ret(1, t);
	return ret;
}

// OpenCL C++ frees memory objects when they are popped from the stack.
void NumberTest::testFloats() {
	function<cl_float(int)> floatGenerator = [] (int i) { return 2 * i; };
	runPerformanceTest("float", "floatTest", floatGenerator);
}
void NumberTest::testFloatVectors() {
	function<cl_float4(int)> float4Generator = [] (int i) {
		cl_float4 ret;
		ret.s[0] = i*1;
		ret.s[1] = i*2;
		ret.s[2] = i*3;
		ret.s[3] = i*4;
		return ret;
	};
	runPerformanceTest("float4", "float4Test", float4Generator);

	function<cl_float8(int)> float8Generator = [] (int i) {
		cl_float8 ret;
		ret.s[0] = i*1;
		ret.s[1] = i*2;
		ret.s[2] = i*3;
		ret.s[3] = i*4;
		ret.s[4] = i*5;
		ret.s[5] = i*6;
		ret.s[6] = i*7;
		ret.s[7] = i*8;
		return ret;
	};
	runPerformanceTest("float8", "float8Test", float8Generator);

	function<cl_float16(int)> float16Generator = [] (int i) {
		cl_float16 ret;
		ret.s[0] = i*1; ret.s[1] = i*2; ret.s[2] = i*3; ret.s[3] = i*4;
		ret.s[4] = i*5; ret.s[5] = i*6; ret.s[6] = i*7; ret.s[7] = i*8;
		ret.s[8] = i*9; ret.s[9] = i*10; ret.s[10] = i*11; ret.s[11] = i*12;
		ret.s[12] = i*13; ret.s[13] = i*14; ret.s[14] = i*15; ret.s[15] = i*16;
		return ret;
	};
	runPerformanceTest("float16", "float16Test", float16Generator);
}


BignumTest::BignumTest(mandel::comp::Computations &worker) :
	worker(worker) {}

void BignumTest::testBignumMultiply() {
	cl_int err;
	int size = 2;
	bn2 *data = (bn2*)calloc(size, sizeof(bn2));
	data[0].s[0] = 0x3F; // 0011 1111
	data[0].s[1] = 0x3F; // 0011 1111
	data[1].s[0] = 0xFF; // 1111 1111
	data[1].s[1] = 0xFFFFFFFF; // 2^32-1
	cl::Buffer inBuffer(worker.context, CL_MEM_READ_ONLY | CL_MEM_USE_HOST_PTR, size * sizeof(bn2), data, &err);
	CHECK_ERROR(err);
	inBuffer.setDestructorCallback(&mandel::mem::freeCMem, data);

	cl::Buffer outBuffer(worker.context, CL_MEM_WRITE_ONLY | CL_MEM_ALLOC_HOST_PTR, size * sizeof(bn2));
	cl::Event testFinished;
	worker.runTest("bignum2MulTest", size, inBuffer, outBuffer, NULL, &testFinished);

	bn2 *outData = (bn2*)calloc(size, sizeof(bn2));
	vector<cl::Event> waitEvts = singleton(testFinished);
	err = worker.testQueue.enqueueReadBuffer(
			outBuffer,
			CL_TRUE,
			0,
			size * sizeof(bn2),
			outData,
			&waitEvts, NULL);
	CHECK_ERROR(err);

	for(int i = 0; i < size; i++) {
		cout << "Bignum2: " << outData[i].s[0] << 
			", " << outData[i].s[1] << endl;
	}
	free(outData);
}

void FractalTest::testGenerateFractal() {
	FloatFractal floatFractal(worker);
	complex<cl_float> centre(0, 0);
	complex<cl_float> size(2, 2);
	floatFractal.generateFractal(centre, size, 640, 480);
}
