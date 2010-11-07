#include "spacePanel.hpp"

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

#include "objects/planet.hpp"
#include "objects/wall.hpp"

using namespace std;

SpacePanel::SpacePanel(wxWindow *parent) :
	CairoPanel(parent),
	mousePrevPos(wxEVT_MOTION)
{
	objs.push_back(new Objects::Planet(100, 100, 60));
	objs.push_back(new Objects::Wall(200, 200, 300, M_PI / 8 * 1));
}

SpacePanel::~SpacePanel()
{
	for(list<Objects::SpaceItem *>::iterator it = objs.begin(); it != objs.end(); it++)
	{
		cout << "Deleting SpaceItem " << *it << endl;
		delete *it;
	}
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

	// Draw objects
	for(list<Objects::SpaceItem *>::iterator it = objs.begin(); it != objs.end(); it++)
	{
		(*it)->draw(cr);
	}
}

void SpacePanel::mouseDown(wxMouseEvent& event)
{
	sel = SEL_None;
	// If clicked place is object
	double tx = event.m_x, ty = event.m_y;
	cout << " X: " << tx << ",  Y: " << ty << endl;
	matrix.get_inverse_matrix().transform_point(tx, ty);
	cout << "tX: " << tx << ", tY: " << ty << endl;
	for(list<Objects::SpaceItem *>::iterator it = objs.begin(); it != objs.end(); it++)
	{
		if((*it)->isClicked(tx, ty))
		{
			// Object is clicked.
			sel = SEL_Item_move;
			selectedItem = *it;
		}
	}
	if(sel == SEL_None)
		sel = SEL_Bg_move;
	mousePrevPos = event;
}

void SpacePanel::mouseMoved(wxMouseEvent& event)
{
	wxPoint distMoved = event - mousePrevPos;
	if(sel == SEL_Bg_move)
	{
		matrix.transform(distMoved);
		redraw(true);
	}
	else if(sel == SEL_Item_move)
	{
		selectedItem->move(distMoved.x, distMoved.y);
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

