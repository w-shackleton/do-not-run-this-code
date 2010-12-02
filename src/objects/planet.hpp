#ifndef PLANET_H
#define PLANET_H

#include "spherical.hpp"

namespace Objects
{
	class Planet : public Spherical
	{
		protected:
			inline std::string getName() { return "planet"; }
			void saveXMLChild(TiXmlElement* item);
		public:
			Planet(double sx, double sy, double sradius);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

			void scale(int r);
	};
};
#endif
