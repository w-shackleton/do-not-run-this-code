#include "blockpiece.hpp"

using namespace Objects;
using namespace std;
using namespace Misc;

#include "../misc/data.hpp"

Block::Block(EditorCallbacks &callbacks, double x, double y, double sx, double sy, int type) :
	Rectangular(callbacks, x, y, sx, sy, rotation, getMinSizeForType(type), getMaxSizeForType(type)),
	type(type)
{
	loadImageForType(type);
}

Block::Block(EditorCallbacks &callbacks, TiXmlElement &item) :
	Rectangular(callbacks, item, getMinSizeForType(type), getMaxSizeForType(type))
{
	item.QueryIntAttribute("type", &type);
	loadImageForType(type);
}

void Block::saveXMLChild(TiXmlElement* item)
{
	Rectangular::saveXMLChild(item);
	item->SetAttribute("type", type);
}

void Block::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cout << "Block draw" << endl;
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

void Block::loadImageForType(int type) {
	switch(type) {
	case BLOCK_CENTER:
		image = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("blockcenter.png"));
		break;
	case BLOCK_CORNER:
		image = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("blockcorner.png"));
		break;
	case BLOCK_EDGE:
		image = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("blockedge.png"));
		break;
	case BLOCK_FADE:
		image = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("blockfade.png"));
		break;
	}
}
Misc::Point Block::getMinSizeForType(int type) {
	// All types should have this min size
	return Point(GRID_SIZE_2, GRID_SIZE_2);
}
Misc::Point Block::getMaxSizeForType(int type) {
	switch(type) {
	case BLOCK_CENTER:
		return Point(GRID_SIZE_2 * 100, GRID_SIZE_2 * 100);
	case BLOCK_CORNER:
		return Point(GRID_SIZE_2, GRID_SIZE_2);
	case BLOCK_EDGE:
		return Point(GRID_SIZE_2 * 100, GRID_SIZE_2);
	case BLOCK_FADE:
		return Point(GRID_SIZE_2, GRID_SIZE_2 * 100);
	}
}
