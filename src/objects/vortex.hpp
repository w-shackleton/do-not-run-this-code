#ifndef VORTEX_H
#define VORTEX_H

#include "rectangular.hpp"

#include <cairomm/refptr.h>

namespace Objects
{
	class Vortex : public Rectangular
	{
		protected:
			inline std::string getName() { return "gravity"; }
			void saveXMLChild(TiXmlElement* item);
			
			Cairo::RefPtr<Cairo::ImageSurface> img;
		public:
			Vortex(double x, double y, double sx, double sy, double rotation);
			Vortex(TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);
	};
};

#endif
