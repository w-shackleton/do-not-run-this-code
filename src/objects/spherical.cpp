#include "spherical.hpp"

using namespace Objects;

Spherical::Spherical(double sx, double sy, double sradius) :
	SpaceItem(sx, sy),
	radius(sradius)
{
}

bool Spherical::isClicked(int cx, int cy)
{
	return sqrt((x-cx) * (x-cx) + (y-cy)*(y-cy)) < radius;
}
