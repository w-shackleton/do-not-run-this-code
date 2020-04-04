#include "sliderEditor.hpp"

#include <wx/sizer.h>
#include <wx/button.h>

using namespace Objects;
using namespace Objects::Helpers;
using namespace std;

BEGIN_EVENT_TABLE(SliderEditor, wxDialog)
	EVT_BUTTON(ID_Cancel_click, SliderEditor::OnCancel)
	EVT_BUTTON(ID_Ok_click, SliderEditor::OnOk)
END_EVENT_TABLE()

Slider::Slider(double *value, int min, int max, std::string title) :
	value(value),
	min(min),
	max(max),
	title(title)
{
}

SliderEditor::SliderEditor(std::vector<Slider> &nums, std::string title) :
	wxDialog(NULL, -1, wxString(title.c_str(), wxConvUTF8)),
	nums(nums)
{
	wxBoxSizer *vsizer = new wxBoxSizer(wxVERTICAL);

	for(int i = 0; i < nums.size(); i++)
	{
		wxBoxSizer *container = new wxBoxSizer(wxHORIZONTAL);

		wxStaticText *label = new wxStaticText(this, -1, wxString(nums[i].title.c_str(), wxConvUTF8));
		container->Add(label);

		wxSlider *slider = new wxSlider(this, -1, *nums[i].value, nums[i].min, nums[i].max);
		sliders.push_back(slider);
		container->Add(slider, 1, wxEXPAND);

		container->SetSizeHints(this);
		vsizer->Add(container, 0, wxEXPAND | wxALL, 5);

		container->Fit(this);
	}

	wxBoxSizer *hsizer = new wxBoxSizer(wxHORIZONTAL);
	hsizer->Add(new wxButton(this, ID_Cancel_click, _("&Cancel")), 1, wxALL, 5);
	SetEscapeId(ID_Cancel_click);
	hsizer->Add(new wxButton(this, ID_Ok_click, _("O&k")), 1, wxALL, 5);

	vsizer->Add(hsizer);
	hsizer->SetSizeHints(this);
	SetSizer(vsizer);
	vsizer->SetSizeHints(this);
}

void SliderEditor::OnCancel(wxCommandEvent& event)
{
	EndModal(1);
}

void SliderEditor::OnOk(wxCommandEvent& event)
{
	EndModal(0);
	for(int i = 0; i < nums.size(); i++)
	{
		*nums[i].value = sliders[i]->GetValue();
	}
}
