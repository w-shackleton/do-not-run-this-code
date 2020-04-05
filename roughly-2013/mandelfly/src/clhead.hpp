#ifndef CLHEAD_H
#define CLHEAD_H

/**
 * Includes the customised CL header
 */

#pragma GCC diagnostic push
// #pragma GCC diagnostic ignore "-Wcpp"
#include<glibmm/ustring.h>
#define __USE_DEV_STRING
typedef Glib::ustring STRING_CLASS;
#include <CL/cl.h>
// Horrible CL 1.1 compat mode thing
#undef CL_VERSION_1_2
#undef CL_EXT_SUFFIX__VERSION_1_1_DEPRECATED
#undef CL_EXT_PREFIX__VERSION_1_1_DEPRECATED
#define CL_EXT_SUFFIX__VERSION_1_1_DEPRECATED
#define CL_EXT_PREFIX__VERSION_1_1_DEPRECATED
#include <CL/cl.hpp>
#pragma GCC diagnostic pop

#endif
