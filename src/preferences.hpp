#ifndef PREFERENCES_H
#define PREFERENCES_H

#include <wx/dialog.h>
#include <wx/textctrl.h>
#include <wx/checkbox.h>

class Preferences : public wxDialog
{
	public:
		Preferences();
	protected:
		DECLARE_EVENT_TABLE();

		enum
		{
			ID_Ok_click = wxID_OK,
			ID_Cancel_click = wxID_CANCEL,
			ID_Set_save_location = wxID_HIGHEST + 1,
		};
		void OnCancel(wxCommandEvent& event);
		void OnOk(wxCommandEvent& event);
		void OnSetSaveLocation(wxCommandEvent& event);

		wxTextCtrl *saveLocationText;
		wxCheckBox *nativeRendering;
};

#endif
