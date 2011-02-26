#include "newLevelDialog.hpp"

#include <iostream>

#include <wx/sizer.h>
#include <wx/button.h>
#include <wx/stattext.h>
#include <wx/msgdlg.h>

using namespace std;

BEGIN_EVENT_TABLE(NewLevelDialog , wxDialog)                  
        EVT_BUTTON(ID_Ok_click, NewLevelDialog::OnOk)        
        EVT_BUTTON(ID_Cancel_click, NewLevelDialog::OnCancel)
END_EVENT_TABLE()                                          

NewLevelDialog::NewLevelDialog() :
	wxDialog(NULL, -1, _("Create New Level Set"))
{
	wxBoxSizer* vsizer = new wxBoxSizer(wxVERTICAL);

	wxGridSizer *prefGrid = new wxGridSizer(2);
	prefGrid->Add(new wxStaticText(this, -1, _("Level Set Title:")), 0, wxALL | wxALIGN_CENTRE, 5);
	title = new wxTextCtrl(this, -1, wxT(""), wxDefaultPosition, wxSize(180, -1));
	prefGrid->Add(title, 1, wxALL | wxEXPAND, 5);

	prefGrid->Add(new wxStaticText(this, -1, _("Creator:")), 0, wxALL | wxALIGN_CENTRE, 5);
	creator = new wxTextCtrl(this, -1, wxT(""), wxDefaultPosition, wxSize(180, -1));
	prefGrid->Add(creator, 1, wxALL | wxEXPAND, 5);

	vsizer->Add(prefGrid);
	prefGrid->SetSizeHints(this);

	wxBoxSizer* hsizer = new wxBoxSizer(wxHORIZONTAL);                           
	hsizer->Add(new wxButton(this, ID_Cancel_click, _("&Cancel")), 1, wxALL, 5);
	SetEscapeId(ID_Cancel_click);                                               
	hsizer->Add(new wxButton(this, ID_Ok_click, _("O&k")), 1, wxALL, 5);        
	                                                                            
	vsizer->Add(hsizer, 1, wxEXPAND);                                                        
	hsizer->SetSizeHints(this);                                                 
	SetSizer(vsizer);                                                           
	vsizer->SetSizeHints(this);                                                 
}

void NewLevelDialog::OnCancel(wxCommandEvent& event)
{
	EndModal(1);
}

void NewLevelDialog::OnOk(wxCommandEvent& event)
{
	if(title->IsEmpty() || creator->IsEmpty())
	{
		wxMessageDialog dlg(this, _("Please enter a title and creator"), wxT(""));
		dlg.ShowModal();
		return;
	}
	levelTitle = title->GetValue();
	levelCreator = creator->GetValue();
	EndModal(0);
}
