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

	EVT_MENU(Objects::ID_CMenu_1, SpacePanel::cMenuClick) // There MUST be a better way than this
	EVT_MENU(Objects::ID_CMenu_2, SpacePanel::cMenuClick)
	EVT_MENU(Objects::ID_CMenu_3, SpacePanel::cMenuClick)
	EVT_MENU(Objects::ID_CMenu_4, SpacePanel::cMenuClick)
	EVT_MENU(Objects::ID_CMenu_5, SpacePanel::cMenuClick)
	EVT_MENU(Objects::ID_CMenu_6, SpacePanel::cMenuClick)
	EVT_MENU(Objects::ID_CMenu_7, SpacePanel::cMenuClick)
	EVT_MENU(Objects::ID_CMenu_8, SpacePanel::cMenuClick)
	EVT_MENU(Objects::ID_CMenu_9, SpacePanel::cMenuClick)
	EVT_MENU(Objects::ID_CMenu_10, SpacePanel::cMenuClick)
END_EVENT_TABLE()

using namespace std;
using namespace Levels;
using namespace Objects;

// TODO: Add a whole stack of documentation to this!

#define STARFIELD_SIZE 2000
#define STARFIELD_STARS 1000

SpacePanel::SpacePanel(wxWindow *parent, LevelManager &lmanager) :
	CairoPanel(parent),
	mousePrevPos(wxEVT_MOTION),
	lmanager(lmanager),
	currentIntersections(false)
{
	for(int i = 0; i < STARFIELD_STARS; i++)
	{
		stars.push_back(wxPoint(rand() % (STARFIELD_SIZE * 2) - STARFIELD_SIZE, rand() % (STARFIELD_SIZE * 2) - STARFIELD_SIZE));
	}

	bgMenu = new wxMenu;

	redraw();
}

SpacePanel::~SpacePanel()
{
}

void SpacePanel::render_draw()
{
	//cr->set_antialias(Cairo::ANTIALIAS_NONE);

	cr->save();
	cr->set_source_rgb(0, 0, 0);
	cr->paint();

	// Clip world outside
	if(lmanager.levelBounds.get())
	{
		cr->rectangle(
				lmanager.levelBounds->x - lmanager.levelBounds->sx / 2 - 3,
				lmanager.levelBounds->y - lmanager.levelBounds->sy / 2 - 3,
				lmanager.levelBounds->sx + 6,
				lmanager.levelBounds->sy + 6);
		cr->clip();
	}

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

	// Draw objects (and check for deletions)
	for(list<Objects::SpaceItem *>::iterator it = lmanager.objs.begin(); it != lmanager.objs.end(); it++)
	{
		if((*it)->recycle)
		{
			delete *it;
			lmanager.objs.erase(it);

			Refresh();
			break; // TODO: Find a better fix to this?
		}
		else (*it)->draw(cr);
	}

	if(lmanager.levelBounds.get())
		lmanager.levelBounds->draw(cr);
	if(lmanager.p.get())
		lmanager.p->draw(cr);
	if(lmanager.portal.get())
		lmanager.portal->draw(cr);

	cr->reset_clip();

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
	selectedItemIsSpecial = false;
	int ret = CLICKED_None;

	if(lmanager.levelBounds->isBorderClicked(tx, ty))
	{
		// Object border is clicked.
		selectedItem = lmanager.levelBounds.get();
		ret = CLICKED_Border;
		selectedItemIsSpecial = true;
	}

	if(lmanager.p->isClicked(tx, ty))
	{
		// Object border is clicked.
		selectedItem = lmanager.p.get();
		ret = CLICKED_Inner;
		selectedItemIsSpecial = true;
	}

	if(lmanager.portal->isClicked(tx, ty))
	{
		// Object border is clicked.
		selectedItem = lmanager.portal.get();
		ret = CLICKED_Inner;
		selectedItemIsSpecial = true;
	}

	if(!selectedItem)
		for(list<Objects::SpaceItem *>::iterator it = lmanager.objs.begin(); (it != lmanager.objs.end()); it++)
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
			redraw();
			break;
		case SEL_Item_move:
			distMoved.x /= matrix.sx;
			distMoved.y /= matrix.sy;
			selectedItem->move(distMoved.x, distMoved.y);
			checkBoundsCollision(selectedItem);
			redraw();
			break;
		case SEL_Item_border_move:
			matrix.get_inverse_matrix().transform_point(tx, ty);

			selectedItem->moveBorder(tx, ty);
			redraw();
			break;
		case SEL_Item_rotate:
			selectedItem->rotate(distMoved.y);
			redraw();
			break;
	}
	mousePrevPos = event;
}

void SpacePanel::mouseReleased(wxMouseEvent& event) // Used for several buttons on the mouse
{
	sel= SEL_None;

	if(!selectedItemIsSpecial && selectedItem)
	{
		// Check collisions
		if(event.Button(wxMOUSE_BTN_LEFT))
		{
			checkBoundsCollision(selectedItem);
//			checkCollisions();
		}
	}
}

// Should this be used at all?
void SpacePanel::checkCollisions()
{
	bool oldCI = currentIntersections;
	currentIntersections = false;

	for(list<Objects::SpaceItem *>::iterator it = lmanager.objs.begin(); it != lmanager.objs.end(); it++)
	{
		(*it)->isIntersecting = false; // Reset all objects
	}

	for(list<Objects::SpaceItem *>::iterator it = lmanager.objs.begin(); it != lmanager.objs.end(); it++)
	{
		for(list<Objects::SpaceItem *>::iterator it2 = it; it2 != lmanager.objs.end(); it2++) // Start from last pos
		{
			if(*it == *it2) continue;
			if((*it)->intersects(**it2))
			{
				(*it)->isIntersecting = true;
				(*it2)->isIntersecting = true;
				currentIntersections = true; // Indicates that level can't be saved in current state
			}
		}
	}

	if(currentIntersections || oldCI) redraw();
}

bool SpacePanel::checkBoundsCollision(SpaceItem* item)
{
	return item->insideBounds(lmanager.levelBounds->sx, lmanager.levelBounds->sy);
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
		redraw();
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
		if(selectedItemIsSpecial) return; // no context menu
		PopupMenu(selectedItem->getContextMenu());
	}
	else
	{
		PopupMenu(bgMenu);
	}
}

void SpacePanel::cMenuClick(wxCommandEvent& evt)
{
	selectedItem->onCMenuItemClick(evt.GetId());
}

void SpacePanel::onRefresh()
{
//	Refresh();
	redraw();
}

wxRealPoint operator-(const wxMouseEvent& lhs, const wxMouseEvent& rhs)
{
	wxRealPoint temp;
	temp.x = lhs.m_x - rhs.m_x;
	temp.y = lhs.m_y - rhs.m_y;
	return temp;
}
