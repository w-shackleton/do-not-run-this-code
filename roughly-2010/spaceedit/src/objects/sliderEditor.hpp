#ifndef SLIDER_EDITOR_H
#define SLIDER_EDITOR_H

#include <vector>
#include <string>

#include <wx/dialog.h>
#include <wx/slider.h>
#include <wx/stattext.h>

namespace Objects
{
	namespace Helpers
	{
		class Slider
		{
			public:
				Slider(double *value, int min, int max, std::string title);
				double *value;
				int min;
				int max;
				std::string title;
		};

		class SliderEditor : public wxDialog
		{
			public:
				SliderEditor(std::vector<Slider> &nums, std::string title);
				DECLARE_EVENT_TABLE();
			protected:
				enum
				{
					ID_Cancel_click = wxID_CANCEL,
					ID_Ok_click = wxID_OK,
				};

				void OnCancel(wxCommandEvent& event);
				void OnOk(wxCommandEvent& event);
				
				std::vector<wxSlider *> sliders;
				std::vector<Slider> &nums;

			private:
		};
	}
}

#endif
