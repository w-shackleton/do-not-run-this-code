#include "infobox.hpp"

using namespace Objects;

#define INFOBOX_SIZE_X 100
#define INFOBOX_SIZE_Y 80

#include "../misc/data.hpp"

InfoBox::InfoBox(double x, double y, double rotation) :
	Rectangular(x, y, INFOBOX_SIZE_X, INFOBOX_SIZE_Y, rotation, Misc::Point(INFOBOX_SIZE_X, INFOBOX_SIZE_Y), Misc::Point(INFOBOX_SIZE_X, INFOBOX_SIZE_Y))
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("message.png"));
}

void InfoBox::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(x, y);
	cr->rotate(rotation);

	cr->set_source(img, -(sx / 2), -(sy / 2));
	cr->rectangle( - (sx / 2),  - (sy / 2), sx, sy);
	cr->fill();

	cr->rotate(-rotation);
	cr->translate(-x, -y);
}
