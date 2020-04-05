#include "load.hpp"

#include "clUtil.h"
#include <iostream>
#include <unistd.h>
#include <fstream>

using namespace mandel::comp;
using namespace std;
using namespace Glib;

// Bin2c files
#include "kernels/numbers.h"
#include "kernels/mandel_bn.h"
#include "kernels/mandel_float.h"

// Bin2c CL headers - must be copied to a temp directory,
// which is then specified for inclusion
#include "kernels/bn.h"
#include "kernels/bignum.h"
#include "kernels/bn_defs.h"
#include "kernels/mandel_bn_impl.h"

#define PUSH_KERNEL(_vec, _kernel) \
	sources.push_back(make_pair(reinterpret_cast<const char*>(kernel_##_kernel), sizeof(kernel_##_kernel)))
#define COPY_HEADER(_kernel) \
	copyHeader(tmpDir + "/" + #_kernel + ".h", reinterpret_cast<const char*>(kernel_##_kernel), sizeof(kernel_##_kernel))
#define DELETE_HEADER(_kernel) \
	remove((tmpDir + "/" + #_kernel + ".h").c_str())

cl::Program mandel::comp::loadPrograms(cl::Context &context, vector<cl::Device> &devices) {
	// Extract headers
	char tmpFormat[] = "/tmp/mandelfly.XXXXXX";
	char *c_tmpDir = mkdtemp(tmpFormat);
	if(!c_tmpDir) {
		printf("Failed to create temp include directory(%d): %s\n", errno, strerror(errno));
	}
	ustring tmpDir = c_tmpDir;
	COPY_HEADER(bn);
	COPY_HEADER(bn_defs);
	COPY_HEADER(bignum);
	COPY_HEADER(mandel_bn_impl);

	cl::Program::Sources sources;
	PUSH_KERNEL(sources, numbers);
	PUSH_KERNEL(sources, mandel_bn);
	PUSH_KERNEL(sources, mandel_float);
	cl_int err;
	cl::Program prog(context, sources, &err);
	CHECK_ERROR(err);
	err = prog.build(devices, (ustring("-I ") + tmpDir).c_str());
	CHECK_ERROR(err);
	ustring log;
	for(cl::Device device : devices) {
		err = prog.getBuildInfo(device, CL_PROGRAM_BUILD_LOG, &log);
		ustring name;
		device.getInfo(CL_DEVICE_NAME, &name);
		CHECK_ERROR(err);
		cout << "Build log for " << name << ":" << endl << log << endl;
	}

	// Clean up
	DELETE_HEADER(bn);
	DELETE_HEADER(bignum);
	DELETE_HEADER(bn_defs);
	DELETE_HEADER(mandel_bn_impl);

	rmdir(tmpDir.c_str());

	return prog;
}

void mandel::comp::copyHeader(ustring path, const char* data, size_t len) {
	ofstream dest;
	dest.open(path);
	dest << string(data, len);
	dest.close();
}
