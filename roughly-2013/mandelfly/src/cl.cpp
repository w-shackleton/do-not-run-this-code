#include <cl.hpp>

#include<glibmm/i18n.h>
#include <iostream>
#include <vector>
#include <exception>

#include "gui.hpp"
#include "clUtil.h"
#include "cl/load.hpp"

using namespace Glib;
using namespace mandel::comp;
using namespace std;

#define CASE_PRINT(_choice) case (_choice):\
	std::cout << "    " << #_choice;\
	break;

cl::Platform mandel::comp::selectPlatform(Gtk::Window &parent) {
	vector<cl::Platform> platforms;
	cl::Platform::get(&platforms);
	if(platforms.size() == 1) return platforms[0];
	// TODO: Make a cmd-line option for selecting platform
	if(platforms.size() == 2) return platforms[1];

	function<ustring(cl::Platform&)> func = [] (cl::Platform& platform) {
		ustring result;
		platform.getInfo(CL_PLATFORM_NAME, &result);
		return result;
	};
	
	cl::Platform *platform = mandel::gui::askUser(
			_("Please select a platform"),
			_("Please choose a platform from the list"),
			parent,
			platforms,
			func);

	if(platform) {
		ustring oclVersion, oclName, oclVendor;
		platform->getInfo(CL_PLATFORM_VERSION, &oclVersion);
		platform->getInfo(CL_PLATFORM_NAME, &oclName);
		platform->getInfo(CL_PLATFORM_VENDOR, &oclVendor);
		cout << "Platform selected:" << endl;
		cout << "	Name: " << oclName << endl;
		cout << "	Vendor: " << oclVendor << endl;
		cout << "	Version: " << oclVersion << endl;
		return *platform;
	}
	throw runtime_error("No platform selected");
}

cl::Device mandel::comp::selectDevice(Gtk::Window &parent, cl::Platform platform) {
	vector<cl::Device> devices;
	platform.getDevices(CL_DEVICE_TYPE_ALL, &devices);
	if(devices.size() == 1) return devices[0];

	function<ustring(cl::Device&)> func = [] (cl::Device& device) {
		ustring result;
		device.getInfo(CL_DEVICE_NAME, &result);
		return result;
	};
	
	cl::Device *device = mandel::gui::askUser(
			_("Please select a device"),
			_("Please choose a device from the list"),
			parent,
			devices,
			func);

	if(device) {
		cl_bool imageSupport;
		device->getInfo(CL_DEVICE_IMAGE_SUPPORT, &imageSupport);
		if(!imageSupport)
			throw runtime_error("No image support for selected device");
		return *device;
	}
	throw runtime_error("No platform selected");
}

Computations::Computations(cl::Platform &platform, vector<cl::Device> &devices) {
	cl_context_properties cps[3] = { 
		CL_CONTEXT_PLATFORM, 
		(cl_context_properties)(platform)(), 
		0 
	};
	cl_int err;
	context = cl::Context(devices, cps, NULL, NULL, &err);
	CHECK_ERROR(err);
	cout << "Created CL context" << endl;

	// TODO: Check if format needed is here
//	listImageFormats(CL_MEM_ALLOC_HOST_PTR | CL_MEM_WRITE_ONLY, "CL_MEM_ALLOC_HOST_PTR");
//	listImageFormats(CL_MEM_READ_WRITE, "Default allocation");

	program = loadPrograms(context, devices);
	cout << "Compiled programs" << endl;

	renderQueue = cl::CommandQueue(context, devices[0], 0, &err);
	CHECK_ERROR(err);
	testQueue = cl::CommandQueue(context, devices[0], CL_QUEUE_PROFILING_ENABLE, &err);
	CHECK_ERROR(err);
	cout << "Created command queues" << endl;
}

void Computations::listImageFormats(cl_mem_flags flags, ustring desc) {
	// Output image formats
	cout << "Supported image formats for " << desc << ":" << endl;
	vector<cl::ImageFormat> imageFormats;
	cl_int err = context.getSupportedImageFormats(flags,
			CL_MEM_OBJECT_IMAGE2D,
			&imageFormats);
	CHECK_ERROR(err);
	for(cl::ImageFormat format : imageFormats) {
		switch(format.image_channel_order) {
			CASE_PRINT(CL_R);
			CASE_PRINT(CL_A);
			CASE_PRINT(CL_INTENSITY);
			CASE_PRINT(CL_LUMINANCE);
			CASE_PRINT(CL_RG);
			CASE_PRINT(CL_RA);
			CASE_PRINT(CL_RGB);
			CASE_PRINT(CL_RGBA);
			CASE_PRINT(CL_ARGB);
			CASE_PRINT(CL_BGRA);
		}
		switch(format.image_channel_data_type) {
			CASE_PRINT(CL_SNORM_INT8);
			CASE_PRINT(CL_SNORM_INT16);
			CASE_PRINT(CL_UNORM_INT8);
			CASE_PRINT(CL_UNORM_INT16);
			CASE_PRINT(CL_UNORM_SHORT_565);
			CASE_PRINT(CL_UNORM_SHORT_555);
			CASE_PRINT(CL_UNORM_INT_101010);
			CASE_PRINT(CL_SIGNED_INT8);
			CASE_PRINT(CL_SIGNED_INT16);
			CASE_PRINT(CL_SIGNED_INT32);
			CASE_PRINT(CL_UNSIGNED_INT8);
			CASE_PRINT(CL_UNSIGNED_INT16);
			CASE_PRINT(CL_UNSIGNED_INT32);
			CASE_PRINT(CL_HALF_FLOAT);
			CASE_PRINT(CL_FLOAT);
		}
		cout << endl;
	}
}

void Computations::runTest(Glib::ustring kernelName, int size, cl::Buffer in, cl::Buffer out, std::vector<cl::Event> *events, cl::Event *event) {
	cl_int err;
	cl::Kernel kernel(program, kernelName.c_str(), &err);
	CHECK_ERROR(err);
	kernel.setArg(0, in);
	kernel.setArg(1, out);

	cl::NDRange global(size);
	cl::NDRange local(1);
	err = testQueue.enqueueNDRangeKernel(kernel, cl::NullRange, global, local, events, event);
	CHECK_ERROR(err);
}
