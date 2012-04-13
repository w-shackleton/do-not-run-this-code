#include "blockpiece.hpp"

using namespace Objects;
using namespace std;
using namespace Misc;
using namespace Cairo;

#include "../misc/data.hpp"

Block::Block(EditorCallbacks &callbacks, double x, double y, double sx, double sy, int type) :
	Rectangular(callbacks, x, y, sx, sy, rotation, getMinSizeForType(type), getMaxSizeForType(type)),
	type(type),
	hasVortexMenuItem(NULL),
	hasVortex(true) // True by default
{
	loadImageForType(type);
	RefPtr<Surface> s = RefPtr<Surface>::cast_dynamic(image);
	pattern = Cairo::SurfacePattern::create(s);
	pattern->set_extend(EXTEND_REPEAT);

	setupContext();
}

Block::Block(EditorCallbacks &callbacks, TiXmlElement &item) :
	Rectangular(callbacks, item, getMinSizeForType(type), getMaxSizeForType(type)),
	hasVortexMenuItem(NULL),
	hasVortex(true)
{
	item.QueryIntAttribute("type", &type);
	item.QueryBoolAttribute("hasVortex", &hasVortex);
	loadImageForType(type);
	RefPtr<Surface> s = RefPtr<Surface>::cast_dynamic(image);
	pattern = Cairo::SurfacePattern::create(s);
	pattern->set_extend(EXTEND_REPEAT);

	setupContext();
}

void Block::setupContext() {
	switch(type) {
		case BLOCK_CENTER:
		case BLOCK_FADE:
			break;
		case BLOCK_EDGE:
		case BLOCK_CORNER:
			hasVortexMenuItem = contextMenu->AppendCheckItem(contextMenuNextAvailableSlot++, _("&Has vortex above surface"));
			hasVortexMenuItem->Check(hasVortex);
			break;
	}
}

void Block::onCMenuItemClick(int id) {
	SpaceItem::onCMenuItemClick(id);
	switch(id) {
		case ID_CMenu_2:
			if(hasVortexMenuItem) {
				hasVortex = hasVortexMenuItem->IsChecked();
			}
			break;
	}
}

void Block::saveXMLChild(TiXmlElement* item)
{
	Rectangular::saveXMLChild(item);
	item->SetAttribute("type", type);
	item->SetAttribute("hasVortex", hasVortex);
}

void Block::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(getX(), getY());
	cr->rotate(getRotation());
	cr->scale(.5, .5);

	pattern->set_matrix(translation_matrix(-getSX(), -getSY()));
	cr->set_source(pattern);
	cr->rectangle(-getSX(), -getSY(), getSX() * 2, getSY() * 2);
	cr->fill();

	cr->scale(2, 2);
	cr->rotate(-getRotation());
	cr->translate(-getX(), -getY());
}

void Block::loadImageForType(int type) {
	switch(type) {
	case BLOCK_CENTER:
		image = ImageSurface::create_from_png(Data::getFilePath("block/center.png"));
		break;
	case BLOCK_CORNER:
		image = ImageSurface::create_from_png(Data::getFilePath("block/corner1.png"));
		break;
	case BLOCK_EDGE:
		image = ImageSurface::create_from_png(Data::getFilePath("block/edge1.png"));
		break;
	case BLOCK_FADE:
		image = ImageSurface::create_from_png(Data::getFilePath("block/fade1.png"));
		break;
	case BLOCK_WALLJOIN1:
		image = ImageSurface::create_from_png(Data::getFilePath("block/walljoin1.png"));
		break;
	case BLOCK_WALLJOIN2:
		image = ImageSurface::create_from_png(Data::getFilePath("block/walljoin2.png"));
		break;
	case BLOCK_WALL_CORNER:
		image = ImageSurface::create_from_png(Data::getFilePath("block/wallcorner.png"));
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
		return Point(GRID_SIZE_2 * 100, GRID_SIZE_2);
	case BLOCK_WALLJOIN1:
	case BLOCK_WALLJOIN2:
	case BLOCK_WALL_CORNER:
		return Point(GRID_SIZE_2, GRID_SIZE_2);
	}
}
