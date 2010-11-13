#ifndef LEVELWALL_H
#define LEVELWALL_H

#include "spaceItem.hpp"

namespace Objects
{
	class LevelWall : public SpaceItem
	{
		public:
			LevelWall(double sx, double sy);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

			void scale(int r);

			bool isClicked(int cx, int cy); // Not used
			bool isBorderClicked(int cx, int cy);

			void moveBorder(int dx, int dy);
	};
};

#endif
