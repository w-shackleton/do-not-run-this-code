#include"spaceFrame.hpp"

BEGIN_EVENT_TABLE(SpaceFrame, wxFrame)
	EVT_CLOSE(SpaceFrame::OnQuit)
	EVT_MENU(SpaceFrame::ID_File_New, SpaceFrame::OnFileNew)
	EVT_MENU(SpaceFrame::ID_File_Open, SpaceFrame::OnFileOpen)
	EVT_MENU(SpaceFrame::ID_File_Save, SpaceFrame::OnFileSave)
	EVT_MENU(SpaceFrame::ID_File_SaveAs, SpaceFrame::OnFileSaveAs)
	EVT_MENU(SpaceFrame::ID_File_Quit, SpaceFrame::OnFileQuit)

	EVT_MENU(SpaceFrame::ID_Level_Change, SpaceFrame::OnLevelInfoChange)

	EVT_MENU(SpaceFrame::ID_Tools_Preferences, SpaceFrame::OnPreferencesOpen)

	EVT_MENU(SpaceFrame::ID_Help_Help, SpaceFrame::OnHelpHelp)
	EVT_MENU(SpaceFrame::ID_Help_About, SpaceFrame::OnHelpAbout)

	EVT_BUTTON(SpaceFrame::ID_tb_c_planet, SpaceFrame::OnCreatePlanet)
	EVT_BUTTON(SpaceFrame::ID_tb_c_infobox, SpaceFrame::OnCreateInfoBox)
	EVT_BUTTON(SpaceFrame::ID_tb_c_wall, SpaceFrame::OnCreateWall)
	EVT_BUTTON(SpaceFrame::ID_tb_c_vortex, SpaceFrame::OnCreateVortex)
	EVT_BUTTON(SpaceFrame::ID_tb_c_blackhole, SpaceFrame::OnCreateBlackHole)

	EVT_MENU(SpaceFrame::ID_tb_c_planet, SpaceFrame::OnCreatePlanet)
	EVT_MENU(SpaceFrame::ID_tb_c_infobox, SpaceFrame::OnCreateInfoBox)
	EVT_MENU(SpaceFrame::ID_tb_c_wall, SpaceFrame::OnCreateWall)
	EVT_MENU(SpaceFrame::ID_tb_c_vortex, SpaceFrame::OnCreateVortex)
	EVT_MENU(SpaceFrame::ID_tb_c_blackhole, SpaceFrame::OnCreateBlackHole)
END_EVENT_TABLE()

#include <iostream>
using namespace std;

#include "misc/data.hpp"

#include "objects/spaceItems.hpp"
#include "objects/infoboxEditor.hpp"
#include "objects/planetEditor.hpp"

#include "levelInfoEditor.hpp"
#include "preferences.hpp"

#ifdef __WXMSW__
#define _FRAME_ICON "icon.xpm"
#else
#define _FRAME_ICON "iconlarge.xpm"
#endif

#define FRAME_TITLE "Space Hopper Level Editor"

SpaceFrame::SpaceFrame()
	: wxFrame(NULL, -1, _(FRAME_TITLE), wxDefaultPosition, wxSize(640, 480))
{
	cout << "Loading!" << endl;
	SetIcon(wxIcon(wxString(Misc::Data::getFilePath(_FRAME_ICON).c_str(), wxConvUTF8), wxBITMAP_TYPE_XPM));

	menuFile = new wxMenu;
	menuEdit = new wxMenu;
	menuLevel = new wxMenu;
	menuCreate = new wxMenu;
	menuAbout = new wxMenu;

	menuFile->Append(ID_File_New, _("&New\tCtrl-N"));
	menuFile->Append(ID_File_Open, _("&Open\tCtrl-O"));
	menuFile->Append(ID_File_Save, _("&Save\tCtrl-S"));
	menuFile->Append(ID_File_SaveAs, _("Save &As"));
	menuFile->Append(ID_File_Quit, _("E&xit\tCtrl-Q"));

	menuEdit->Append(ID_Tools_Preferences, _("&Preferences\tCtrl-P"));

	menuLevel->Append(ID_Level_Change, _("Edit &Level Info\tCtrl-L"));

	menuCreate->Append(ID_tb_c_planet, _("Create Planet"));
	menuCreate->Append(ID_tb_c_infobox, _("Create Info Box"));
	menuCreate->Append(ID_tb_c_wall, _("Create Wall"));
	menuCreate->Append(ID_tb_c_vortex, _("Create Vortex"));
	menuCreate->Append(ID_tb_c_blackhole, _("Create Black Hole"));

	menuAbout->Append(ID_Help_Help, _("&Help\tCtrl-H"));
	menuAbout->Append(ID_Help_About, _("&About"));

	menuBar = new wxMenuBar;
	menuBar->Append(menuFile, _("&File"));
	menuBar->Append(menuEdit, _("&Edit"));
	menuBar->Append(menuLevel, _("&Level"));
	menuBar->Append(menuCreate, _("&Create"));
	menuBar->Append(menuAbout, _("&Help"));
	SetMenuBar(menuBar);

	wxToolBar *toolbar = CreateToolBar();
	tbButtons.push_back(new wxButton(toolbar, ID_tb_c_planet, _("Create Planet")));
	tbButtons.push_back(new wxButton(toolbar, ID_tb_c_infobox, _("Create Info Box")));
	tbButtons.push_back(new wxButton(toolbar, ID_tb_c_wall, _("Create Wall")));
	tbButtons.push_back(new wxButton(toolbar, ID_tb_c_vortex, _("Create Vortex")));
	tbButtons.push_back(new wxButton(toolbar, ID_tb_c_blackhole, _("Create Black Hole")));
	for(list<wxButton *>::iterator it = tbButtons.begin(); it != tbButtons.end(); it++)
	{
		toolbar->AddControl(*it);
	}
	toolbar->Realize();

	CreateStatusBar();
	SetStatusText(_("SpaceGame Editor"));

	hcontainer = new wxBoxSizer(wxHORIZONTAL);
	spacePanel = new SpacePanel(this, lmanager);
	lmanager.setEditorCallbacks(spacePanel);
	hcontainer->Add(spacePanel, 1, wxEXPAND);

	SetSizer(hcontainer);
	SetAutoLayout(true);

	LevelInfoEditor editor(lmanager);
	editor.ShowModal();

	SetTitle(_(FRAME_TITLE) + wxString((" - " + lmanager.levelName).c_str(), wxConvUTF8));
}

bool SpaceFrame::save()
{
	if(lmanager.levelChanged)
	{
		if(!lmanager.save()) // If couldn't save automatically
		{
			return saveAs();
		}
	}
	return true;
}

bool SpaceFrame::saveAs()
{
	wxFileDialog dialog(this, _("Save level"), wxT(""), wxT(""), _("Levels|*.slv"), wxFD_SAVE | wxFD_OVERWRITE_PROMPT);
	if(dialog.ShowModal() == wxID_OK)
	{
		lmanager.saveLevel((string)dialog.GetFilename().mb_str(wxConvUTF8));
		return true;
	}
	else return false;
}

bool SpaceFrame::open()
{
	if(!save()) return false;
	wxFileDialog dialog(this, _("Open level"), wxT(""), wxT(""), _("Levels|*.slv"), wxFD_OPEN | wxFD_FILE_MUST_EXIST);
	if(dialog.ShowModal() == wxID_OK)
	{
		return lmanager.openLevel((string)dialog.GetFilename().mb_str(wxConvUTF8));
	}
	return false;
}

void SpaceFrame::OnQuit(wxCloseEvent& event)
{
	if(!lmanager.levelChanged)
	{
		Destroy();
		return;
	}
	wxMessageDialog dialog(this, _("Do you want to save the current level?"), _("Save level?"), wxYES_NO | wxCANCEL | wxICON_QUESTION);
	int ret = dialog.ShowModal();
	switch(ret)
	{
		case wxID_CANCEL:
			break;
		case wxID_NO:
			Destroy();
			break;
		case wxID_YES:
			if(!save())
				break;
			else
			{
				Destroy();
				break;
			}
	}
}

void SpaceFrame::OnFileNew(wxCommandEvent& event)
{
	if(!lmanager.levelChanged)
	{
		lmanager.newLevel();
		spacePanel->redraw();
		LevelInfoEditor editor(lmanager, true);
		editor.ShowModal();
		SetTitle(_(FRAME_TITLE) + wxString((" - " + lmanager.levelName).c_str(), wxConvUTF8));
		spacePanel->redraw();
		return;
	}

	LevelInfoEditor editor(lmanager, true);

	wxMessageDialog dialog(this, _("Do you want to save the current level?"), _("Save level?"), wxYES_NO | wxCANCEL | wxICON_QUESTION);
	int ret = dialog.ShowModal();
	switch(ret)
	{
		case wxID_CANCEL:
			break;
		case wxID_NO:
			lmanager.levelChanged = false; // Little hack
			lmanager.newLevel();
			spacePanel->redraw();
			editor.ShowModal();
			SetTitle(_(FRAME_TITLE) + wxString((" - " + lmanager.levelName).c_str(), wxConvUTF8));
			spacePanel->redraw();
			break;
		case wxID_YES:
			if(!save())
				break;
			lmanager.newLevel();
			spacePanel->redraw();
			editor.ShowModal();
			SetTitle(_(FRAME_TITLE) + wxString((" - " + lmanager.levelName).c_str(), wxConvUTF8));
			spacePanel->redraw();
			break;
	}
}

void SpaceFrame::OnFileOpen(wxCommandEvent& event)
{
	if(!lmanager.levelChanged)
	{
		open();
		spacePanel->redraw();
		return;
	}
	wxMessageDialog dialog(this, _("Do you want to save the current level?"), _("Save level?"), wxYES_NO | wxCANCEL | wxICON_QUESTION);
	int ret = dialog.ShowModal();
	switch(ret)
	{
		case wxID_CANCEL:
			break;
		case wxID_NO:
			lmanager.levelChanged = false; // Little hack
			open();
			spacePanel->redraw();
			break;
		case wxID_YES:
			if(!save())
				break;
			open();
			spacePanel->redraw();
			break;
	}
}

void SpaceFrame::OnFileSave(wxCommandEvent& event)
{
	save();
}

void SpaceFrame::OnFileSaveAs(wxCommandEvent& event)
{
	saveAs();
}

void SpaceFrame::OnFileQuit(wxCommandEvent& event)
{
	Close(TRUE);
}

void SpaceFrame::OnLevelInfoChange(wxCommandEvent& event)
{
	LevelInfoEditor editor(lmanager);
	editor.ShowModal();
	SetTitle(_(FRAME_TITLE) + wxString((" - " + lmanager.levelName).c_str(), wxConvUTF8));
}

void SpaceFrame::OnPreferencesOpen(wxCommandEvent& event)
{
	Preferences prefDialog;
	prefDialog.ShowModal();
}

void SpaceFrame::OnHelpHelp(wxCommandEvent& event)
{
	wxLaunchDefaultBrowser(wxT("http://digitalsquid.co.uk"));
}

void SpaceFrame::OnHelpAbout(wxCommandEvent& event)
{
	wxMessageBox(_("SpaceGame editor\n\nDigitalsquid\ndigitalsquid.co.uk"));
}

void SpaceFrame::OnCreatePlanet(wxCommandEvent& event)
{
	wxSize pos = spacePanel->getMovedPos() + spacePanel->GetSize() / 2;

	int type = 3;
	Objects::Helpers::PlanetEditor editor(this);
	cout << type << "Part 1" << endl;
	if(editor.ShowModal() == 0)
	{
		type = editor.type;
		cout << type << "Part 2" << endl;
		lmanager.objs.push_back(new Objects::Planet(*spacePanel, type, pos.GetWidth(), pos.GetHeight(), 50)); // Default radius
	}
	spacePanel->redraw();
}

void SpaceFrame::OnCreateInfoBox(wxCommandEvent& event)
{
	wxSize pos = spacePanel->getMovedPos() + spacePanel->GetSize() / 2;

	string text;
	bool initialShow;
	
	Objects::Helpers::InfoBoxEditor ibCreator(this, text, initialShow);
	if(ibCreator.ShowModal() == 0) // If user clicked 'OK' - only want to create then
	{
		lmanager.objs.push_back(new Objects::InfoBox(*spacePanel, pos.GetWidth(), pos.GetHeight(), 0, text, initialShow));
	}
	spacePanel->redraw();
}

void SpaceFrame::OnCreateWall(wxCommandEvent& event)
{
	wxSize pos = spacePanel->getMovedPos() + spacePanel->GetSize() / 2;
	lmanager.objs.push_back(new Objects::Wall(*spacePanel, pos.GetWidth(), pos.GetHeight(), 100, 0));
	spacePanel->redraw();
}

void SpaceFrame::OnCreateVortex(wxCommandEvent& event)
{
	wxSize pos = spacePanel->getMovedPos() + spacePanel->GetSize() / 2;
	lmanager.objs.push_back(new Objects::Vortex(*spacePanel, pos.GetWidth(), pos.GetHeight(), 100, 100, 0));
	spacePanel->redraw();
}

void SpaceFrame::OnCreateBlackHole(wxCommandEvent& event)
{
	wxSize pos = spacePanel->getMovedPos() + spacePanel->GetSize() / 2;
	lmanager.objs.push_back(new Objects::BlackHole(*spacePanel, pos.GetWidth(), pos.GetHeight()));
	spacePanel->redraw();
}

