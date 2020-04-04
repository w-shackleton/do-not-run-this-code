#include "PrefDialog.hpp"
#include <iostream>
using namespace std;

#include <wx/xrc/xmlres.h>
#include <wx/notebook.h>

#include "wxstatfuncs.hpp"
#include "main.hpp"

BEGIN_EVENT_TABLE(PrefDialog, wxDialog)
	EVT_BUTTON(XRCID("prefButtonSave"), PrefDialog::onButtonSavePressed)
	EVT_BUTTON(XRCID("prefButtonCancel"), PrefDialog::onButtonCancelPressed)
END_EVENT_TABLE()

PrefDialog::PrefDialog(const wxString& title)
: wxDialog(NULL, -1, title)
{
	cout << "          Loading Preferences... " << endl;
	prefPanel = wxXmlResource::Get()->LoadPanel(this, wxT("prefPanel"));
	cout << "          * Done!" << endl;
	
	Fit();
	
	cout << "Loaded." << endl;
}

void PrefDialog::onButtonSavePressed(wxCommandEvent& WXUNUSED(event))
{
	Close(TRUE);
}

void PrefDialog::onButtonCancelPressed(wxCommandEvent& WXUNUSED(event))
{
	Close(TRUE);
}
