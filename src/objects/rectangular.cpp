#include "rectangular.hpp"

using namespace Objects;
#include <iostream>
using namespace std;

Rectangular::Rectangular(double x, double y, double sx, double sy, double rotation) :
	SpaceItem(x, y),
	sx(sx),
	sy(sy),
	rotation(rotation)
{
	cornerMatrix = Cairo::identity_matrix();
}

bool Rectangular::isClicked(int cx, int cy)
{
	updateCornerPoints();
	return Misc::pointInPolygon(cornerPoints, Misc::Point(cx, cy));
}

void Rectangular::updateCornerMatrix()
{
	cornerMatrix = Cairo::identity_matrix();
	cornerMatrix.translate(x, y);
	cornerMatrix.rotate(rotation);
}

void Rectangular::updateCornerPoints()
{
	updateCornerMatrix();

	// Put points into vector
	cornerPoints.clear();
	cornerPoints.push_back(Misc::Point(- (sx / 2), - (sy / 2)));
	cornerPoints.push_back(Misc::Point(+ (sx / 2), - (sy / 2)));
	cornerPoints.push_back(Misc::Point(+ (sx / 2), + (sy / 2)));
	cornerPoints.push_back(Misc::Point(- (sx / 2), + (sy / 2)));

	//for(int i = 0; i < cornerPoints.size(); i++)
	//{
		//cout << "P" << i << ": " << cornerPoints[i].x << ", " << cornerPoints[i].y << endl;
	//}

	// Transform points
	cornerMatrix.transform_point(cornerPoints[0].x, cornerPoints[0].y);
	cornerMatrix.transform_point(cornerPoints[1].x, cornerPoints[1].y);
	cornerMatrix.transform_point(cornerPoints[2].x, cornerPoints[2].y);
	cornerMatrix.transform_point(cornerPoints[3].x, cornerPoints[3].y);
	//for(int i = 0; i < cornerPoints.size(); i++)
	//{
		//cout << "P" << i << ": " << cornerPoints[i].x << ", " << cornerPoints[i].y << endl;
	//}
}
