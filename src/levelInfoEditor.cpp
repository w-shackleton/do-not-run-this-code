#include "levelInfoEditor.hpp"

#include <iostream>

#include <wx/sizer.h>
#include <wx/button.h>
#include <wx/stattext.h>

using namespace std;
using namespace Levels;

BEGIN_EVENT_TABLE(LevelInfoEditor, wxDialog)                  
        EVT_BUTTON(ID_Ok_click, LevelInfoEditor::OnOk)        
        EVT_BUTTON(ID_Cancel_click, LevelInfoEditor::OnCancel)
END_EVENT_TABLE()                                          

LevelInfoEditor::LevelInfoEditor(LevelManager &lmanager, bool newLevel) :
	wxDialog(NULL, -1, wxString(newLevel ? _("Edit Level Info") : _("Create New Level"))),
	lmanager(lmanager)
{
	wxBoxSizer* vsizer = new wxBoxSizer(wxVERTICAL);

	wxGridSizer *prefGrid = new wxGridSizer(2);
	prefGrid->Add(new wxStaticText(this, -1, _("Level Title:")), 0, wxALL | wxALIGN_CENTRE, 5);
	title = new wxTextCtrl(this, -1, wxString(lmanager.levelName.c_str(), wxConvUTF8), wxDefaultPosition, wxSize(180, -1));
	prefGrid->Add(title, 1, wxALL | wxEXPAND, 5);

	prefGrid->Add(new wxStaticText(this, -1, _("Author:")), 0, wxALL | wxALIGN_CENTRE, 5);
	creator = new wxTextCtrl(this, -1, wxString(lmanager.creator.c_str(), wxConvUTF8), wxDefaultPosition, wxSize(180, -1));
	prefGrid->Add(creator, 1, wxALL | wxEXPAND, 5);

	prefGrid->Add(new wxStaticText(this, -1, _("Number of stars to collect:")), 0, wxALL | wxALIGN_CENTRE, 5);
	stars = new wxTextCtrl(this, -1, wxString::Format(wxT("%d"), lmanager.numberStars), wxDefaultPosition, wxSize(180, -1));
	prefGrid->Add(stars, 1, wxALL | wxEXPAND, 5);

	vsizer->Add(prefGrid);
	prefGrid->SetSizeHints(this);

	wxBoxSizer* hsizer = new wxBoxSizer(wxHORIZONTAL);                           
	if(newLevel) hsizer->Add(new wxButton(this, ID_Cancel_click, _("&Cancel")), 1, wxALL, 5);
	SetEscapeId(ID_Cancel_click);                                               
	hsizer->Add(new wxButton(this, ID_Ok_click, _("O&k")), 1, wxALL, 5);        
	                                                                            
	vsizer->Add(hsizer, 1, wxEXPAND);                                                        
	hsizer->SetSizeHints(this);                                                 
	SetSizer(vsizer);                                                           
	vsizer->SetSizeHints(this);                                                 
}

void LevelInfoEditor::OnCancel(wxCommandEvent& event)
{
	EndModal(1);
}

void LevelInfoEditor::OnOk(wxCommandEvent& event)
{
	lmanager.change(); // We have changed something
	lmanager.levelName = title->GetValue().mb_str();
	lmanager.creator = creator->GetValue().mb_str();

	stars->GetValue().ToLong((long*)&lmanager.numberStars);

	EndModal(0);
}
