#ifndef GEOMETRY_H
#define GEOMETRY_H

#include <vector>
#include <cmath>

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

	static inline double hypotenuse(double x, double y)
	{
		return sqrt(x * x + y * y);
	}
}

#endif
