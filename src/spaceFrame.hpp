#ifndef SPACEFRAME_H
#define SPACEFRAME_H

#include <wx/wx.h>
#include "spacePanel.hpp"

class SpaceFrame: public wxFrame
{
	public:
		SpaceFrame();
		DECLARE_EVENT_TABLE()
	private:
		wxMenu *menuFile, *menuAbout;
		wxMenuBar *menuBar;
		enum
		{
			ID_File_Quit = wxID_HIGHEST + 1,
			ID_Help_About,
		};

		wxBoxSizer *hcontainer;
		SpacePanel *spacePanel;

		void OnQuit(wxCommandEvent& event);
		void OnAbout(wxCommandEvent& event);
};

#endif
