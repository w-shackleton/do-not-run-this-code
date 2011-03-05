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

			Point operator -(Point& second);

			// Converts to a hypotenuse
			operator double() const;
	};

	bool pointInPolygon(const std::vector<Point> points, const Point point);

	static inline double hypotenuse(double x, double y)
	{
		return sqrt(x * x + y * y);
	}

	static inline void trimMinMax(double &num, double min, double max)
	{
		if(num < min) num = min;
		if(num > max) num = max;
	}

	static inline double distance(double x1, double y1, double x2, double y2)
	{
		return sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	/*
	   These colours go from 0 to 1, to comply with cairomm
	   */
	typedef struct Colour
	{
		Colour(double r, double g, double b);
		Colour(bool none = false);
		double r, g, b;
		bool noColour;
	} Colour;
}

#endif
