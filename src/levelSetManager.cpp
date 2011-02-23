#include"levelSetManager.hpp"

BEGIN_EVENT_TABLE(LevelSetManager, wxFrame)
	EVT_CLOSE(LevelSetManager::OnQuit)
	EVT_MENU(LevelSetManager::ID_File_New, LevelSetManager::OnFileNew)
	EVT_MENU(LevelSetManager::ID_File_Quit, LevelSetManager::OnFileQuit)

	EVT_MENU(LevelSetManager::ID_Tools_Preferences, LevelSetManager::OnPreferencesOpen)

	EVT_MENU(LevelSetManager::ID_Help_Help, LevelSetManager::OnHelpHelp)
	EVT_MENU(LevelSetManager::ID_Help_About, LevelSetManager::OnHelpAbout)

	EVT_BUTTON(LevelSetManager::ID_Refresh, LevelSetManager::OnRefreshLists)

	EVT_LISTBOX(LevelSetManager::ID_LevelSet_list, LevelSetManager::OnLevelSetItemSelected)
END_EVENT_TABLE()

#include <iostream>
using namespace std;

#include <wx/icon.h>
#include <wx/msgdlg.h>

#include "misc/data.hpp"

#include "levelInfoEditor.hpp"
#include "preferences.hpp"

#ifdef __WXMSW__
#define _FRAME_ICON "icon.xpm"
#else
#define _FRAME_ICON "iconlarge.xpm"
#endif

#define FRAME_TITLE "Space Hopper Level Editor - Level Set Manager"

LevelSetManager::LevelSetManager()
	: wxFrame(NULL, -1, _(FRAME_TITLE), wxDefaultPosition, wxSize(640, 480))
{
	cout << "Loading!" << endl;
	SetIcon(wxIcon(wxString(Misc::Data::getFilePath(_FRAME_ICON).c_str(), wxConvUTF8), wxBITMAP_TYPE_XPM));

	menuFile = new wxMenu;
	menuEdit = new wxMenu;
	menuAbout = new wxMenu;

	menuFile->Append(ID_File_New, _("&New\tCtrl-N"));
	menuFile->Append(ID_File_Quit, _("E&xit\tCtrl-Q"));

	menuEdit->Append(ID_Tools_Preferences, _("&Preferences\tCtrl-P"));

	menuAbout->Append(ID_Help_Help, _("&Help\tCtrl-H"));
	menuAbout->Append(ID_Help_About, _("&About"));

	menuBar = new wxMenuBar;
	menuBar->Append(menuFile, _("&File"));
	menuBar->Append(menuEdit, _("&Edit"));
	menuBar->Append(menuAbout, _("&Help"));
	SetMenuBar(menuBar);

	hcontainer = new wxBoxSizer(wxHORIZONTAL);
	wxBoxSizer *lh = new wxBoxSizer(wxVERTICAL);

	lh->Add(new wxButton(this, ID_Refresh, _("Refresh")), 0, wxALL, 5);
	lh->Add(new wxListBox(this, ID_LevelSet_list), 1, wxALL, 5);

	wxBoxSizer *rh = new wxBoxSizer(wxVERTICAL);
	hcontainer->Add(lh, 1);
	hcontainer->Add(rh, 1);

	SetSizer(hcontainer);
	SetAutoLayout(true);

	refreshLists();
}

void LevelSetManager::refreshLists()
{
	wxDir dir(wxString(Misc::Data::saveLocation.c_str(), wxConvUTF8));
	dir.Traverse(*this);
}

wxDirTraverseResult LevelSetManager::OnFile(const wxString& filename)
{
	cout << "File found: " << string(filename.mb_str()) << endl;
	return wxDIR_CONTINUE;
}

wxDirTraverseResult LevelSetManager::OnDir(const wxString& dirname)
{
	cout << "Dir found: " << string(dirname.mb_str()) << endl;
	return wxDIR_CONTINUE;
}

void LevelSetManager::OnQuit(wxCloseEvent& event)
{
	if(!openLevels.isEmpty())
	{
		wxMessageDialog dialog(this, _("There are still open levels"));
		return;
	}
	Destroy();
}

void LevelSetManager::OnFileNew(wxCommandEvent& event)
{
}

void LevelSetManager::OnFileQuit(wxCommandEvent& event)
{
	Close(TRUE);
}

void LevelSetManager::OnPreferencesOpen(wxCommandEvent& event)
{
	Preferences prefDialog;
	prefDialog.ShowModal();
}

void LevelSetManager::OnHelpHelp(wxCommandEvent& event)
{
	wxLaunchDefaultBrowser(wxT("http://digitalsquid.co.uk"));
}

void LevelSetManager::OnHelpAbout(wxCommandEvent& event)
{
	wxMessageBox(_("SpaceGame editor\n\nDigitalsquid\ndigitalsquid.co.uk"));
}

void LevelSetManager::OnRefreshLists(wxCommandEvent& event)
{
	refreshLists();
}

void LevelSetManager::OnLevelSetItemSelected(wxCommandEvent& event)
{
}
