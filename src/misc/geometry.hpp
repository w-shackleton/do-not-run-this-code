#ifndef GEOMETRY_H
#define GEOMETRY_H

#include <vector>

namespace Misc
{
	class Point
	{
		public:
			double x, y;
			Point(double x, double y);
			Point();
	};

	bool pointInPolygon(const std::vector<Point> points, const Point point);
}

#endif
