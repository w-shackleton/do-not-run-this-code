#include"levelSetManager.hpp"

BEGIN_EVENT_TABLE(LevelSetManager, wxFrame)
	EVT_CLOSE(LevelSetManager::OnQuit)
	EVT_MENU(LevelSetManager::ID_File_New, LevelSetManager::OnFileNew)
	EVT_MENU(LevelSetManager::ID_File_Quit, LevelSetManager::OnFileQuit)

	EVT_MENU(LevelSetManager::ID_Tools_Preferences, LevelSetManager::OnPreferencesOpen)

	EVT_MENU(LevelSetManager::ID_Help_Help, LevelSetManager::OnHelpHelp)
	EVT_MENU(LevelSetManager::ID_Help_About, LevelSetManager::OnHelpAbout)

	EVT_BUTTON(LevelSetManager::ID_Refresh, LevelSetManager::OnRefreshLists)
	EVT_BUTTON(LevelSetManager::ID_Delete_levelset, LevelSetManager::OnDeleteLevelSet)
	EVT_BUTTON(LevelSetManager::ID_New_levelset, LevelSetManager::OnFileNew)

	EVT_LISTBOX(LevelSetManager::ID_LevelSet_list, LevelSetManager::OnLevelSetItemSelected)
	EVT_LISTBOX(LevelSetManager::ID_Level_list, LevelSetManager::OnLevelItemSelected)
	EVT_LISTBOX_DCLICK(LevelSetManager::ID_Level_list, LevelSetManager::OnLevelItemDblClicked)

	EVT_BUTTON(LevelSetManager::ID_Level_name_set, LevelSetManager::OnLevelNameSet)
	EVT_BUTTON(LevelSetManager::ID_Level_set_name_set, LevelSetManager::OnLevelSetNameSet)
END_EVENT_TABLE()

#include <iostream>
using namespace std;

#include <wx/icon.h>
#include <wx/msgdlg.h>
#include <wx/stattext.h>
#include <wx/textctrl.h>
#include <wx/textfile.h>
#include <wx/tokenzr.h>
#include <wx/filename.h>

#include "misc/data.hpp"

#include "newLevelDialog.hpp"
#include "levelInfoEditor.hpp"
#include "preferences.hpp"
#include "levelrw/levelrw.hpp"

#ifdef __WXMSW__
#define _FRAME_ICON "icon.xpm"
#else
#define _FRAME_ICON "iconlarge.xpm"
#endif

#define FRAME_TITLE "Space Hopper Level Editor - Level Set Manager"

#define LEVELSET_META_NAME "info.lvlset"

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

	lh->Add(new wxStaticText(this, -1, _("Level Sets")), 0, wxALL, 5);
	wxBoxSizer *lhButtonTop = new wxBoxSizer(wxHORIZONTAL);
	lh->Add(lhButtonTop);
	lhButtonTop->Add(new wxButton(this, ID_Refresh, _("Refresh")), 0, wxALL, 5);
	lhButtonTop->Add(new wxButton(this, ID_New_levelset, _("New")), 0, wxALL, 5);
	deleteLevelSetButton = new wxButton(this, ID_Delete_levelset, _("Delete"));
	deleteLevelSetButton->Disable();
	lhButtonTop->Add(deleteLevelSetButton, 0, wxALL, 5);

	wxBoxSizer *lhTop = new wxBoxSizer(wxHORIZONTAL);
	lh->Add(lhTop);
	lhTop->Add(new wxStaticText(this, -1, _("Level Set Name:")), 0, wxTOP, 9);
	levelSetName = new wxTextCtrl(this, ID_Level_set_name);
	levelSetName->Disable();
	lhTop->Add(levelSetName, 1, wxEXPAND | wxALL, 3);
	lhTop->Add(new wxStaticText(this, -1, _("Creator:")), 0, wxTOP, 9);
	levelSetCreator = new wxTextCtrl(this, ID_Level_set_creator);
	levelSetCreator->Disable();
	lhTop->Add(levelSetCreator, 1, wxEXPAND | wxALL, 3);
	levelSetDataSet = new wxButton(this, ID_Level_set_name_set, _("Save"));
	levelSetDataSet->Disable();
	lhTop->Add(levelSetDataSet, 0, wxALL, 3);

	levelSetList = new wxListBox(this, ID_LevelSet_list);
	lh->Add(levelSetList, 1, wxALL | wxEXPAND, 5);

	wxBoxSizer *rh = new wxBoxSizer(wxVERTICAL);

	rh->Add(new wxStaticText(this, -1, _("Levels in the same series must be completed sequentially")), 0, wxALL, 5);

	wxBoxSizer *rhTop = new wxBoxSizer(wxHORIZONTAL);
	rh->Add(rhTop);
	rhTop->Add(new wxStaticText(this, -1, _("Series:")), 0, wxTOP, 9);
	levelSeries = new wxTextCtrl(this, ID_Level_series);
	levelSeries->Disable();
	rhTop->Add(levelSeries, 1, wxEXPAND | wxALL, 3);
	rhTop->Add(new wxStaticText(this, -1, _(", level:")), 0, wxTOP, 9);
	levelNumber = new wxTextCtrl(this, ID_Level_number);
	levelNumber->Disable();
	rhTop->Add(levelNumber, 1, wxEXPAND | wxALL, 3);
	levelDataSet = new wxButton(this, ID_Level_name_set, _("Save"));
	levelDataSet->Disable();
	rhTop->Add(levelDataSet, 0, wxALL, 3);

	levelList = new wxListBox(this, ID_Level_list);
	levelList->Disable();
	rh->Add(levelList, 1, wxALL | wxEXPAND, 5);

	hcontainer->Add(lh, 1, wxEXPAND);

	wxBoxSizer *centerLabelContainer = new wxBoxSizer(wxVERTICAL);
	centerLabelContainer->Add(new wxStaticText(this, -1, wxT("->")), 1, wxTOP, 150);
	hcontainer->Add(centerLabelContainer, 0, wxEXPAND);

	hcontainer->Add(rh, 1, wxEXPAND);

	SetSizer(hcontainer);
	SetAutoLayout(true);
	Layout();
	Fit();
	wxSize sz = GetSize();
	sz.SetHeight(400);
	SetSize(sz);

	refreshLists();
}

void LevelSetManager::refreshLists()
{
	levelSets.clear();

	wxDir dir(wxString(Misc::Data::saveLocation.c_str(), wxConvUTF8));
	dir.Traverse(*this);
	syncListsToScreen();
}

void LevelSetManager::syncListsToScreen()
{
	levelSetList->Clear();
	for(map<wxString, LevelSetMetadata>::iterator iter = levelSets.begin(); iter != levelSets.end(); iter++)
	{
		levelSetList->Append(iter->second.setName);
	}
}

wxDirTraverseResult LevelSetManager::OnFile(const wxString& filename)
{
	wxFileName fn = wxFileName::FileName(filename);
	wxString filext = fn.GetExt();
	wxString dir = fn.GetDirs().Last();

	if(filext.CmpNoCase(wxT("slv")) != 0)
	{
		return wxDIR_CONTINUE; // Not a level file
	}

	for(map<wxString, LevelSetMetadata>::iterator iter = levelSets.begin(); iter != levelSets.end(); iter++)
	{
		if(iter->first == dir) // Find directory and add to it
		{
			iter->second.levels.push_back(LevelMetadata(filename));
		}
	}

	return wxDIR_CONTINUE;
}

wxDirTraverseResult LevelSetManager::OnDir(const wxString& dirname)
{
	wxString dir = wxFileName::DirName(dirname).GetDirs().Last();
	cout << "Dir found: " << dir.mb_str() << endl;
	levelSets.insert(pair<wxString, LevelSetMetadata>(dir, LevelSetMetadata(dir)));
	return wxDIR_CONTINUE;
}

void LevelSetManager::OnQuit(wxCloseEvent& event)
{
	if(!openLevels.isEmpty())
	{
		wxMessageDialog dialog(this, _("There are still open levels"));
		dialog.ShowModal();
		return;
	}
	Destroy();
}

void LevelSetManager::OnFileNew(wxCommandEvent& event)
{
	NewLevelDialog newLvl;
	if(newLvl.ShowModal() == 0)
	{
		levelSets.insert(pair<wxString, LevelSetMetadata>(newLvl.levelTitle, LevelSetMetadata(newLvl.levelTitle, newLvl.levelTitle, newLvl.levelCreator)));
	}

	syncListsToScreen();
}

void LevelSetManager::OnDeleteLevelSet(wxCommandEvent& event)
{
	for(list<LevelMetadata>::iterator iter = currentLevels->begin(); iter != currentLevels->end(); iter++)
	{
		if(openLevels.isOpen(iter->levelFileName))
		{
			wxMessageDialog dlg(this, _("Some levels are still open in this level set"), _("Levels still open"));
			dlg.ShowModal();
			return;
		}
	}

	wxMessageDialog dlg(this, _("Do you want to delete this level set?"), _("Delete level set?"), wxOK | wxCANCEL);
	if(dlg.ShowModal() == wxID_OK)
	{
		wxString dir = wxString(Misc::Data::saveLocation.c_str(), wxConvUTF8) + wxT("/") + currentLevelSet->folderName;

		cout << "Deleting level set " << dir << endl;

		wxArrayString files;
		wxDir::GetAllFiles(dir, &files, wxEmptyString, wxDIR_FILES | wxDIR_HIDDEN);
		for(wxArrayString::iterator iter = files.begin(); iter != files.end(); iter++)
		{
			wxRemoveFile(*iter);
		}
		
		if(!wxRmdir(dir))
		{
			wxMessageDialog dlg(this, _("Failed to delete level set"), _("Couldn't delete level set"));
			dlg.ShowModal();
			return;
		}

		refreshLists();
	}
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
	if(levelSetList->GetSelection() == -1) // Disable RH selector
	{
		levelList->Disable();

		levelSetName->Disable();
		levelSetCreator->Disable();
		levelSetName->SetValue(wxT(""));
		levelSetCreator->SetValue(wxT(""));
		levelSetDataSet->Disable();

		deleteLevelSetButton->Disable();

		levelSeries->Disable();
		levelNumber->Disable();
		levelDataSet->Disable();
		levelSeries->SetValue(wxT(""));
		levelNumber->SetValue(wxT(""));
	}
	else
	{
		levelList->Enable();
		levelList->Clear();

		levelSetName->Enable();
		levelSetCreator->Enable();
		levelSetDataSet->Enable();

		deleteLevelSetButton->Enable();

		map<wxString, LevelSetMetadata>::iterator iter = levelSets.begin();
		for( // Move iterator to correct element
				int i = 0;
				(i < levelSetList->GetSelection()) && iter != levelSets.end();
				(i++, iter++));
		cout << "Level set: " << iter->second.setName.mb_str() << endl;

		currentLevels = &iter->second.levels;
		currentLevelSet = &iter->second;
		currentLevels->sort(sortCurrentLevels);
		levelSetName->SetValue(currentLevelSet->setName);
		levelSetCreator->SetValue(currentLevelSet->creatorName);

		for(list<LevelMetadata>::iterator iter = currentLevels->begin(); iter != currentLevels->end(); iter++)
		{
			wxString fileName = wxFileName::FileName(iter->levelFileName).GetName();

			bool resave = false;
			if(!fileName.Contains(wxT(" ")))
			{
				fileName += wxT(" 1");
				resave = true;
			}

			int pos = fileName.Find(' ', true);

			wxString series = fileName.Mid(0, pos);
			wxString number = fileName.Mid(pos + 1);

			long tmpNum;
			wxCommandEvent tmpEvt;
			if(!number.ToLong(&tmpNum))
			{
				number = wxT("1");
				OnLevelNameSet(tmpEvt); // Save new level name
			}
			if(resave)
				OnLevelNameSet(tmpEvt);
			levelList->Append(series + wxT(" - ") + number + wxT(" - ") + (iter->levelName != wxT("") ? iter->levelName : wxString(_("Untitled"))));
		}
	}
}

void LevelSetManager::OnLevelItemSelected(wxCommandEvent& event)
{
	if(event.GetSelection() == -1) // Disable RH selector
	{
		levelSeries->Disable();
		levelNumber->Disable();
		levelDataSet->Disable();
		levelSeries->SetValue(wxT(""));
		levelNumber->SetValue(wxT(""));
	}
	else
	{
		list<LevelMetadata>::iterator iter = currentLevels->begin();
		for( // Move iterator to correct element
				int i = 0;
				(i < event.GetSelection()) && iter != currentLevels->end();
				(i++, iter++));

		currentLevel = &*iter;

		if(!openLevels.isOpen(iter->levelFileName))
		{
			levelSeries->Enable();
			levelNumber->Enable();
			levelDataSet->Enable();

			wxString fileName = wxFileName::FileName(iter->levelFileName).GetName();

			bool resave = false;
			if(!fileName.Contains(wxT(" ")))
			{
				fileName += wxT(" 1");
				resave = true;
			}

			int pos = fileName.Find(' ', true);

			wxString series = fileName.Mid(0, pos);
			wxString number = fileName.Mid(pos + 1);

			long tmpNum;
			wxCommandEvent tmpEvt;
			if(!number.ToLong(&tmpNum))
			{
				number = wxT("1");
				OnLevelNameSet(tmpEvt); // Save new level name
			}
			if(resave)
				OnLevelNameSet(tmpEvt);

			levelSeries->SetValue(series);
			levelNumber->SetValue(number);
		}
		else
		{
			levelSeries->Disable();
			levelNumber->Disable();
			levelDataSet->Disable();
			levelSeries->SetValue(wxT(""));
			levelNumber->SetValue(wxT(""));
		}
	}
}

void LevelSetManager::OnLevelItemDblClicked(wxCommandEvent& event)
{
	if(event.GetSelection() == -1) // Disable RH selector
	{
		levelSeries->Disable();
		levelNumber->Disable();
		levelDataSet->Disable();
		levelSeries->SetValue(wxT(""));
		levelNumber->SetValue(wxT(""));
	}
	else
	{
		list<LevelMetadata>::iterator iter = currentLevels->begin();
		for( // Move iterator to correct element
				int i = 0;
				(i < event.GetSelection()) && iter != currentLevels->end();
				(i++, iter++));

		currentLevel = &*iter;

		if(!openLevels.isOpen(iter->levelFileName))
		{
			openLevels.openLevel(currentLevelSet->setName, currentLevel->levelFileName);
		}
		else
		{
			levelSeries->Disable();
			levelNumber->Disable();
			levelDataSet->Disable();
			levelSeries->SetValue(wxT(""));
			levelNumber->SetValue(wxT(""));
			wxMessageDialog(this, _("Level already open"), _("Level already open")).ShowModal();
		}
	}
}

void LevelSetManager::OnLevelNameSet(wxCommandEvent& WXUNUSED(event))
{
	long tmpNum;
	if(!levelNumber->GetValue().ToLong(&tmpNum))
	{
		wxMessageDialog(this, _("Incorrect number specified in series"), _("Incorrect number")).ShowModal();
		return;
	}
	if(levelSeries->IsEmpty())
	{
		wxMessageDialog(this, _("Please enter a level series"), _("Please enter a series")).ShowModal();
		return;
	}

	wxFileName newName(currentLevel->levelFileName);
	newName.SetName(levelSeries->GetValue() + wxT(" ") + levelNumber->GetValue());
	if(wxFileExists(newName.GetFullPath()))
	{
		wxMessageDialog(this, _("This level already exists"), _("Level already exists")).ShowModal();
		return;
	}
	wxRenameFile(currentLevel->levelFileName, newName.GetFullPath(), false);
	currentLevel->levelFileName = newName.GetFullPath();

	// Refresh
	wxCommandEvent tmpEvt;
	OnLevelSetItemSelected(tmpEvt); // Retrigger event to regenerate list. Probably could be done better
}

void LevelSetManager::OnLevelSetNameSet(wxCommandEvent& event)
{
	if(levelSetName->IsEmpty())
	{
		wxMessageDialog(this, _("Please enter a level set name"), _("Please enter a name")).ShowModal();
		return;
	}
	if(levelSetCreator->IsEmpty())
	{
		wxMessageDialog(this, _("Please enter a level set creator"), _("Please enter a set creator")).ShowModal();
		return;
	}

	currentLevelSet->setName = levelSetName->GetValue();
	currentLevelSet->creatorName = levelSetCreator->GetValue();
	currentLevelSet->saveMetadata();
	syncListsToScreen();
}

bool LevelSetManager::sortCurrentLevels(LevelMetadata first, LevelMetadata second)
{
	return first.levelFileName < second.levelFileName;
}


LevelSetMetadata::LevelSetMetadata(wxString folderName) :
	folderName(folderName),
	metaFileName(wxString(Misc::Data::saveLocation.c_str(), wxConvUTF8) + wxT("/") + folderName + wxT("/") + wxT(LEVELSET_META_NAME))
{
	wxMkdir(wxString(Misc::Data::saveLocation.c_str(), wxConvUTF8) + wxT("/") + folderName);

	wxTextFile config(metaFileName);
	if(config.Open())
	{ // Process config file
		for(wxString line = config.GetFirstLine(); !config.Eof(); line = config.GetNextLine())
		{
			wxStringTokenizer tkz(line, wxT(":"));
			if(!tkz.HasMoreTokens()) continue; // Malformed line
			wxString key = tkz.GetNextToken();

			if(!tkz.HasMoreTokens()) continue; // Malformed line
			wxString value = tkz.GetNextToken();

			if(key.Cmp(wxT("setname")) == 0)
			{
				setName = value;
			}
			if(key.Cmp(wxT("authour")) == 0)
			{
				creatorName = value;
			}
		}
		config.Close();
	}
	else
	{
		config.Create();
		setName = _("New Level Set");
		creatorName = _("Me");
		saveMetadata();
	}

}

LevelSetMetadata::LevelSetMetadata(wxString folderName, wxString setName, wxString creatorName) :
	folderName(folderName),
	setName(setName),
	creatorName(creatorName),
	metaFileName(wxString(Misc::Data::saveLocation.c_str(), wxConvUTF8) + wxT("/") + folderName + wxT("/") + wxT(LEVELSET_META_NAME))
{
	wxMkdir(wxString(Misc::Data::saveLocation.c_str(), wxConvUTF8) + wxT("/") + folderName);

	wxTextFile config(metaFileName);
	config.Create();
	saveMetadata();
}

void LevelSetMetadata::saveMetadata()
{
	wxTextFile config(metaFileName);
	if(config.Open())
	{
		config.Clear();
		config.AddLine(wxT("setname:") + setName);
		config.AddLine(wxT("authour:") + creatorName);

		config.Write();
	}
	else
		cout << "ERROR: Couldn't save configuration to file!" << endl;

}

LevelMetadata::LevelMetadata()
{}

LevelMetadata::LevelMetadata(wxString levelFileName) :
	levelFileName(levelFileName)
{
	Levels::LevelReader reader;
	string sLevelName, sCreatorName;
	if(!reader.open(string(levelFileName.mb_str()), sLevelName, sCreatorName))
		cout << "Couldn't load " << levelFileName << "!" << endl;
	levelName = wxString(sLevelName.c_str(), wxConvUTF8);
	creatorName = wxString(sLevelName.c_str(), wxConvUTF8);
}

