#include "spacePanel.hpp"

BEGIN_EVENT_TABLE(SpacePanel, CairoPanel)
	EVT_LEFT_DOWN(SpacePanel::mouseDown)
	EVT_MOTION(SpacePanel::mouseMoved)
	EVT_LEFT_UP(SpacePanel::mouseReleased)
	EVT_MIDDLE_DOWN(SpacePanel::middleDown)
	EVT_MIDDLE_UP(SpacePanel::mouseReleased)
/*	EVT_RIGHT_DOWN(SpacePanel::rightClick)
	EVT_LEAVE_WINDOW(SpacePanel::mouseLeftWindow)
	EVT_KEY_DOWN(SpacePanel::keyPressed)
	EVT_KEY_UP(SpacePanel::keyReleased)*/
	EVT_MOUSEWHEEL(SpacePanel::mouseWheelMoved)

	EVT_CONTEXT_MENU(SpacePanel::contextMenu)

	EVT_MENU(SpacePanel::ID_Object_delete, SpacePanel::onObjectMenuDelete)
END_EVENT_TABLE()

using namespace std;
using namespace Levels;

// TODO: Add a whole stack of documentation to this!

#define STARFIELD_SIZE 2000
#define STARFIELD_STARS 1000

SpacePanel::SpacePanel(wxWindow *parent, LevelManager &lmanager) :
	CairoPanel(parent),
	mousePrevPos(wxEVT_MOTION),
	lmanager(lmanager)
{
	for(int i = 0; i < STARFIELD_STARS; i++)
	{
		stars.push_back(wxPoint(rand() % (STARFIELD_SIZE * 2) - STARFIELD_SIZE, rand() % (STARFIELD_SIZE * 2) - STARFIELD_SIZE));
	}

	objectMenu = new wxMenu;
	bgMenu = new wxMenu;

	objectMenu->Append(ID_Object_delete, _("&Delete"));

	redraw(true, true);
}

SpacePanel::~SpacePanel()
{
}

void SpacePanel::redraw_draw()
{
	//cr->set_antialias(Cairo::ANTIALIAS_NONE);

	cr->save();
	cr->set_source_rgb(0, 0, 0);
	cr->paint();

	// Draw stars
	cr->translate(-matrix.tx / 1.5, -matrix.ty / 1.5); // Give feeling of stars in background

	cr->set_source_rgb(1, 1, 1);
	cr->set_line_width(1 / matrix.sx);
	for(vector<wxPoint>::iterator it = stars.begin(); it != stars.end(); it++)
	{
		cr->move_to(it->x, it->y);
		cr->line_to(it->x + 1, it->y + 1);
		cr->stroke();
	}
	cr->translate(matrix.tx / 1.5, matrix.ty / 1.5);

	cr->set_line_width(2);

	// Draw objects
	for(list<Objects::SpaceItem *>::iterator it = lmanager.objs.begin(); it != lmanager.objs.end(); it++)
	{
		(*it)->draw(cr);
	}

	cr->translate(-matrix.tx / matrix.sx, -matrix.ty / matrix.sy);

	// Draw screen size hinter
	cr->set_source_rgba(0.9, 0.9, 0.9, 0.1);
	cr->rectangle(0, 0, PHONE_SCREEN_X, PHONE_SCREEN_Y);
	cr->fill();

	double dashesData[] = {2, 2};
	vector<double> dashes(dashesData, dashesData + sizeof(dashesData) / sizeof(double));
	cr->set_dash(dashes, 0);

	cr->set_source_rgba(1, 1, 1, 0.3);
	cr->rectangle(0, 0, PHONE_SCREEN_X, PHONE_SCREEN_Y);
	cr->stroke();

	vector<double> blankDashes(0, 0);
	cr->set_dash(blankDashes, 0);
}

int SpacePanel::getClickedObject(double x, double y, bool useBorder)
{
	// Level changed
	lmanager.change();

	// If clicked place is object
	double tx = x, ty = y;
	matrix.get_inverse_matrix().transform_point(tx, ty);

	selectedItem = NULL;
	int ret = CLICKED_None;
	for(list<Objects::SpaceItem *>::iterator it = lmanager.objs.begin(); it != lmanager.objs.end(); it++)
	{
		if(useBorder)
			if((*it)->isBorderClicked(tx, ty))
		{
			// Object border is clicked.
			selectedItem = *it;
			ret = CLICKED_Border;
			continue;
		}
		if((*it)->isClicked(tx, ty))
		{
			// Object is clicked.
			selectedItem = *it;
			ret = CLICKED_Inner;
			continue;
		}
	}
	return ret;
}

void SpacePanel::mouseDown(wxMouseEvent& event)
{
	if(event.m_shiftDown) // shift means apply to bg
		sel = SEL_Bg_move;
	else if(event.m_controlDown)
	{
		sel = SEL_Item_rotate;
		getClickedObject(event.m_x, event.m_y, false);
	}
	else
	{
		switch(getClickedObject(event.m_x, event.m_y, true))
		{

			case CLICKED_Inner:
				sel = SEL_Item_move;
				break;
			case CLICKED_Border:
				sel = SEL_Item_border_move;
				break;
			default:
				sel = SEL_Bg_move;
				break;
		}
	}
	mousePrevPos = event;
}

void SpacePanel::mouseMoved(wxMouseEvent& event)
{
	wxRealPoint distMoved = event - mousePrevPos;
	double tx = event.m_x, ty = event.m_y;
	switch(sel)
	{
		case SEL_Bg_move:
			matrix.transform(distMoved);
			redraw(true, true);
			break;
		case SEL_Item_move:
			distMoved.x /= matrix.sx;
			distMoved.y /= matrix.sy;
			selectedItem->move(distMoved.x, distMoved.y);
			redraw(true, true);
			break;
		case SEL_Item_border_move:
			matrix.get_inverse_matrix().transform_point(tx, ty);

			selectedItem->moveBorder(tx, ty);
			redraw(true, true);
			break;
		case SEL_Item_rotate:
			selectedItem->rotate(distMoved.y);
			redraw(true, true);
			break;
	}
	mousePrevPos = event;
}

void SpacePanel::mouseReleased(wxMouseEvent& event) // Used for several buttons on the mouse
{
	sel= SEL_None;
}

void SpacePanel::mouseWheelMoved(wxMouseEvent& event)
{
	if(sel == SEL_None) // To make sure not clicking something
	{
		if(event.m_shiftDown) // shift means apply to bg
		{
			matrix.scale_rotation(-event.m_wheelRotation);
		}
		else if(getClickedObject(event.m_x, event.m_y, false) == CLICKED_Inner)
		{
			selectedItem->scale(-event.m_wheelRotation);
		}
		else
		{
			matrix.scale_rotation(-event.m_wheelRotation);
		}
		redraw(true, true);
	}
}

void SpacePanel::middleDown(wxMouseEvent& event)
{
	if(getClickedObject(event.m_x, event.m_y, false) == CLICKED_Inner) // Any other sort of click
	{
		sel = SEL_Item_rotate;
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
}

void SpacePanel::onObjectMenuDelete(wxCommandEvent& WXUNUSED(event))
{
	cout << "Deleting item..." << endl;

	lmanager.objs.remove(selectedItem);

	redraw(true, true);
}

wxRealPoint operator-(const wxMouseEvent& lhs, const wxMouseEvent& rhs)
{
	wxRealPoint temp;
	temp.x = lhs.m_x - rhs.m_x;
	temp.y = lhs.m_y - rhs.m_y;
	return temp;
}
