#ifndef SPACEFRAME_H
#define SPACEFRAME_H

#include <wx/wx.h>
#include "spacePanel.hpp"

#include <levelrw/levelManager.hpp>

class SpaceFrame: public wxFrame
{
	public:
		SpaceFrame();
		DECLARE_EVENT_TABLE()
	protected:
		wxMenu *menuFile, *menuAbout;
		wxMenuBar *menuBar;
		enum
		{
			ID_File_New = wxID_HIGHEST + 1,
			ID_File_Open,
			ID_File_Save,
			ID_File_SaveAs,
			ID_File_Quit,

			ID_Help_Help,
			ID_Help_About,
		};

		wxBoxSizer *hcontainer;
		SpacePanel *spacePanel;

		/* Returns true if level saved successfully */
		bool save();
		bool saveAs();
		bool open();

		void OnQuit(wxCloseEvent& event);

		void OnFileNew(wxCommandEvent& event);
		void OnFileOpen(wxCommandEvent& event);
		void OnFileSave(wxCommandEvent& event);
		void OnFileSaveAs(wxCommandEvent& event);
		void OnFileQuit(wxCommandEvent& event);

		void OnHelpAbout(wxCommandEvent& event);
		void OnHelpHelp(wxCommandEvent& event);

		/* Returns false if action should be cancelled */
		bool checkForSave();

		Levels::LevelManager lmanager;
};

#endif
