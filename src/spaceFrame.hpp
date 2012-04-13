#ifndef SPACEFRAME_H
#define SPACEFRAME_H

#include <wx/wx.h>
#include "spacePanel.hpp"

#include <levelrw/levelManager.hpp>

class OpenLevelList;
class SpaceFrame: public wxFrame
{
	public:
		SpaceFrame(OpenLevelList &parent, wxString& levelSetName, std::string filename, bool newLevel = false);
		~SpaceFrame();
		DECLARE_EVENT_TABLE()

	public:
		Levels::LevelManager lmanager;
	protected:
		wxMenu *menuFile, *menuLevel, *menuAbout, *menuCreate, *menuEdit;
		wxMenuBar *menuBar;

		std::list<wxButton *> tbButtons;

		wxString levelSetName;

		enum
		{
			ID_File_Save = wxID_HIGHEST + 1,
			ID_File_Quit,

			ID_Level_Change,

			ID_Tools_Preferences,

			ID_Help_Help,
			ID_Help_About,

			ID_tb_c_planet,
			ID_tb_c_infobox,
			ID_tb_c_wall,
			ID_tb_c_vortex,
			ID_tb_c_blackhole,

			ID_tb_c_blocks,

			ID_block_center,
			ID_block_corner,
			ID_block_edge,
			ID_block_fade,
			ID_block_walljoin1,
			ID_block_walljoin2,
			ID_block_wallcorner,

			ID_tb_c_star,
		};

		wxBoxSizer *hcontainer;
		SpacePanel *spacePanel;

		/* Returns true if level saved successfully */
		bool save();

		void OnQuit(wxCloseEvent& event);

		void OnFileNew(wxCommandEvent& event);
		void OnFileOpen(wxCommandEvent& event);
		void OnFileSave(wxCommandEvent& event);
		void OnFileSaveAs(wxCommandEvent& event);
		void OnFileQuit(wxCommandEvent& event);

		void OnLevelInfoChange(wxCommandEvent& event);

		void OnPreferencesOpen(wxCommandEvent& event);

		void OnHelpAbout(wxCommandEvent& event);
		void OnHelpHelp(wxCommandEvent& event);

		void OnCreatePlanet(wxCommandEvent& event);
		void OnCreateInfoBox(wxCommandEvent& event);
		void OnCreateWall(wxCommandEvent& event);
		void OnCreateVortex(wxCommandEvent& event);
		void OnCreateBlackHole(wxCommandEvent& event);

		void OnCreateBlock(wxCommandEvent& event);
		void OnCreateBlockPart(wxCommandEvent& event);

		void OnCreateStar(wxCommandEvent& event);

		/* Returns false if action should be cancelled */
		bool checkForSave();

		OpenLevelList &parent;
		
	private:
		void init(std::string filename, bool newLevel = false);
};

#endif
