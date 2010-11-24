#include "vortex.hpp"

using namespace Objects;

#define VORTEX_MIN_X 40
#define VORTEX_MIN_Y 40
#define VORTEX_MAX_X 400
#define VORTEX_MAX_Y 400

Vortex::Vortex(double x, double y, double sx, double sy, double rotation) :
	Rectangular(x, y, sx, sy, rotation, Misc::Point(VORTEX_MIN_X, VORTEX_MIN_Y), Misc::Point(VORTEX_MAX_X, VORTEX_MAX_Y))
{
}

void Vortex::draw(Cairo::RefPtr<Cairo::Context> &cr)
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
