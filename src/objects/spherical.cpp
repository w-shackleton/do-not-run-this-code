#include "spherical.hpp"

using namespace Objects;

#include "../misc/geometry.hpp"

#include <iostream>
using namespace std;

Spherical::Spherical(double sx, double sy, double sradius) :
	SpaceItem(sx, sy),
	radius(sradius)
{
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
