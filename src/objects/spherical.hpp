#ifndef SPHERICAL_H
#define SPHERICAL_H

#include "spaceItem.hpp"

namespace Objects
{
	class Spherical : public SpaceItem
	{
		protected:
			double radius;
		public:
			Spherical(double sx, double sy, double sradius);
			bool isClicked(int cx, int cy);
	};
};
#endif
