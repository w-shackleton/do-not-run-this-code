#ifndef INFOBOX_EDITOR_H
#define INFOBOX_EDITOR_H

#include <wx/dialog.h>
#include <wx/textctrl.h>
#include <wx/checkbox.h>

namespace Objects
{
	namespace Helpers
	{
		class InfoBoxEditor : public wxDialog
		{
			public:
				InfoBoxEditor(wxWindow* parent, std::string &text, bool &initialShow);
				DECLARE_EVENT_TABLE();
			protected:
				std::string &text;
				bool &initialShow;

				enum
				{
					ID_Cancel_click = wxID_CANCEL,
					ID_Ok_click = wxID_OK,
				};

				void OnCancel(wxCommandEvent& event);
				void OnOk(wxCommandEvent& event);
			private:
				wxTextCtrl *textEdit;
				wxCheckBox *initialShowBox;
		};
	}
}

#endif
