#ifndef PHYLANDAREA_H
#define PHYLANDAREA_H

#include "header.h"
#include "wx/wx.h"
#include <wx/dcbuffer.h>
#include <wx/sizer.h>
#include "PhyLandDList.h"
#include "PhyLandImgRes.h"
#include "PhyLandJointList.h"
#include "MsgSend.h"


/* This program can take up to about 8 million objects and 8 million joints IN THEORY,
 * but since this limit is so unreasonable, I haven't placed any limits in the code.
 * 8m ≈ 256 * 256 * 128
 * This is due to the algorithm to find the clicked object that assigns an individual colour
 * to each object, and finds the colour of the selected object,
 * with nextColId(...) and getNextCol(...) in statfuncs.cpp
 */


class PhyLandArea : public wxPanel
{

	//bool pressedDown;
	//wxString text;
	
	//static const int buttonWidth = 200;
	//static const int buttonHeight = 50;
	//PhyLandDList itemList;
	
public:
	PhyLandDListE* currItem;
	PhyLandDList itemList;
	int currSelected;
	
	PhyLandArea(wxFrame* parent);
	
	void paintEvent(wxPaintEvent & evt);
	void paintNow();
	
	wxDC& render(wxDC& dc, bool clickRender);
	void refresh();
	
	// some useful events
	void mouseMoved(wxMouseEvent& event);
	void mouseWheelMoved(wxMouseEvent& event);
	void mouseDown(wxMouseEvent& event);
	void mouseReleased(wxMouseEvent& event);
	void mouseMDown(wxMouseEvent& event);
	void mouseMReleased(wxMouseEvent& event);
	void rightClick(wxMouseEvent& event);
	void mouseLeftWindow(wxMouseEvent& event);
	void keyPressed(wxKeyEvent& event);
	void keyReleased(wxKeyEvent& event);
	void OnEraseBackGround(wxEraseEvent& event);
	
	void receiveMsgHandle(MsgSend* msg);
	
	void addCircle();
	void addRect();
	void addPoly(wxPoint points[], int count);
	void addjDist();
	
	DECLARE_EVENT_TABLE()
private:
	void SendEvent();
	int currSizeX, currSizeY;
	int shift_x, shift_y;
	int shift_dx, shift_dy;
	
	
	MsgSend* Msg;
	enum
	{
		DRAW_None,
		DRAW_Rotat,
		DRAW_Rect,
		DRAW_Circ,
		DRAW_Joint_Dist,
		DRAW_Joint_Dist2,
	};
	int drawState;
	int currCreating;
	
	PhyLandImgResc resc;
	
	int currRotating;
	PhyLandDListE* currRItem;
	
	int jointTot;
	PhyLandJointList jointList;
	PhyLandJointListI* currJoint;
	
	int mouse_x;
	int mouse_y;
};

static int sortComp(const PhyLandDListE** elem1, const PhyLandDListE** elem2);
#endif
