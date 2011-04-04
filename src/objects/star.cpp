#include "star.hpp"

using namespace Objects;

#include "../misc/data.hpp"

#ifdef HAVE_MATH_H
# include <cmath>
#endif

#define STAR_RADIUS 30
#define IMG_RADIUS 60

Star::Star(EditorCallbacks &callbacks, double sx, double sy) :
	Spherical(callbacks, sx, sy, STAR_RADIUS, STAR_RADIUS, STAR_RADIUS)
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("star.png"));
}

Star::Star(EditorCallbacks &callbacks, TiXmlElement &item) :
	Spherical(callbacks, item, STAR_RADIUS, STAR_RADIUS)
{
	radius = STAR_RADIUS;
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("star.png"));
}

void Star::saveXMLChild(TiXmlElement* item)
{
	Spherical::saveXMLChild(item);
}

void Star::draw(Cairo::RefPtr<Cairo::Context> &cr)
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
