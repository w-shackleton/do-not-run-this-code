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
			void saveXMLChild(TiXmlElement* item);
			Cairo::RefPtr<Cairo::ImageSurface> img;
			inline std::string getName() { return "infobox"; }
	};
};

#endif
