#include "planet.hpp"

using namespace Objects;

#ifdef HAVE_MATH_H
# include <cmath>
#endif

#define PLANET_MIN 30
#define PLANET_MAX 200

Planet::Planet(double sx, double sy, double sradius) :
	Spherical(sx, sy, sradius, PLANET_MIN, PLANET_MAX)
{
}

Planet::Planet(TiXmlElement &item) :
	Spherical(item, PLANET_MIN, PLANET_MAX)
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
