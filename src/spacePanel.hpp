#ifndef SPACEPANEL_H
#define SPACEPANEL_H

#include <wx/wx.h>
#include "cairoPanel.hpp"

#include "objects/spaceItem.hpp"

#include <list>

#define PHONE_SCREEN_X 480
#define PHONE_SCREEN_Y 320

class SpacePanel : public CairoPanel
{
	public:
		SpacePanel(wxWindow *parent);
		~SpacePanel();

		DECLARE_EVENT_TABLE();
	protected:
		void redraw_draw();

		int getClickedObject(double x, double y, bool useBorder);

		// Some useful events
		void mouseDown(wxMouseEvent& event);
		void mouseMoved(wxMouseEvent& event);
		void mouseReleased(wxMouseEvent& event);
		void mouseWheelMoved(wxMouseEvent& event);
		void middleDown(wxMouseEvent& event);
		/*void rightClick(wxMouseEvent& event);
		void mouseLeftWindow(wxMouseEvent& event);
		void keyPressed(wxKeyEvent& event);
		void keyReleased(wxKeyEvent& event);*/

		void contextMenu(wxContextMenuEvent& event);

		wxMouseEvent mousePrevPos;

		enum
		{
			SEL_None,
			SEL_Bg_move,
			SEL_Item_move,
			SEL_Item_border_move,
			SEL_Item_rotate,
		};

		enum
		{
			CLICKED_None,
			CLICKED_Inner,
			CLICKED_Border
		};

		enum
		{
			ID_Object_delete = wxID_HIGHEST + 1,
		};
		
		/*
		   Current selection mode - what is being selected
		   */
		int sel;
		Objects::SpaceItem *selectedItem;

		std::list<Objects::SpaceItem *> objs;

		wxMenu *objectMenu, *bgMenu;
		void onObjectMenuDelete(wxCommandEvent& WXUNUSED(event));
};

// Some useful operators

wxRealPoint operator-(const wxMouseEvent& lhs, const wxMouseEvent& rhs);

#endif
