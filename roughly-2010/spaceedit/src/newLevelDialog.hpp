#ifndef NEWLEVELDIALOG_H
#define NEWLEVELDIALOG_H

#include <wx/dialog.h>
#include <wx/textctrl.h>

class NewLevelDialog : public wxDialog
{
	public:
		NewLevelDialog();

		wxString levelTitle, levelCreator;
	protected:
		DECLARE_EVENT_TABLE();

		enum
		{
			ID_Ok_click = wxID_OK,
			ID_Cancel_click = wxID_CANCEL,
		};
		void OnCancel(wxCommandEvent& event);
		void OnOk(wxCommandEvent& event);

		wxTextCtrl *title, *creator;
};

#endif
