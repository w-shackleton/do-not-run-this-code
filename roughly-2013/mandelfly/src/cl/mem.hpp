#ifndef MANDEL_CL_MEM_H
#define MANDEL_CL_MEM_H

#include "clhead.hpp"

namespace mandel {
	namespace mem {
		void freeCMem(cl_mem memobj, void *userData);
	}
}

#endif
