#include "levelWall.hpp"

using namespace Objects;

LevelWall::LevelWall(double sx, double sy) :
	SpaceItem(0, 0)
{
}

void LevelWall::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(x, y);

	cr->translate(-x, -y);
}

void LevelWall::scale(int r)
{
}

bool LevelWall::isBorderClicked(int cx, int cy)
{
	return false;
}

bool LevelWall::isClicked(int cx, int cy) { return false; }

void LevelWall::moveBorder(int dx, int dy)
{
}
