#include "vortex.hpp"

using namespace Objects;
using namespace std;
#include <iostream>

#include "../misc/data.hpp"

#define VORTEX_MIN_X 100
#define VORTEX_MIN_Y 100
#define VORTEX_MAX_X 400
#define VORTEX_MAX_Y 400

#define IMG_SIZE_X 400
#define IMG_SIZE_Y 400
#define IMG_NAME "vortex.png"

Vortex::Vortex(double x, double y, double sx, double sy, double rotation) :
	Rectangular(x, y, sx, sy, rotation, Misc::Point(VORTEX_MIN_X, VORTEX_MIN_Y), Misc::Point(VORTEX_MAX_X, VORTEX_MAX_Y))
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath(IMG_NAME));
}

Vortex::Vortex(TiXmlElement &item) :
	Rectangular(item, Misc::Point(VORTEX_MIN_X, VORTEX_MIN_Y), Misc::Point(VORTEX_MAX_X, VORTEX_MAX_Y))
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath(IMG_NAME));
}

void Vortex::saveXMLChild(TiXmlElement* item)
{
	Rectangular::saveXMLChild(item);
}

void Vortex::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(x, y);
	cr->rotate(rotation);
//	cr->scale(sx / (double)IMG_SIZE_X, sy / (double)IMG_SIZE_Y); // This actually looks better without this scale

//	cr->set_source(img, -IMG_SIZE_X / 2, -IMG_SIZE_Y / 2); // Old method
//	cr->rectangle( - (IMG_SIZE_X / 2),  - (IMG_SIZE_Y / 2), IMG_SIZE_X, IMG_SIZE_Y); cr->fill();
	cr->set_source(img, -IMG_SIZE_X / 2, -IMG_SIZE_Y / 2);
	cr->rectangle( - (sx / 2),  - (sy / 2), sx, sy); cr->fill();

	cr->set_source_rgb(0, 0, 0);
	cr->rectangle( - (sx / 2),  - (IMG_SIZE_Y / 2), sx, IMG_SIZE_Y); cr->stroke();

//	cr->scale((double)IMG_SIZE_X / sx, (double)IMG_SIZE_Y / sy);
	cr->rotate(-rotation);
	cr->translate(-x, -y);
}
