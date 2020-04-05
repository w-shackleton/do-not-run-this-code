
#include "gui.hpp"
#include "cl.hpp"
#include "tests.hpp"

#include<gtkmm.h>
#include <iostream>

using namespace Gtk;
using namespace Glib;
using namespace mandel::gui;
using namespace mandel::comp;
using namespace std;

int main(int argc, char **argv) {
	testing::InitGoogleTest(&argc, argv);
	if( 0 || RUN_ALL_TESTS()) {
		cout << "WARNING: A test failed" << endl;
		// TODO: Warn user properly etc.
		exit(1);
	}

	setenv("UBUNTU_MENUPROXY", "0");

	Glib::RefPtr<Gtk::Application> app =
		Gtk::Application::create(argc, argv,
				"uk.digitalsquid.mandelfly");
	// Choose CL device
	cl::Platform platform;
	cl::Device device;
	try {
		Window win;
		platform = selectPlatform(win);
		device = selectDevice(win, platform);
	} catch(exception e) {
		cout << "Failed to load OpenCL: " << e.what() << endl;
		exit(1);
	}

	vector<cl::Device> devices;
	devices.push_back(device);

	/*
	cl_int err;
	cl_context_properties cps[3] = { 
		CL_CONTEXT_PLATFORM, 
		(cl_context_properties)(platform)(), 
		0 
	};
	cl::Context context = cl::Context(devices, cps, NULL, NULL, &err);
	CHECK_ERROR(err);

	cl::ImageFormat format { CL_ARGB, CL_UNSIGNED_INT8 };
	// cl::ImageFormat format { CL_RGBA, CL_UNSIGNED_INT8 };
	cl::Image2D image(context,
			CL_MEM_WRITE_ONLY | CL_MEM_ALLOC_HOST_PTR,
			format,
			300, 300,
			0, NULL,
			&err);
	CHECK_ERROR(err);
	*/

	MainScreen window(platform, devices);
	app->run(window);
	return 0;
}
