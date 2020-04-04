#ifndef PHYLANDDLIST_H
#define PHYLANDDLIST_H

#include <wx/wx.h>
#include "header.h"

enum {
PLDLE_Rect = 1,
PLDLE_Circle,
PLDLE_Poly,
};

class PhyLandDListE
{
public:
	PhyLandDListE(double x, double y, double width, double height, double rot, wxColour col, double den, double res, double fric, bool fixed); // Rectangle
	PhyLandDListE(double x, double y, double radius, wxColour col, double den, double res, bool fixed); // Circle
	PhyLandDListE(double x, double y, wxPoint points[], int count, wxColour col, double den, double res, double fric, bool fixed); // Poly
	
	void polyComputeCentre(int xOffset, int yOffset);
	
	wxColour colour;
	double pointX, pointY;
	double rectSizeX, rectSizeY;
	double rotation;
	double circleRad;
	int itemType;
	
	double density, rest, friction;
	bool fix;
	int sortPos;
	
	wxPoint polyPoints[POLY_MAX_POINTS];
	int polyCount;
};

WX_DECLARE_LIST(PhyLandDListE, PhyLandDList);



PhyLandDListE* getDListItem(PhyLandDList list, int place);

#endif
