#include "PhyLandPEArea.h"
#include <iostream>
#include "statfuncs.h"
using namespace std;

BEGIN_EVENT_TABLE(PhyLandPEArea, wxPanel)
 
	EVT_MOTION(PhyLandPEArea::mouseMoved)
	EVT_LEFT_DOWN(PhyLandPEArea::mouseDown)
	EVT_LEFT_UP(PhyLandPEArea::mouseReleased)
	EVT_LEFT_DCLICK(PhyLandPEArea::mouseDClick)
	EVT_MIDDLE_DOWN(PhyLandPEArea::mouseMDown)
	EVT_MIDDLE_UP(PhyLandPEArea::mouseMReleased)
	EVT_RIGHT_DOWN(PhyLandPEArea::rightClick)
	EVT_LEAVE_WINDOW(PhyLandPEArea::mouseLeftWindow)
	EVT_KEY_DOWN(PhyLandPEArea::keyPressed)
	EVT_KEY_UP(PhyLandPEArea::keyReleased)
	EVT_MOUSEWHEEL(PhyLandPEArea::mouseWheelMoved)
 
	EVT_PAINT(PhyLandPEArea::paintEvent)
	EVT_ERASE_BACKGROUND(PhyLandPEArea::OnEraseBackGround)

 
END_EVENT_TABLE()

PhyLandPEArea::PhyLandPEArea(wxWindow* parent)
: wxPanel(parent)
{
	editing = false;
	currPoint = 0;
	currSelected = 0;
}

PhyLandPEArea::PhyLandPEArea(wxWindow* parent, wxPoint existing[], int count)
: wxPanel(parent)
{
	editing = true;
	currPoint = count;
	for(int i = 0; i < count; points[i] = existing[i++]);
	cout << "H" << endl;
}

void PhyLandPEArea::mouseMoved(wxMouseEvent& event)
{
	if(!editing)
	{
		if(currPoint != POLY_MAX_POINTS)
		{
			points[currPoint] = wxPoint(event.m_x, event.m_y);
			Refresh();
		}
	}
	else
	{
		if(event.LeftIsDown() & currSelected != 0									)
		{
			points[currSelected - 1] = event.GetPosition();
			Refresh();
		}
	}
}
void PhyLandPEArea::mouseWheelMoved(wxMouseEvent& event) {}

void PhyLandPEArea::mouseDown(wxMouseEvent& event)
{
	if(!editing)
	{
		points[currPoint] = wxPoint(event.m_x, event.m_y);
		points[currPoint].y = event.m_y;
		cout << "Point " << currPoint + 1 << ", X: " << points[currPoint].x << ", Y: " << points[currPoint].y << endl;
		currPoint++;
		
		if(currPoint != POLY_MAX_POINTS)
			points[currPoint] = wxPoint(event.m_x, event.m_y);
		else
			editing = true;
		Refresh();
	}
	else
	{
		CHECKPOINTERPOS(col,event);
		if(col.Red() == 255 &&
		   col.Green() == 255 &&
		   col.Blue() == 255)
		{
			currSelected = 0;
		}
		else
		{
			if(col.Green() == 1)
			{
				for(int i = currPoint - 1; i > col.Red(); i--)
				{
					points[i + 1] = points[i];
				}
				points[col.Red() + 1] = event.GetPosition();
				currPoint++;
				currSelected = col.Red() + 2;
			}
			else if(col.Green() == 2)
			{
				currSelected = col.Red() + 1;
			}
		}
		Refresh();
		Msg->sendMsg(MSG_Poly_Inv);
	}
}

void PhyLandPEArea::mouseReleased(wxMouseEvent& event) {}

void PhyLandPEArea::mouseDClick(wxMouseEvent& event)
{
	editing = true;
	Refresh();
}

void PhyLandPEArea::mouseMDown(wxMouseEvent& event) {}
void PhyLandPEArea::mouseMReleased(wxMouseEvent& event) {}
void PhyLandPEArea::rightClick(wxMouseEvent& event) {}
void PhyLandPEArea::mouseLeftWindow(wxMouseEvent& event) {}
void PhyLandPEArea::keyPressed(wxKeyEvent& event) {}
void PhyLandPEArea::keyReleased(wxKeyEvent& event) {}
void PhyLandPEArea::OnEraseBackGround(wxEraseEvent& event) {}

void PhyLandPEArea::paintEvent(wxPaintEvent & evt)
{
	//wxMemoryDC mapDC;
	wxAutoBufferedPaintDC dc(this);
	render(dc, false);
}

void PhyLandPEArea::paintNow()
{
	//wxMemoryDC mapDC;
	wxClientDC dc(this);
	render(dc, false);
}

wxDC& PhyLandPEArea::render(wxDC& dc, bool clickRender)
{
	wxCoord x, y;
	dc.GetSize(&x, &y);
	currSizeX = x;
	currSizeY = y;
	
	dc.SetPen(wxPen(wxColor(255, 255, 255), 1));
	dc.DrawRectangle(0, 0, x, y);
	if(!clickRender)
	{
		dc.SetPen(wxPen(wxColor(0, 0, 128), 1));
		dc.SetBrush(wxBrush(wxColor(128, 128, 255)));
		if(!editing)
			dc.DrawPolygon(currPoint == POLY_MAX_POINTS ? POLY_MAX_POINTS : currPoint + 1, points, 0, 0, wxODDEVEN_RULE);
		else
		{
			dc.DrawPolygon(currPoint, points, 0, 0, wxODDEVEN_RULE);
			
			dc.SetPen(wxPen(wxColor(0, 0, 128), 1));
			dc.SetBrush(wxBrush(wxColor(0, 0, 128)));
			for(int i = 0; i < currPoint; i++)
				dc.DrawCircle(points[i], 4);
		}
	}
	else
	{
		if(currPoint != POLY_MAX_POINTS)
		{
			for(int i = 0; i < currPoint; i++)
			{
				dc.SetPen(wxPen(wxColor(i, 1, 0), 6));
				dc.DrawLine(
					points[i].x,
					points[i].y,
					points[(i + 1) % currPoint].x,
					points[(i + 1) % currPoint].y);
			}
		}
		for(int i = 0; i < currPoint; i++)
		{
			dc.SetPen(wxPen(wxColor(i, 2, 0), 0));
			dc.SetBrush(wxBrush(wxColor(i, 2, 0)));
			dc.DrawCircle(points[i], 7);
		}
	}
}

void PhyLandPEArea::validate()
{
	if(editing)
	{
		//Get rid of vertical / horizontal lines
		for(int i = 0; i < currPoint; i++)
		{
			for(int j = 0; j < currPoint; j++)
			{
				if((i != j) & (points[i].x == points[j].x))
					points[j].x++;
			}
		}
		
		// Find left hand 
		int lowestX = currSizeX;
		int lowestID = currPoint;
		for(int i = 0; i < currPoint; i++)
		{
			if(points[i].x < lowestX)
			{
				lowestX = points[i].x;
				lowestID = i;
			}
		}
		//cout << lowestX << endl;
		
		// Main loop
		bool finding = true;
		bool Found[currPoint];
		for(int i = 0; i < currPoint; Found[i++] = false);
		wxPoint newPoints[currPoint];
		
		newPoints[0] = points[lowestID];
		Found[lowestID] = true;
		int totFound = 1;
		
		while(finding)
		{
			finding = false;
			for(int i = 0; i < currPoint; i++)
			{
				//cout << endl;
				//cout << "Found[" << i << "]=" << (Found[i] ? "True" : "False") << endl;
				if(Found[i] == false)
				{
					// Each combo here between newPoints[totFound - 1] and points[i]
					// When found, set i to currPoint to exit loop.
					// y intercept = y value - x * gradient
					//cout << "Points: nP=(" << newPoints[totFound - 1].x << "," << newPoints[totFound - 1].y << "), p=(" << points[i].x << "," << points[i].y << ")" << endl;
					float grad = 
						((float)(points[i].y - newPoints[totFound - 1].y)) / 
						((float)(points[i].x - newPoints[totFound - 1].x));
					//cout << "i: " << i << endl;
					//cout << "dy: " << ((float)(points[i].y - newPoints[totFound - 1].y)) << endl;
					//cout << "dx: " << ((float)(points[i].x - newPoints[totFound - 1].x)) << endl;
					//cout << "Gradient: " << grad << endl;
					int yInter = points[i].y - (points[i].x * grad);
					//cout << "Y Intercept: " << yInter << ", Grad: " << grad << endl;
					
					bool allLess = true;
					for(int j = 0; j < currPoint; j++)
					{
						int cYInter = points[j].y - (points[j].x * grad);
						if(cYInter > yInter)
						{
							allLess = false;
							j = currPoint;
						}
					}
					
					bool allGreater = true;
					for(int j = 0; j < currPoint; j++)
					{
						int cYInter = points[j].y - (points[j].x * grad);
						if(cYInter < yInter)
						{
							allGreater = false;
							j = currPoint;
						}
					}
					
					if(allGreater | allLess)
					{
					//	cout << "Outside at i=" << i << ", tF=" << totFound << endl;
						totFound++;
						newPoints[totFound - 1] = points[i];
						Found[i] = true;
					//	cout << "Found[" << i << "] set to " << (Found[i] ? "True" : "False");
						i = currPoint; // Cancel loop << endl;
						finding = true; // Continue outer loop
					}
				}
			}
		}
		currPoint = totFound;
		for(int i = 0; i < totFound; i++)
		{
			points[i] = newPoints[i];
		}
		//cout << "Done!" << endl;
		Refresh();
		Msg->sendMsg(MSG_Poly_Val);
	}
}

void PhyLandPEArea::receiveMsgHandle(MsgSend *msg)
{
	Msg = msg;
}
