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
			Rectangular(EditorCallbacks &callbacks, double x, double y, double sx, double sy, double rotation, Misc::Point min, Misc::Point max);
			Rectangular(EditorCallbacks &callbacks, TiXmlElement &item, Misc::Point min, Misc::Point max);
			virtual bool isClicked(int cx, int cy);
			virtual bool isBorderClicked(int cx, int cy);

			void moveBorder(int dx, int dy);

			void scale(int r);
			void rotate(double r); // In RADIANS

			bool insideBounds(double sx, double sy);
			bool intersects(SpaceItem& second);
			
			double sx, sy;
			double rotation;
	};
};

#endif
