#include "spherical.hpp"

Spherical::Spherical(double sx, double sy, double sradius) :
	SpaceItem(sx, sy),
	radius(sradius)
{
}

bool Spherical::isClicked(int cx, int cy)
{
	return sqrt((x-cx) * (x-cx) + (y-cy)*(y-cy)) < radius;
}
