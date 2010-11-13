#include "geometry.hpp"

Misc::Point::Point(double x, double y) :
	x(x),
	y(y)
{
}

Misc::Point::Point()
{
}

bool Misc::pointInPolygon(const std::vector<Point> points, const Misc::Point point)
{
	int i, j;
	bool c = false;
	for (i = 0, j = points.size() - 1; i < points.size(); j = i++)
	{
		if (( ((points[i].y<=point.y) && (point.y<points[j].y)) || ((points[j].y<=point.y) && (point.y<points[i].y)) ) &&
				(point.x < (points[j].x - points[i].x) * (point.y - points[i].y) / (points[j].y - points[i].y) + points[i].x))
			c = !c;
	}
	return c;
}
