#include "Block.hpp"

using namespace Objects;

#include "../misc/data.hpp"

Block::Block(EditorCallbacks &callbacks, double x, double y, double sx, double sy, int type) :
	Rectangular(callbacks, x, y, sx, RECT_SIZE_Y, rotation, Misc::Point(RECT_MIN_X, RECT_SIZE_Y), Misc::Point(RECT_MAX_X, RECT_SIZE_Y))
{
}

Block::Block(EditorCallbacks &callbacks, TiXmlElement &item) :
	Rectangular(callbacks, item, Misc::Point(RECT_MIN_X, RECT_SIZE_Y), Misc::Point(RECT_MAX_X, RECT_SIZE_Y))
{
}

void Block::saveXMLChild(TiXmlElement* item)
{
	Rectangular::saveXMLChild(item);
}

void Block::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(getX(), getY());
	cr->rotate(getRotation());
	cr->scale(.5, .5);

	cr->set_source(image, -getSX(), -getSY());
	cr->rectangle(-getSX(), -getSY(), getSX() * 2, getSY() * 2);
	cr->fill();

	cr->scale(2, 2);
	cr->rotate(-getRotation());
	cr->translate(-getX(), -getY());
}
