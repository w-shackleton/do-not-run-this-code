#include "levelInfoEditor.hpp"

#include <iostream>

using namespace std;
using namespace Levels;

BEGIN_EVENT_TABLE(LevelInfoEditor, wxDialog)                  
        EVT_BUTTON(ID_Cancel_click, LevelInfoEditor::OnCancel)
        EVT_BUTTON(ID_Ok_click, LevelInfoEditor::OnOk)        
END_EVENT_TABLE()                                          

LevelInfoEditor::LevelInfoEditor(LevelManager &lmanager) :
	wxDialog(NULL, -1, _("Edit Level Info"))
{
	wxBoxSizer hsizer = new wxBoxSizer(wxHORIZONTAL);                           
	hsizer->Add(new wxButton(this, ID_Cancel_click, _("&Cancel")), 1, wxALL, 5);
	SetEscapeId(ID_Cancel_click);                                               
	hsizer->Add(new wxButton(this, ID_Ok_click, _("O&k")), 1, wxALL, 5);        
	                                                                            
	vsizer->Add(hsizer);                                                        
	hsizer->SetSizeHints(this);                                                 
	SetSizer(vsizer);                                                           
	vsizer->SetSizeHints(this);                                                 
}

void LevelInfoEditor::OnCancel(wxCommandEvent& event)
{
}

void LevelInfoEditor::OnOk(wxCommandEvent& event)
{
}
