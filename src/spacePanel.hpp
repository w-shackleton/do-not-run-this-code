#ifndef SPACEPANEL_H
#define SPACEPANEL_H

#include <wx/wx.h>
#include "cairoPanel.hpp"

class SpacePanel : public CairoPanel
{
	public:
		SpacePanel(wxWindow *parent);

		DECLARE_EVENT_TABLE();
	protected:
		void redraw(bool repaint);

		// Some useful events
		void mouseDown(wxMouseEvent& event);
		void mouseMoved(wxMouseEvent& event);
		void mouseReleased(wxMouseEvent& event);
		/*void mouseWheelMoved(wxMouseEvent& event);
		void rightClick(wxMouseEvent& event);
		void mouseLeftWindow(wxMouseEvent& event);
		void keyPressed(wxKeyEvent& event);
		void keyReleased(wxKeyEvent& event);*/

		wxMouseEvent mousePrevPos;

		enum
		{
			SEL_None,
			SEL_Bg_move,
		};
		
		/*
		   Current selection mode - what is being selected
		   */
		int sel;
};

// Some useful operators

wxPoint operator-(const wxMouseEvent& lhs, const wxMouseEvent& rhs);

#endif
