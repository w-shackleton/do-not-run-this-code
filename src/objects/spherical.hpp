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

			double min, max;
		public:
			Spherical(EditorCallbacks &callbacks, double sx, double sy, double sradius, double min, double max);
			Spherical(EditorCallbacks &callbacks, TiXmlElement &item, double min, double max);
			bool isClicked(int cx, int cy);
			bool isBorderClicked(int cx, int cy);

			void moveBorder(int dx, int dy);
			void rotate(double r); // In RADIANS
			void scale(int r);
	};
};
#endif
