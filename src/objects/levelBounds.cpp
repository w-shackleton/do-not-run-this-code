#include "levelBounds.hpp"

using namespace Objects;

#define RECT_MIN 1000
#define RECT_MAX 2000

LevelBounds::LevelBounds(EditorCallbacks &callbacks, double sx, double sy) :
	Rectangular(callbacks, 0, 0, sx, sy, rotation, Misc::Point(RECT_MIN, RECT_MIN), Misc::Point(RECT_MAX, RECT_MAX))
{
}

LevelBounds::LevelBounds(EditorCallbacks &callbacks, TiXmlElement &item) :
	Rectangular(callbacks, item, Misc::Point(RECT_MIN, RECT_MIN), Misc::Point(RECT_MAX, RECT_MAX))
{
}

void LevelBounds::saveXMLChild(TiXmlElement* item)
{
	Rectangular::saveXMLChild(item);
}

void LevelBounds::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(x, y);
//	cr->rotate(rotation);
//	cr->scale(.5, .5);

	double oldLw = cr->get_line_width();
	cr->set_line_width(6);

	cr->set_source_rgb(1, 1, 1);
	cr->rectangle(-sx / 2, -sy / 2, sx, sy);
	cr->stroke();

	cr->set_line_width(oldLw);

//	cr->scale(2, 2);
//	cr->rotate(-rotation);
	cr->translate(-x, -y);
}

bool LevelBounds::isClicked(int cx, int cy)
{
	return false; // We don't want interior clicking
}
