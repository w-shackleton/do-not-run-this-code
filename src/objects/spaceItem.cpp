#include "spaceItem.hpp"

SpaceItem::SpaceItem(double sx, double sy) :
	x(sx),
	y(sy)
{
}

void SpaceItem::move(int dx, int dy)
{
	x += dx;
	y += dy;
}
