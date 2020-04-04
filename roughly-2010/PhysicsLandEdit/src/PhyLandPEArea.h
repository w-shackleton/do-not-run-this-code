#ifndef PHYLANDPEAREA_H
#define PHYLANDPEAREA_H

#include <wx/wx.h>
#include <wx/dcbuffer.h>
#include "header.h"
#include "MsgSend.h"

class PhyLandPEArea : public wxPanel
{
public:
	PhyLandPEArea(wxWindow* parent);
	PhyLandPEArea(wxWindow* parent, wxPoint existing[], int count);
	
	void mouseMoved(wxMouseEvent& event);
	void mouseWheelMoved(wxMouseEvent& event);
	void mouseDown(wxMouseEvent& event);
	void mouseReleased(wxMouseEvent& event);
	void mouseDClick(wxMouseEvent& event);
	void mouseMDown(wxMouseEvent& event);
	void mouseMReleased(wxMouseEvent& event);
	void rightClick(wxMouseEvent& event);
	void mouseLeftWindow(wxMouseEvent& event);
	void keyPressed(wxKeyEvent& event);
	void keyReleased(wxKeyEvent& event);
	void OnEraseBackGround(wxEraseEvent& event);
	
	void validate();
	int currPoint;
	
	void receiveMsgHandle(MsgSend* msg);
	
	wxPoint points[POLY_MAX_POINTS];
	
	DECLARE_EVENT_TABLE()
private:
	
	void paintEvent(wxPaintEvent & evt);
	void paintNow();
	
	wxDC& render(wxDC& dc, bool clickRender);
	
	int currSelected;
	bool editing;
	
	int currSizeX, currSizeY;
	
	MsgSend* Msg;
};
enum
{
	MSG_Poly_Inv,
	MSG_Poly_Val,
};

#endif
