#include "wall.hpp"

using namespace Objects;

#define RECT_SIZE_Y 30
#define RECT_MIN_X 80
#define RECT_MAX_X 1000

Wall::Wall(double x, double y, double sx, double rotation) :
	Rectangular(x, y, sx, RECT_SIZE_Y, rotation, Misc::Point(RECT_MIN_X, RECT_SIZE_Y), Misc::Point(RECT_MAX_X, RECT_SIZE_Y))
{
}

void Wall::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(x, y);
	cr->rotate(rotation);

	cr->set_source_rgb(0.2, 0.2, 0.2);
	cr->rectangle( - (sx / 2),  - (sy / 2), sx, sy);
	cr->fill();

	cr->set_source_rgb(0, 0, 0);
	cr->rectangle( - (sx / 2),  - (sy / 2), sx, sy);
	cr->stroke();

	cr->rotate(-rotation);
	cr->translate(-x, -y);
}
