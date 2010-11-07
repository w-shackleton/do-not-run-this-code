#ifndef RECTANGULAR_H
#define RECTANGULAR_H

#include <vector>
#include "spaceItem.hpp"
#include "../misc/geometry.hpp"

namespace Objects
{
	class Rectangular : public SpaceItem
	{
		protected:
			double sx, sy;
			double rotation;

			Cairo::Matrix cornerMatrix;
			void updateCornerMatrix();

			std::vector<Misc::Point> cornerPoints;
			void updateCornerPoints();
		public:
			Rectangular(double x, double y, double sx, double sy, double rotation);
			bool isClicked(int cx, int cy);
	};
};

#endif
