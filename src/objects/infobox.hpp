#ifndef INFOBOX_H
#define INFOBOX_H

#include "rectangular.hpp"
#include <cairomm/surface.h>
#include <cairomm/refptr.h>

namespace Objects
{
	class InfoBox : public Rectangular
	{
		public:
			InfoBox(double x, double y, double rotation);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);
		protected:
			Cairo::RefPtr<Cairo::ImageSurface> img;
	};
};

#endif
