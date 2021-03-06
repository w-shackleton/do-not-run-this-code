#ifndef SPACEPANEL_H
#define SPACEPANEL_H

#include <wx/wx.h>
#include "cairoPanel.hpp"

#include "objects/spaceItem.hpp"
#include "editorCallbacks.hpp"

#include <list>

#include <levelrw/levelManager.hpp>

#define PHONE_SCREEN_X 480
#define PHONE_SCREEN_Y 320

class SpacePanel : public CairoPanel, public EditorCallbacks
{
	public:
		SpacePanel(wxWindow *parent, Levels::LevelManager &lmanager);
		~SpacePanel();

		// Callbacks
		void onRefresh();

		DECLARE_EVENT_TABLE();
	protected:
		void render_draw();

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

		void checkCollisions();
		bool checkBoundsCollision(Objects::SpaceItem *item);

		wxMouseEvent mousePrevPos;
		Levels::LevelManager &lmanager;

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

		/*
		   Current selection mode - what is being selected
		   */
		int sel;
		Objects::SpaceItem *selectedItem;
		bool selectedItemIsSpecial;

		std::vector<wxPoint> stars;

		wxMenu *bgMenu;

		void cMenuClick(wxCommandEvent& evt);

		bool currentIntersections;
};

// Some useful operators

wxRealPoint operator-(const wxMouseEvent& lhs, const wxMouseEvent& rhs);

#endif
