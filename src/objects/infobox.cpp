#include "infobox.hpp"

using namespace Objects;

#define INFOBOX_SIZE_X 200
#define INFOBOX_SIZE_Y 150

InfoBox::InfoBox(double x, double y, double rotation) :
	Rectangular(x, y, INFOBOX_SIZE_X, INFOBOX_SIZE_Y, rotation, Misc::Point(INFOBOX_SIZE_X, INFOBOX_SIZE_Y), Misc::Point(INFOBOX_SIZE_X, INFOBOX_SIZE_Y))
{
}

void InfoBox::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(x, y);
	cr->rotate(rotation);

	cr->set_source_rgb(0.2, 1, 0.2);
	cr->rectangle( - (sx / 2),  - (sy / 2), sx, sy);
	cr->fill();

	cr->set_source_rgb(0, 0, 0);
	cr->rectangle( - (sx / 2),  - (sy / 2), sx, sy);
	cr->stroke();

	cr->rotate(-rotation);
	cr->translate(-x, -y);
}
