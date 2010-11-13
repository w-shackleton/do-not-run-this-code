#include "spacePanel.hpp"

BEGIN_EVENT_TABLE(SpacePanel, CairoPanel)
	EVT_LEFT_DOWN(SpacePanel::mouseDown)
	EVT_MOTION(SpacePanel::mouseMoved)
	EVT_LEFT_UP(SpacePanel::mouseReleased)
/*	EVT_RIGHT_DOWN(SpacePanel::rightClick)
	EVT_LEAVE_WINDOW(SpacePanel::mouseLeftWindow)
	EVT_KEY_DOWN(SpacePanel::keyPressed)
	EVT_KEY_UP(SpacePanel::keyReleased)*/
	EVT_MOUSEWHEEL(SpacePanel::mouseWheelMoved)

	EVT_CONTEXT_MENU(SpacePanel::contextMenu)

	EVT_MENU(SpacePanel::ID_Object_delete, SpacePanel::onObjectMenuDelete)
END_EVENT_TABLE()

#include "objects/planet.hpp"
#include "objects/wall.hpp"
#include "objects/levelWall.hpp"

using namespace std;

SpacePanel::SpacePanel(wxWindow *parent) :
	CairoPanel(parent),
	mousePrevPos(wxEVT_MOTION)
{
	objs.push_back(new Objects::LevelWall(100, 100));
	objs.push_back(new Objects::Planet(100, 100, 60));
	objs.push_back(new Objects::Wall(200, 200, 300, M_PI / 8 * 1));

	objectMenu = new wxMenu;
	bgMenu = new wxMenu;

	objectMenu->Append(ID_Object_delete, _("&Delete"));
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

int SpacePanel::getClickedObject(double x, double y, bool useBorder)
{
	// If clicked place is object
	double tx = x, ty = y;
	matrix.get_inverse_matrix().transform_point(tx, ty);

	selectedItem = NULL;
	int ret = CLICKED_None;
	for(list<Objects::SpaceItem *>::iterator it = objs.begin(); it != objs.end(); it++)
	{
		if((*it)->isClicked(tx, ty))
		{
			// Object is clicked.
			selectedItem = *it;
			ret = CLICKED_Inner;
			continue;
		}
		if(useBorder)
			if((*it)->isBorderClicked(tx, ty))
		{
			// Object border is clicked.
			selectedItem = *it;
			ret = CLICKED_Border;
			continue;
		}
	}
	return ret;
}

void SpacePanel::mouseDown(wxMouseEvent& event)
{
	if(event.m_shiftDown) // shift means apply to bg
		sel = SEL_Bg_move;
	else
	{
		int clickType = getClickedObject(event.m_x, event.m_y, true);

		if(clickType == CLICKED_Inner)
			sel = SEL_Item_move;
		else if(clickType == CLICKED_Border)
			sel = SEL_Item_border_move;
		else
			sel = SEL_Bg_move;
	}
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
		distMoved.x /= matrix.sx;
		distMoved.y /= matrix.sy;
		selectedItem->move(distMoved.x, distMoved.y);
		redraw(true);
	}
	else if(sel == SEL_Item_border_move)
	{
		double tx = event.m_x, ty = event.m_y;
		matrix.get_inverse_matrix().transform_point(tx, ty);

		selectedItem->moveBorder(tx, ty);
		redraw(true);
	}
	mousePrevPos = event;
}

void SpacePanel::mouseReleased(wxMouseEvent& event)
{
	sel= SEL_None;
}

void SpacePanel::mouseWheelMoved(wxMouseEvent& event)
{
	if(sel == SEL_None) // To make sure not clicking something
	{
		if(event.m_shiftDown) // shift means apply to bg
		{
			matrix.scale_rotation(event.m_wheelRotation);
		}
		else if(getClickedObject(event.m_x, event.m_y, false) == CLICKED_Inner)
		{
			selectedItem->scale(event.m_wheelRotation);
		}
		else
		{
			matrix.scale_rotation(event.m_wheelRotation);
		}
		redraw(true);
	}
}

void SpacePanel::contextMenu(wxContextMenuEvent& event)
{
	wxPoint point = ScreenToClient(event.GetPosition());
	if(getClickedObject(point.x, point.y, false) == CLICKED_Inner)
	{
		PopupMenu(objectMenu);
	}
	else
	{
		PopupMenu(bgMenu);
	}
//	redraw(true);
}

wxPoint operator-(const wxMouseEvent& lhs, const wxMouseEvent& rhs)
{
	wxPoint temp;
	temp.x = lhs.m_x - rhs.m_x;
	temp.y = lhs.m_y - rhs.m_y;
	return temp;
}

void SpacePanel::onObjectMenuDelete(wxCommandEvent& WXUNUSED(event))
{
	cout << "Deleting item..." << endl;

	objs.remove(selectedItem);

	redraw(true);
}
