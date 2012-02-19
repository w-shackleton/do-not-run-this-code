#include "wall.hpp"

using namespace Objects;
using namespace Cairo;

#define RECT_SIZE_Y GRID_SIZE_2
#define RECT_MIN_X 80
#define RECT_MAX_X 1200

#define IMG_SIZE_X 32
#define IMG_SIZE_Y 64

#include "../misc/data.hpp"

Wall::Wall(EditorCallbacks &callbacks, double x, double y, double sx, double rotation) :
	Rectangular(callbacks, x, y, sx, RECT_SIZE_Y, rotation, Misc::Point(RECT_MIN_X, RECT_SIZE_Y), Misc::Point(RECT_MAX_X, RECT_SIZE_Y)),
	hasEndsMenuItem(NULL),
	hasEnds(true) // True by default
{
	wallImage = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("walledge.png"));
	RefPtr<Surface> s = RefPtr<Surface>::cast_dynamic(wallImage);
	wallPattern = Cairo::SurfacePattern::create(s);
	wallPattern->set_extend(EXTEND_REPEAT);

	wallside = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("wallside.png"));
	hasEndsMenuItem = contextMenu->AppendCheckItem(contextMenuNextAvailableSlot++, _("&Has End parts"));
	hasEndsMenuItem->Check(hasEnds);
}

Wall::Wall(EditorCallbacks &callbacks, TiXmlElement &item) :
	Rectangular(callbacks, item, Misc::Point(RECT_MIN_X, RECT_SIZE_Y), Misc::Point(RECT_MAX_X, RECT_SIZE_Y)),
	hasEndsMenuItem(NULL),
	hasEnds(true) // True by default
{
	sy = RECT_SIZE_Y;
	item.QueryBoolAttribute("hasEnds", &hasEnds);

	wallImage = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("walledge.png"));
	RefPtr<Surface> s = RefPtr<Surface>::cast_dynamic(wallImage);
	wallPattern = Cairo::SurfacePattern::create(s);
	wallPattern->set_extend(EXTEND_REPEAT);

	wallside = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("wallside.png"));
	hasEndsMenuItem = contextMenu->AppendCheckItem(contextMenuNextAvailableSlot++, _("&Has End parts"));
	hasEndsMenuItem->Check(hasEnds);
}

void Wall::onCMenuItemClick(int id) {
	SpaceItem::onCMenuItemClick(id);
	switch(id) {
		case ID_CMenu_2:
			if(hasEndsMenuItem) {
				hasEnds = hasEndsMenuItem->IsChecked();
			}
			break;
	}
}

void Wall::saveXMLChild(TiXmlElement* item)
{
	Rectangular::saveXMLChild(item);
	item->SetAttribute("hasEnds", hasEnds);
}

void Wall::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(getX(), getY());
	cr->rotate(getRotation());
	cr->scale(.5, .5);

	wallPattern->set_matrix(translation_matrix(-getSX(), -getSY()));
	cr->set_source(wallPattern);
	cr->rectangle(-getSX(), -getSY(), getSX() * 2, getSY() * 2);
	cr->fill();

	if(hasEnds) {
		for(int i = 0; i < 2; i++) // draw on both sides by rotating round
		{
			cr->rotate(M_PI);
			cr->set_source(wallside, getSX(), -getSY());
			cr->rectangle( getSX(), -getSY(), IMG_SIZE_X, IMG_SIZE_Y);
			cr->fill();
		}
	}

	cr->scale(2, 2);
	cr->rotate(-getRotation());
	cr->translate(-getX(), -getY());
}
