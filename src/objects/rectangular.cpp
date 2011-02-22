#include "rectangular.hpp"

using namespace Objects;
#include <iostream>
#include <cmath>
using namespace std;

Rectangular::Rectangular(EditorCallbacks &callbacks, double x, double y, double sx, double sy, double rotation, Misc::Point min, Misc::Point max) :
	SpaceItem(callbacks, x, y),
	sx(abs(sx)),
	sy(abs(sy)),
	rotation(rotation),
	min(min),
	max(max)
{
	cornerMatrix = Cairo::identity_matrix();
}

Rectangular::Rectangular(EditorCallbacks &callbacks, TiXmlElement &item, Misc::Point min, Misc::Point max) :
	SpaceItem(callbacks, item),
	min(min),
	max(max)
{
	cornerMatrix = Cairo::identity_matrix();
	item.QueryDoubleAttribute("sx", &sx);
	item.QueryDoubleAttribute("sy", &sy);
	item.QueryDoubleAttribute("rotation", &rotation);
	rotation *= M_PI / 180;
}

bool Rectangular::isClicked(int cx, int cy)
{
	updateCornerPoints();
	return Misc::pointInPolygon(cornerPoints, Misc::Point(cx, cy));
}
bool Rectangular::isBorderClicked(int cx, int cy)
{
	// Temporarily resize rectangle to check if border was clicked
	sx += BORDER_CLICK_SIZE;
	sy += BORDER_CLICK_SIZE;
	updateCornerPoints();
	bool inOut = Misc::pointInPolygon(cornerPoints, Misc::Point(cx, cy));

	sx -= BORDER_CLICK_SIZE * 2;
	sy -= BORDER_CLICK_SIZE * 2;
	updateCornerPoints();
	bool inIn = Misc::pointInPolygon(cornerPoints, Misc::Point(cx, cy));

	sx += BORDER_CLICK_SIZE;
	sy += BORDER_CLICK_SIZE;

	borderSelectedType = EdgeX_selected;

	updateCornerPoints();

	double px = cx, py = cy;
	for(int i = 0; i < cornerPoints.size(); i++)
	{
		if(Misc::hypotenuse(cornerPoints[i].x - px, cornerPoints[i].y - py) < BORDER_CLICK_SIZE * 2) // Inside corner, x2 to add a bit of slack for the user
		{
			borderSelectedType = Corner_selected;
			// borderCornerMoveOriginalPos = Misc::Point(x, y);
		}
	}

	if(borderSelectedType == EdgeX_selected)
	{
		updateCornerMatrix(); // Get inverse Matrix
		cornerMatrix.invert();
		cornerMatrix.transform_point(px, py);

		double hy = abs(py / sy);
		double hx = abs(px / sx);
		if(abs(py / sy) > abs(px / sx))
		{
			// Y is greater, therefore selecting horizontal edge.
			borderSelectedType = EdgeY_selected;
		}
	}

	if(inOut)
		if(!inIn)
			return true;
	return false;
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

void Rectangular::saveXMLChild(TiXmlElement* item)
{
	SpaceItem::saveXMLChild(item);
	item->SetDoubleAttribute("sx", sx);
	item->SetDoubleAttribute("sy", sy);
	item->SetDoubleAttribute("rotation", rotation * 180 / M_PI);
}

void Rectangular::moveBorder(int dx, int dy)
{
	double px = dx, py = dy;

	updateCornerMatrix(); // Get inverse Matrix
	cornerMatrix.invert();
	cornerMatrix.transform_point(px, py);
		
	if(borderSelectedType == Corner_selected)
	{
		// TODO: Make move from corner, leaving other corner intact
		sx = px * 2;
		sy = py * 2;

		// Changed, so update
		updateCornerPoints();
	}
	else if(borderSelectedType == EdgeX_selected)
	{
		sx = px * 2;
	}
	else if(borderSelectedType == EdgeY_selected)
	{
		sy = py * 2;
	}
	sx = abs(sx);
	sy = abs(sy);
	Misc::trimMinMax(sx, min.x, max.x);
	Misc::trimMinMax(sy, min.y, max.y);
}

void Rectangular::scale(int r)
{
	if(r < 0)
	{
		sx += 20;
		sy += 20;
	}
	else if(r > 0)
	{
		sx -= 20;
		sy -= 20;
	}
	Misc::trimMinMax(sx, min.x, max.x);
	Misc::trimMinMax(sy, min.y, max.y);
}

void Rectangular::rotate(double r)
{
	rotation += r * ROTATION_MULTIPLIER;
}
