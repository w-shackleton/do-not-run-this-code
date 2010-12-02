#ifndef SPHERICAL_H
#define SPHERICAL_H

#include "spaceItem.hpp"

namespace Objects
{
	class Spherical : public SpaceItem
	{
		protected:
			double radius;
			virtual void saveXMLChild(TiXmlElement* item);
		public:
			Spherical(double sx, double sy, double sradius);
			bool isClicked(int cx, int cy);
			bool isBorderClicked(int cx, int cy);

			void moveBorder(int dx, int dy);
			void rotate(double r); // In RADIANS
	};
};
#endif
