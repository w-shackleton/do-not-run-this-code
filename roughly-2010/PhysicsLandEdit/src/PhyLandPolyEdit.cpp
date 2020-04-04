#include "PhyLandPolyEdit.h"
#include <iostream>
using namespace std;

BEGIN_EVENT_TABLE(PhyLandPolyEdit, wxDialog)
	
	EVT_BUTTON(ID_Validate, PhyLandPolyEdit::Validate)
	EVT_BUTTON(ID_Save, PhyLandPolyEdit::SaveClose)
	
END_EVENT_TABLE()

PhyLandPolyEdit::PhyLandPolyEdit(const wxString& title)
: wxDialog( NULL, -1, title, wxDefaultPosition, wxSize(POLYEDIT_WINSIZE, POLYEDIT_WINSIZE))
{
	area = new PhyLandPEArea(this);
	PolyEditSetup();
}

PhyLandPolyEdit::PhyLandPolyEdit(const wxString& title, wxPoint existing[], int count)
: wxDialog( NULL, -1, title, wxDefaultPosition, wxSize(POLYEDIT_WINSIZE, POLYEDIT_WINSIZE))
{
	area = new PhyLandPEArea(this, existing, count);
	PolyEditSetup();
}

void PhyLandPolyEdit::PolyEditSetup()
{
	vcontainer = new wxBoxSizer(wxVERTICAL);
	
	area->receiveMsgHandle(this);
	vcontainer->Add(area, 1, wxEXPAND);
	
	hcontainer = new wxBoxSizer(wxHORIZONTAL);
	b = new wxButton(this, ID_Validate, _("Validate"));
	save = new wxButton(this, ID_Save, _("Save"));
	save->Disable();
	hcontainer->Add(b, 0, 0, 10);
	hcontainer->Add(save, 0, 0, 10);
	vcontainer->Add(hcontainer);
	
	SetSizer(vcontainer);
}

void PhyLandPolyEdit::Validate(wxCommandEvent& event)
{
	area->validate();
}

void PhyLandPolyEdit::SaveClose(wxCommandEvent& event)
{
	for(int i = 0; i < POLY_MAX_POINTS; i++)
	{
		resultPoints[i] = area->points[i];
	}
	resultCount = area->currPoint;
	EndModal(2);
}

void PhyLandPolyEdit::sendMsg(int what)
{
	//cout << "!" << endl;
	if(what == MSG_Poly_Inv)
	{
		save->Disable();
		b->Enable();
	}
	else if(what == MSG_Poly_Val)
	{
		save->Enable();
		b->Disable();
		
	}
}
