#ifndef PLANET_H
#define PLANET_H

#include "spherical.hpp"

namespace Objects
{
	class Planet : public Spherical
	{
		protected:
		public:
			Planet(double sx, double sy, double sradius);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

			void scale(int r);
	};
};
#endif
