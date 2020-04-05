/**
 * This module defines the basic OpenCL interactivity and initialisation
 */
#ifndef MANDEL_CL_H
#define MANDEL_CL_H

#include "clhead.hpp"

#include<vector>

#include <gtkmm/window.h>
#include <glibmm/objectbase.h>

namespace mandel {
	namespace comp {
		cl::Platform selectPlatform(Gtk::Window &parent);
		cl::Device selectDevice(Gtk::Window &parent, cl::Platform platform);

		class Computations : public Glib::ObjectBase {
			public:
				Computations(cl::Platform &platform, std::vector<cl::Device> &devices);
				cl::Context context;
				cl::CommandQueue renderQueue, testQueue;
				cl::Program program;

				void runTest(Glib::ustring kernelName, int size, cl::Buffer in, cl::Buffer out, std::vector<cl::Event> *events, cl::Event *event);

			private:
				void listImageFormats(cl_mem_flags flags, Glib::ustring desc);
		};
	}
}


#endif
