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

//			static std::vector<Type> types = {Type(1, .5, "planet1.jpg")};
		public:
			Planet(double sx, double sy, double sradius);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);
	};
};
#endif
