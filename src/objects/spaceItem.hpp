#ifndef O_SPACEITEM_H
#define O_SPACEITEM_H

//#include <cairomm/surface.h>
#include <cairomm/context.h>

#define BORDER_CLICK_SIZE 8
#define ROTATION_MULTIPLIER 0.04 /* Arbitary value, purely for user interactivity */

namespace Objects
{
	class SpaceItem
	{
		protected:
			double x, y;
		public:
			SpaceItem(double sx, double sy);

			virtual void draw(Cairo::RefPtr<Cairo::Context> &cr) = 0;
			virtual bool isClicked(int cx, int cy) = 0;
			virtual bool isBorderClicked(int cx, int cy) = 0;
			void move(int dx, int dy);
			virtual void moveBorder(int dx, int dy) = 0;

			virtual void scale(int r) = 0;
			virtual void rotate(double r) = 0; // In RADIANS
	};
};

#endif
