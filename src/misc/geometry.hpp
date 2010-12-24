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

	static inline void trimMinMax(double &num, double min, double max)
	{
		if(num < min) num = min;
		if(num > max) num = max;
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
