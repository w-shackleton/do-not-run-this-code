#include "preferences.hpp"

#include <iostream>

#include <wx/sizer.h>
#include <wx/button.h>
#include <wx/stattext.h>
#include <wx/dirdlg.h>

#include "misc/data.hpp"
#include "cairoPanel.hpp" // For CAIRO_NATIVE

using namespace std;

BEGIN_EVENT_TABLE(Preferences, wxDialog)                  
        EVT_BUTTON(ID_Ok_click, Preferences::OnOk)        
        EVT_BUTTON(ID_Cancel_click, Preferences::OnCancel)
        EVT_BUTTON(ID_Set_save_location, Preferences::OnSetSaveLocation)
END_EVENT_TABLE()                                          

Preferences::Preferences() :
	wxDialog(NULL, -1, wxString(_("Preferences")))
{
	wxBoxSizer* vsizer = new wxBoxSizer(wxVERTICAL);

	wxGridSizer *prefGrid = new wxGridSizer(2);

	prefGrid->Add(new wxStaticText(this, -1, _("Level Save Location:")), 0, wxALL | wxALIGN_CENTRE, 5);

	wxBoxSizer *holder = new wxBoxSizer(wxHORIZONTAL);
	saveLocationText = new wxTextCtrl(this, -1, wxString(Misc::Data::saveLocation.c_str(), wxConvUTF8), wxDefaultPosition, wxSize(180, -1));
	holder->Add(saveLocationText, 1, wxALL | wxEXPAND, 5);
	holder->Add(new wxButton(this, ID_Set_save_location, _("Set Location")), 0, wxALL, 5);

	prefGrid->Add(holder, 1, wxALL | wxEXPAND, 5);

	prefGrid->Add(new wxStaticText(this, -1, _("Use native rendering (sometimes faster)")), 0, wxALL | wxALIGN_CENTRE, 5);
	nativeRendering = new wxCheckBox(this, -1, wxT(""));
	nativeRendering->SetValue(Misc::Data::nativeRendering);
#ifndef CAIRO_NATIVE
	nativeRendering->Disable();
#endif
	prefGrid->Add(nativeRendering, 1, wxALL | wxEXPAND, 5);

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

void Preferences::OnCancel(wxCommandEvent& event)
{
	EndModal(1);
}

void Preferences::OnOk(wxCommandEvent& event)
{
	Misc::Data::saveLocation = saveLocationText->GetValue().mb_str();
	Misc::Data::nativeRendering = nativeRendering->GetValue();
	Misc::Data::savePreferences();
	EndModal(0);
}

void Preferences::OnSetSaveLocation(wxCommandEvent& event)
{
	wxDirDialog dir(this, wxT("Choose location to save level sets"), saveLocationText->GetValue());
	dir.ShowModal();
	saveLocationText->SetValue(dir.GetPath());
}

