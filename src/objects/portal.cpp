#include "portal.hpp"

using namespace Objects;

#include "../misc/data.hpp"

#ifdef HAVE_MATH_H
# include <cmath>
#endif

#define PORTAL_RADIUS 70
#define IMG_RADIUS 150

Portal::Portal(EditorCallbacks &callbacks, double sx, double sy) :
	Spherical(callbacks, sx, sy, PORTAL_RADIUS, PORTAL_RADIUS, PORTAL_RADIUS)
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("portal.png"));
}

Portal::Portal(EditorCallbacks &callbacks, TiXmlElement &item) :
	Spherical(callbacks, item, PORTAL_RADIUS, PORTAL_RADIUS)
{
	radius = PORTAL_RADIUS;
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("portal.png"));
}

void Portal::saveXMLChild(TiXmlElement* item)
{
	Spherical::saveXMLChild(item);
}

void Portal::draw(Cairo::RefPtr<Cairo::Context> &cr)
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
