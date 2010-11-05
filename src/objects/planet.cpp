#include "planet.hpp"

using namespace Objects;

#ifdef HAVE_MATH_H
# include <cmath>
#endif

Planet::Planet(double sx, double sy, double sradius) :
	Spherical(sx, sy, sradius)
{
}
void Planet::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->set_source_rgb(0.1, 0.9, 0.1);
	cr->arc(x, y, radius, 0, 2 * M_PI);
	cr->fill();

	cr->set_source_rgb(0, 0, 0);
	cr->arc(x, y, radius, 0, 2 * M_PI);
	cr->stroke();
}
