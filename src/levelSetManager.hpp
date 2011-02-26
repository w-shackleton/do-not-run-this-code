#ifndef LEVELSETMANAGER_H
#define LEVELSETMANAGER_H

#include <wx/frame.h>
#include <wx/menu.h>
#include <list>
#include <map>
#include <wx/button.h>
#include <wx/sizer.h>
#include <wx/dir.h>

#include "openLevelList.hpp"

class LevelMetadata
{
	public:
		LevelMetadata();
		LevelMetadata(wxString levelFileName);
		wxString levelName, creatorName, levelFileName;

		bool operator ==(const LevelMetadata& b);
};

class LevelSetMetadata
{
	public:
		LevelSetMetadata(wxString folderName);
		LevelSetMetadata(wxString folderName, wxString setName, wxString creatorName);
		wxString setName, creatorName, metaFileName, folderName;

		std::list<LevelMetadata> levels;

		void saveMetadata();
};

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
			ID_New_levelset,
			ID_Delete_levelset,
			ID_New_level,
			ID_Delete_level,

			ID_LevelSet_list,
			ID_Level_list,

			ID_Level_series,
			ID_Level_number,
			ID_Level_name_set,

			ID_Level_set_name,
			ID_Level_set_creator,
			ID_Level_set_name_set,
		};

		wxBoxSizer *hcontainer;

		wxListBox *levelSetList, *levelList;
		wxTextCtrl *levelSeries, *levelNumber;
		wxTextCtrl *levelSetName, *levelSetCreator;
		wxButton *levelDataSet, *levelSetDataSet, *deleteLevelSetButton;
		wxButton *newLevelButton, *deleteLevelButton;

		void OnQuit(wxCloseEvent& event);

		void OnFileNew(wxCommandEvent& event);
		void OnDeleteLevelSet(wxCommandEvent& event);

		void OnNewLevel(wxCommandEvent& event);
		void OnDeleteLevel(wxCommandEvent& event);

		void OnFileQuit(wxCommandEvent& event);

		void OnPreferencesOpen(wxCommandEvent& event);

		void OnHelpAbout(wxCommandEvent& event);
		void OnHelpHelp(wxCommandEvent& event);

		void OnRefreshLists(wxCommandEvent& WXUNUSED(event));
		void OnLevelSetItemSelected(wxCommandEvent& event);
		void OnLevelItemSelected(wxCommandEvent& event);
		void OnLevelItemDblClicked(wxCommandEvent& event);

		void OnLevelNameSet(wxCommandEvent& WXUNUSED(event));
		void OnLevelSetNameSet(wxCommandEvent& WXUNUSED(event));

		OpenLevelList openLevels;
		LevelSetMetadata *currentLevelSet;
		std::list<LevelMetadata> *currentLevels;
		LevelMetadata *currentLevel;

		std::map<wxString, LevelSetMetadata> levelSets;

		void syncListsToScreen();

	private:
		virtual wxDirTraverseResult OnFile(const wxString& filename);
		virtual wxDirTraverseResult OnDir(const wxString& dirname);

		static bool sortCurrentLevels(LevelMetadata first, LevelMetadata second);
};

#endif
