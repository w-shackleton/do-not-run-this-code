#include "wall.hpp"

using namespace Objects;

Wall::Wall(double x, double y, double sx, double rotation) :
	Rectangular(x, y, sx, 30, rotation)
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

void Wall::scale(int r)
{
	if(r < 0)
		sx += 20;
	else if(r > 0)
		sx -= 20;
	if(sx < 80)
		sx = 80;
}
