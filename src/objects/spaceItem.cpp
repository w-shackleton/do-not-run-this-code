#include "spaceItem.hpp"

using namespace Objects;
using namespace std;

SpaceItem::SpaceItem(double sx, double sy) :
	x(sx),
	y(sy),
	recycle(false),
	contextMenuNextAvailableSlot(ID_CMenu_1)
{
	contextMenu = new wxMenu;
	contextMenu->Append(contextMenuNextAvailableSlot++, _("&Delete"));
}

SpaceItem::SpaceItem(TiXmlElement &item) :
	recycle(false),
	contextMenuNextAvailableSlot(ID_CMenu_1)
{
	item.QueryDoubleAttribute("x", &x);
	item.QueryDoubleAttribute("y", &y);
	contextMenu = new wxMenu;
	contextMenu->Append(contextMenuNextAvailableSlot++, _("&Delete"));
}

SpaceItem::~SpaceItem()
{
	delete contextMenu;
}

void SpaceItem::move(double dx, double dy)
{
	x += dx;
	y += dy;
}

void SpaceItem::saveXML(TiXmlElement& parent)
{
	TiXmlElement *item = new TiXmlElement(getName());

	saveXMLChild(item);

	parent.LinkEndChild(item);
}

void SpaceItem::saveXMLChild(TiXmlElement* item)
{
	item->SetDoubleAttribute("x", x);
	item->SetDoubleAttribute("y", y);
}

void SpaceItem::onCMenuItemClick(int id)
{
	cout << "Menu item " << id << " clicked." << endl;
	switch(id)
	{
		case ID_CMenu_1:
			recycle = true;
			return;
	}
}
