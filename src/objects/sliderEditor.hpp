#ifndef INFOBOX_EDITOR_H
#define INFOBOX_EDITOR_H

#include <wx/dialog.h>

namespace Objects
{
	namespace Helpers
	{
		class SliderEditor : public wxDialog
		{
			public:
				SliderEditor(std::vector<double> &nums);
				DECLARE_EVENT_TABLE();
			protected:
				enum
				{
					ID_Cancel_click = wxID_HIGHEST + 1,
					ID_Ok_click,
				};

				void OnCancel(wxCommandEvent& event);
				void OnOk(wxCommandEvent& event);
			private:
		};

		typedef struct
		{
		} Slider;
	}
}

#endif
