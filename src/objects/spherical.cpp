#include "spherical.hpp"

using namespace Objects;

#include "../misc/geometry.hpp"

#include <iostream>
using namespace std;

// TODO: Implement optional resize (like in rectangular.cpp)
Spherical::Spherical(double sx, double sy, double sradius) :
	SpaceItem(sx, sy),
	radius(sradius)
{
}

void Spherical::saveXMLChild(TiXmlElement* item)
{
	SpaceItem::saveXMLChild(item);
	item->SetDoubleAttribute("radius", radius);
}

bool Spherical::isClicked(int cx, int cy)
{
	return Misc::hypotenuse(x - cx, y - cy) < radius;
}

bool Spherical::isBorderClicked(int cx, int cy)
{
	float distanceFromCentre = Misc::hypotenuse(x - cx, y - cy);
	if(distanceFromCentre < radius + BORDER_CLICK_SIZE)
		if(distanceFromCentre > radius - BORDER_CLICK_SIZE)
			return true;
	return false;
}

void Spherical::moveBorder(int dx, int dy)
{
	radius = Misc::hypotenuse(x - dx, y - dy);
}

void Spherical::rotate(double r) // In RADIANS
{
	// for(int i = 0; i < 0; i++)
	// {
	//	Because less is more!
	// }
}
