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

			Misc::Point min, max;

			enum
			{
				Corner_selected,
				EdgeX_selected,
				EdgeY_selected,
			};
			int borderSelectedType;
			// Misc::Point borderCornerMoveOriginalPos;
			virtual void saveXMLChild(TiXmlElement* item);
		public:
			Rectangular(double x, double y, double sx, double sy, double rotation, Misc::Point min, Misc::Point max);
			bool isClicked(int cx, int cy);
			bool isBorderClicked(int cx, int cy);

			void moveBorder(int dx, int dy);

			void scale(int r);
			void rotate(double r); // In RADIANS
	};
};

#endif
