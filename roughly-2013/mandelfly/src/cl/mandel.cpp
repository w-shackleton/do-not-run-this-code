#include "mandel.hpp"

#include "clUtil.h"

using namespace Glib;
using namespace Gdk;
using namespace mandel::comp;
using namespace std;

// NOTE: Two types of RefPtr in use. Cairo and Glib!
Cairo::RefPtr<Cairo::ImageSurface> mandel::comp::imageToSurface(cl::CommandQueue &queue, cl::Image2D &img, int width, int height, vector<cl::Event> *events) {
	// Assuming RGBA data here. OpenCL format is CL_RGBA, CL_UNSIGNED_INT8

	int stride = Cairo::ImageSurface::format_stride_for_width(Cairo::FORMAT_ARGB32, width);

	unsigned int imageBytes = stride * height * 4 * sizeof(uint8_t);
	guint8 *data = new guint8[imageBytes];
	cl::size_t<3> origin;
	origin[0]=0;
	origin[1]=0;
	origin[2]=0;

	cl::size_t<3> region;
	region[0]=width;
	region[1]=height;
	region[2]=1;

	cl_int err = queue.enqueueReadImage(img,
			CL_TRUE,
			origin, region,
			stride, 0,
			data,
			events);
	CHECK_ERROR(err);
	htonlArray(reinterpret_cast<uint32_t*>(data), imageBytes / sizeof(uint32_t));
	if(err) return Cairo::RefPtr<Cairo::ImageSurface>();

	return Cairo::ImageSurface::create(data, Cairo::FORMAT_ARGB32, width, height, width * 4 * sizeof(uint8_t));
	// TODO: Use freeMemory
}

Cairo::RefPtr<Cairo::ImageSurface> mandel::comp::imageToSurface(guint8 *data, int width, int height) {
	unsigned int imageBytes = width * height * 4 * sizeof(uint8_t);
//	htonlArray(reinterpret_cast<uint32_t*>(data), imageBytes / sizeof(uint32_t));
	return Cairo::ImageSurface::create(data, Cairo::FORMAT_ARGB32, width, height, width * 4 * sizeof(uint8_t));
	// TODO: Use freeMemory
}
