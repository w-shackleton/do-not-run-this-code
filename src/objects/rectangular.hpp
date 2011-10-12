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
			/**
			 * Updates the corner points array.
			 * If actualPositions is true, then the non-stepped positions will be used.
			 */
			void updateCornerPoints(bool actualPositions = false);

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
			double getRotation();

			bool insideBounds(double sx, double sy);
			bool intersects(SpaceItem& second);
			
			/**
			 * Total W&H of the rect
			 */
			double sx, sy;

			/**
			 * Gets the stepped X coord of the object.
			 */
			inline double getSX() {
				if(isGridSnapped) return floor(sx / GRID_SIZE_2) * GRID_SIZE_2;
				return sx;
			}
			/**
			 * Gets the stepped Y coord of the object.
			 */
			inline double getSY() {
				if(isGridSnapped) return floor(sy / GRID_SIZE_2) * GRID_SIZE_2;
				return sy;
			}

			double rotation;
	};
};

#endif
