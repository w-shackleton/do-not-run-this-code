#ifndef LEVELSETMANAGER_H
#define LEVELSETMANAGER_H

#include <wx/frame.h>
#include <wx/menu.h>
#include <list>
#include <wx/button.h>
#include <wx/sizer.h>
#include <wx/dir.h>

#include "openLevelList.hpp"

class LevelSetManager: public wxFrame, private wxDirTraverser
{
	public:
		LevelSetManager();
		DECLARE_EVENT_TABLE()
		void refreshLists();
	protected:
		wxMenu *menuFile, *menuAbout, *menuEdit;
		wxMenuBar *menuBar;

		enum
		{
			ID_File_New = wxID_HIGHEST + 1,
			ID_File_Quit,

			ID_Tools_Preferences,

			ID_Help_Help,
			ID_Help_About,

			ID_Refresh,
			ID_LevelSet_list,
			ID_Level_list,
		};

		wxBoxSizer *hcontainer;

		void OnQuit(wxCloseEvent& event);

		void OnFileNew(wxCommandEvent& event);
		void OnFileQuit(wxCommandEvent& event);

		void OnPreferencesOpen(wxCommandEvent& event);

		void OnHelpAbout(wxCommandEvent& event);
		void OnHelpHelp(wxCommandEvent& event);

		void OnRefreshLists(wxCommandEvent& WXUNUSED(event));
		void OnLevelSetItemSelected(wxCommandEvent& event);

		OpenLevelList openLevels;

	private:
		virtual wxDirTraverseResult OnFile(const wxString& filename);
		virtual wxDirTraverseResult OnDir(const wxString& dirname);
};

#endif
