#include "infobox.hpp"

using namespace Objects;

#define INFOBOX_SIZE_X (GRID_SIZE * 5)
#define INFOBOX_SIZE_Y (GRID_SIZE * 4)

#include "../misc/data.hpp"

#include "infoboxEditor.hpp"
using namespace Objects::Helpers;

#include <iostream>
using namespace std;

InfoBox::InfoBox(EditorCallbacks &callbacks, double x, double y, double rotation, std::string text, bool initialShow) :
	Rectangular(callbacks, x, y, INFOBOX_SIZE_X, INFOBOX_SIZE_Y, rotation, Misc::Point(INFOBOX_SIZE_X, INFOBOX_SIZE_Y), Misc::Point(INFOBOX_SIZE_X, INFOBOX_SIZE_Y)),
	text(text),
	initialShow(initialShow)
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("message.png"));
	contextMenu->Append(contextMenuNextAvailableSlot++, _("&Edit"));
}

InfoBox::InfoBox(EditorCallbacks &callbacks, TiXmlElement &item) :
	Rectangular(callbacks, item, Misc::Point(INFOBOX_SIZE_X, INFOBOX_SIZE_Y), Misc::Point(INFOBOX_SIZE_X, INFOBOX_SIZE_Y))
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("message.png"));
	contextMenu->Append(contextMenuNextAvailableSlot++, _("&Edit"));
	text = item.GetText() == NULL ? "" : item.GetText();
	initialShow = item.Attribute("initialshow") == "true";
	sx = INFOBOX_SIZE_X;
	sy = INFOBOX_SIZE_Y;
}

void InfoBox::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(getX(), getY());
	cr->rotate(rotation);
	cr->scale(.5, .5);

	//cr->set_source(img, -(sx / 2), -(sy / 2)); // Old - now using scale to make image better quality
	//cr->rectangle( - (sx / 2),  - (sy / 2), sx, sy);
	//cr->fill();

	cr->set_source(img, -getSX(), -getSY());
	cr->rectangle(-getSX(), -getSY(), getSX() * 2, getSY() * 2); cr->fill();

	cr->scale(2, 2);
	cr->rotate(-rotation);
	cr->translate(-getX(), -getY());
}

void InfoBox::saveXMLChild(TiXmlElement* item)
{
	Rectangular::saveXMLChild(item);

	TiXmlText *xtext = new TiXmlText(text);
	item->LinkEndChild(xtext);

	item->SetAttribute("initialshow", initialShow ? "true" : "false");
}

void InfoBox::onCMenuItemClick(int id)
{
	SpaceItem::onCMenuItemClick(id);
	switch(id)
	{
		case ID_CMenu_2:
			cout << "Editing..." << endl;
			InfoBoxEditor editor(NULL, text, initialShow);
			editor.ShowModal();
			return;
	}
}
