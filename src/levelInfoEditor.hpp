#ifndef LEVELINFOEDITOR_H
#define LEVELINFOEDITOR_H

#include <wx/dialog.h>
#include <wx/textctrl.h>

#include "levelrw/levelManager.hpp"

class LevelInfoEditor : public wxDialog
{
	public:
		LevelInfoEditor(Levels::LevelManager &lmanager);
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
		Levels::LevelManager &lmanager;
};

#endif
