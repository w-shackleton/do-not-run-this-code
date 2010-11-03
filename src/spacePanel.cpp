#include "spacePanel.hpp"

#include "objects/planet.hpp"
BEGIN_EVENT_TABLE(SpacePanel, CairoPanel)
	EVT_LEFT_DOWN(SpacePanel::mouseDown)
	EVT_MOTION(SpacePanel::mouseMoved)
	EVT_LEFT_UP(SpacePanel::mouseReleased)
/*	EVT_RIGHT_DOWN(SpacePanel::rightClick)
	EVT_LEAVE_WINDOW(SpacePanel::mouseLeftWindow)
	EVT_KEY_DOWN(SpacePanel::keyPressed)
	EVT_KEY_UP(SpacePanel::keyReleased)
	EVT_MOUSEWHEEL(SpacePanel::mouseWheelMoved)*/
END_EVENT_TABLE()

SpacePanel::SpacePanel(wxWindow *parent) :
	CairoPanel(parent),
	mousePrevPos(wxEVT_MOTION)
{
	objs.push_back(&Objects::Planet(10, 10, 60));
}

void SpacePanel::redraw(bool repaint)
{
	CairoPanel::redraw(repaint);
	cr->save();
	cr->set_source_rgb(0.5, 0.6, 0.7);
	cr->paint();

	cr->set_source_rgba(0.0, 0.0, 0.0, 0.7);
	cr->arc(200.0, 200.0, 
	50, 0.0, 1.7 * M_PI);
	cr->stroke();

	cr->set_source_rgba(1.0, 0.0, 0.0, 0.7);
	cr->move_to(100, 100);
	cr->line_to(250, 300);
	cr->stroke();
}

void SpacePanel::mouseDown(wxMouseEvent& event)
{
	sel= SEL_Bg_move;
	mousePrevPos = event;
}

void SpacePanel::mouseMoved(wxMouseEvent& event)
{
	if(sel == SEL_Bg_move)
	{
		wxPoint distMoved = event - mousePrevPos;
		matrix.transform(distMoved);
		redraw(true);
	}
	mousePrevPos = event;
}

void SpacePanel::mouseReleased(wxMouseEvent& event)
{
	sel= SEL_None;
}

wxPoint operator-(const wxMouseEvent& lhs, const wxMouseEvent& rhs)
{
	wxPoint temp;
	temp.x = lhs.m_x - rhs.m_x;
	temp.y = lhs.m_y - rhs.m_y;
	return temp;
}

