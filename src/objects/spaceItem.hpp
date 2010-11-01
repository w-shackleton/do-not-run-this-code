#ifndef O_SPACEITEM_H
#define O_SPACEITEM_H

//#include <cairomm/surface.h>
#include <cairomm/context.h>

class SpaceItem
{
	protected:
		double x, y;
	public:
		SpaceItem(double sx, double sy);

		virtual void draw(Cairo::RefPtr<Cairo::Context> &cr) = 0;
		virtual bool isClicked(int cx, int cy) = 0;
		void move(int dx, int dy);
};

#endif
