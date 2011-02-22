#include "blackhole.hpp"

using namespace Objects;

#include "../misc/data.hpp"

#ifdef HAVE_MATH_H
# include <cmath>
#endif

#define BLACKHOLE_RADIUS 70
#define IMG_RADIUS 250

BlackHole::BlackHole(EditorCallbacks &callbacks, double sx, double sy) :
	Spherical(callbacks, sx, sy, BLACKHOLE_RADIUS, BLACKHOLE_RADIUS, BLACKHOLE_RADIUS)
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("bh.png"));
}

BlackHole::BlackHole(EditorCallbacks &callbacks, TiXmlElement &item) :
	Spherical(callbacks, item, BLACKHOLE_RADIUS, BLACKHOLE_RADIUS)
{
	radius = BLACKHOLE_RADIUS;
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("bh.png"));
}

void BlackHole::saveXMLChild(TiXmlElement* item)
{
	Spherical::saveXMLChild(item);
}

void BlackHole::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(x, y);
//	cr->rotate(rotation);
	cr->scale(radius / IMG_RADIUS, radius / IMG_RADIUS);

	cr->set_source(img, -IMG_RADIUS, -IMG_RADIUS);
	cr->rectangle(-IMG_RADIUS, -IMG_RADIUS, IMG_RADIUS * 2, IMG_RADIUS * 2);
	cr->fill();

	cr->scale(IMG_RADIUS / radius, IMG_RADIUS / radius);
//	cr->rotate(-rotation);
	cr->translate(-x, -y);
}
