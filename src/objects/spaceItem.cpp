#include "spaceItem.hpp"

using namespace Objects;

SpaceItem::SpaceItem(double sx, double sy) :
	x(sx),
	y(sy)
{
}

SpaceItem::SpaceItem(TiXmlElement &item)
{
	item.QueryDoubleAttribute("x", &x);
	item.QueryDoubleAttribute("y", &y);
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
