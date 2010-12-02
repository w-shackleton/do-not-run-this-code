#include "spaceItem.hpp"

using namespace Objects;

SpaceItem::SpaceItem(double sx, double sy) :
	x(sx),
	y(sy)
{
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
