#include "planet.hpp"

using namespace Objects;

#ifdef HAVE_MATH_H
# include <cmath>
#endif

Planet::Planet(double sx, double sy, double sradius) :
	Spherical(sx, sy, sradius)
{
}

void Planet::saveXMLChild(TiXmlElement* item)
{
	Spherical::saveXMLChild(item);
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

// TODO: Make this more like rectangular.cpp, and move to spherical.cpp
void Planet::scale(int r)
{
	if(r < 0)
		radius *= 1.1;
	else if(r > 0)
		radius /= 1.1;
	if(radius < 30)
		radius = 30;
}
