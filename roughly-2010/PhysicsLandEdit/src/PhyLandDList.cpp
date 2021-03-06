#include "PhyLandDList.h"
#include <iostream>
using namespace std;

PhyLandDListE::PhyLandDListE(double x, double y, double width, double height, double rot, wxColour col, double den, double res, double fric, bool fixed)
{
	pointX = x;
	pointY = y;
	rectSizeX = width;
	rectSizeY = height;
	rotation = rot;
	colour = col;
	itemType = PLDLE_Rect;
	
	density = den;
	rest = res;
	friction = fric;
	fix = fixed;
	
	sortPos = 0;
}

PhyLandDListE::PhyLandDListE(double x, double y, double radius, wxColour col, double den, double res, bool fixed)
{
	pointX = x;
	pointY = y;
	colour = col;
	circleRad = radius;
	itemType = PLDLE_Circle;
	
	density = den;
	rest = res;
	fix = fixed;
	
	sortPos = 0;
	
	
}

PhyLandDListE::PhyLandDListE(double x, double y, wxPoint points[], int count, wxColour col, double den, double res, double fric, bool fixed)
{
	pointX = x;
	pointY = y;
	colour = col;
	
	itemType = PLDLE_Poly;
	
	density = den;
	rest = res;
	friction = fric;
	fix = fixed;
	
	sortPos = 0;
	
	for(int i = 0; i < count; i++)
	{
		polyPoints[i] = points[i];
	}
	polyCount = count;
	
	polyComputeCentre(0, 0);
}

void PhyLandDListE::polyComputeCentre(int xOffset, int yOffset)
{
	int Xlow = polyPoints[0].x;
	for(int i = 0; i < polyCount; i++)
	{
		if(polyPoints[i].x < Xlow)
			Xlow = polyPoints[i].x;
	}
	
	int Ylow = polyPoints[0].y;
	for(int i = 0; i < polyCount; i++)
	{
		if(polyPoints[i].y < Ylow)
			Ylow = polyPoints[i].y;
	}
	
	int Xtop = 0;
	for(int i = 0; i < polyCount; i++)
	{
		if(polyPoints[i].x > Xtop)
			Xtop = polyPoints[i].x;
	}
	
	int Ytop = 0;
	for(int i = 0; i < polyCount; i++)
	{
		if(polyPoints[i].y > Ytop)
			Ytop = polyPoints[i].y;
	}
	//cout << "Min: (" << Xlow << "," << Ylow << "), Top: (" << Xtop << "," << Ytop << ")" << endl;
	
	for(int i = 0; i < polyCount; i++)
	{
		polyPoints[i].x -= (Xtop + Xlow) / 2 - xOffset;
		polyPoints[i].y -= (Ytop + Ylow) / 2 - yOffset;
	}
}

#include <wx/listimpl.cpp>
WX_DEFINE_LIST(PhyLandDList);


PhyLandDListE* getDListItem(PhyLandDList list, int place)
{
	PhyLandDList::iterator elem;
	//cout << "     a" << place << endl;
	elem = list.begin();
	//cout << "     s" << &list << endl;
	for(int i = 0; i < place; i++)
		elem++;
	
	//cout << "     d" << endl;
	return *elem;
}
