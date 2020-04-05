/**
 * This module loads CL scripts
 */
#ifndef MANDEL_CL_LOAD_H
#define MANDEL_CL_LOAD_H

#include <glibmm/ustring.h>

#include "clhead.hpp"
#include<vector>

namespace mandel {
	namespace comp {
		cl::Program loadPrograms(cl::Context &context, std::vector<cl::Device> &devices);
		void copyHeader(Glib::ustring path, const char* data, size_t len);
	}
}

#endif
