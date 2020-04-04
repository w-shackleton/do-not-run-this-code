#ifndef PREF_DIALOG_H
#define PREF_DIALOG_H

#include <wx/wx.h>

class PrefDialog: public wxDialog
{
protected:
	wxPanel *prefPanel;
	
	void onButtonSavePressed(wxCommandEvent& WXUNUSED(event));
	void onButtonCancelPressed(wxCommandEvent& WXUNUSED(event));
public:
	PrefDialog(const wxString& title);
	
	DECLARE_EVENT_TABLE()
};
#endif
