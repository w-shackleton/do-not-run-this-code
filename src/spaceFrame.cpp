#include"spaceFrame.hpp"

BEGIN_EVENT_TABLE(SpaceFrame, wxFrame)
	EVT_CLOSE(SpaceFrame::OnQuit)
	EVT_MENU(SpaceFrame::ID_File_New, SpaceFrame::OnFileNew)
	EVT_MENU(SpaceFrame::ID_File_Open, SpaceFrame::OnFileOpen)
	EVT_MENU(SpaceFrame::ID_File_Save, SpaceFrame::OnFileSave)
	EVT_MENU(SpaceFrame::ID_File_SaveAs, SpaceFrame::OnFileSaveAs)
	EVT_MENU(SpaceFrame::ID_File_Quit, SpaceFrame::OnFileQuit)

	EVT_MENU(SpaceFrame::ID_Help_Help, SpaceFrame::OnHelpHelp)
	EVT_MENU(SpaceFrame::ID_Help_About, SpaceFrame::OnHelpAbout)
END_EVENT_TABLE()

#include <iostream>
using namespace std;

#include "misc/data.hpp"

#ifdef __WXMSW__
#define _FRAME_ICON "icon.xpm"
#else
#define _FRAME_ICON "iconlarge.xpm"
#endif

SpaceFrame::SpaceFrame()
	: wxFrame(NULL, -1, _("SpaceGame Editor"), wxDefaultPosition, wxSize(640, 480))
{
	cout << "Loading!" << endl;
	SetIcon(wxIcon(wxString(Misc::Data::getFilePath(_FRAME_ICON).c_str(), wxConvUTF8), wxBITMAP_TYPE_XPM));

	menuFile = new wxMenu;
	menuAbout = new wxMenu;

	menuFile->Append(ID_File_New, _("&New\tCtrl-N"));
	menuFile->Append(ID_File_Open, _("&Open\tCtrl-O"));
	menuFile->Append(ID_File_Save, _("&Save\tCtrl-S"));
	menuFile->Append(ID_File_SaveAs, _("Save &As"));
	menuFile->Append(ID_File_Quit, _("E&xit\tCtrl-Q"));

	menuAbout->Append(ID_Help_Help, _("&Help\tCtrl-H"));
	menuAbout->Append(ID_Help_About, _("&About"));

	menuBar = new wxMenuBar;
	menuBar->Append(menuFile, _("&File"));
	menuBar->Append(menuAbout, _("&Help"));

	SetMenuBar(menuBar);
	CreateStatusBar();
	SetStatusText(_("SpaceGame Editor"));

	hcontainer = new wxBoxSizer(wxHORIZONTAL);
	spacePanel = new SpacePanel(this, lmanager);
	hcontainer->Add(spacePanel, 1, wxEXPAND);

	SetSizer(hcontainer);
	SetAutoLayout(true);
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
	}
}

void SpaceFrame::OnFileOpen(wxCommandEvent& event)
{
	open();
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

void SpaceFrame::OnHelpHelp(wxCommandEvent& event)
{
	wxLaunchDefaultBrowser(wxT("http://digitalsquid.co.uk"));
}

void SpaceFrame::OnHelpAbout(wxCommandEvent& event)
{
	wxMessageBox(_("SpaceGame editor\n\nDigitalsquid\ndigitalsquid.co.uk"));
}
