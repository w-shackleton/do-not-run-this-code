#ifndef VORTEX_H
#define VORTEX_H

#include "rectangular.hpp"

namespace Objects
{
	class Vortex : public Rectangular
	{
		protected:
			inline std::string getName() { return "gravity"; }
			void saveXMLChild(TiXmlElement* item);
		public:
			Vortex(double x, double y, double sx, double sy, double rotation);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);
	};
};

#endif
