#include "infobox.hpp"

using namespace Objects;

#define INFOBOX_SIZE_X 50
#define INFOBOX_SIZE_Y 40

#include "../misc/data.hpp"

#include <iostream>
using namespace std;

InfoBox::InfoBox(double x, double y, double rotation) :
	Rectangular(x, y, INFOBOX_SIZE_X, INFOBOX_SIZE_Y, rotation, Misc::Point(INFOBOX_SIZE_X, INFOBOX_SIZE_Y), Misc::Point(INFOBOX_SIZE_X, INFOBOX_SIZE_Y))
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("message.png"));
}

void InfoBox::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(x, y);
	cr->rotate(rotation);
	cr->scale(.5, .5);

	//cr->set_source(img, -(sx / 2), -(sy / 2)); // Old - now using scale to make image better quality
	//cr->rectangle( - (sx / 2),  - (sy / 2), sx, sy);
	//cr->fill();

	cr->set_source(img, -sx, -sy);
	cr->rectangle(-sx, -sy, sx * 2, sy * 2); cr->fill();

	cr->scale(2, 2);
	cr->rotate(-rotation);
	cr->translate(-x, -y);
}

void InfoBox::saveXMLChild(TiXmlElement* item)
{
	Rectangular::saveXMLChild(item);
}
