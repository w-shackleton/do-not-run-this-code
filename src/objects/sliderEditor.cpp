#include "sliderEditor.hpp"

#include <wx/sizer.h>
#include <wx/button.h>

#include <vector>

#include <wx/wx.h>

using namespace Objects;
using namespace Objects::Helpers;

BEGIN_EVENT_TABLE(SliderEditor, wxDialog)
	EVT_BUTTON(ID_Cancel_click, SliderEditor::OnCancel)
	EVT_BUTTON(ID_Ok_click, SliderEditor::OnOk)
END_EVENT_TABLE()

SliderEditor::SliderEditor(std::vector<double> &nums) :
	wxDialog(parent, -1, _("Edit Message box")),
	text(text),
	initialShow(initialShow)
{
	wxBoxSizer *vsizer = new wxBoxSizer(wxVERTICAL);

	textEdit = new wxTextCtrl(this, -1, wxString(text.c_str(), wxConvUTF8), wxDefaultPosition, wxDefaultSize, wxTE_MULTILINE);
	vsizer->Add(textEdit);

	initialShowBox = new wxCheckBox(this, -1, _("Show at level start"));
	initialShowBox->SetValue(initialShow);
	vsizer->Add(initialShowBox);

	wxBoxSizer *hsizer = new wxBoxSizer(wxHORIZONTAL);
	hsizer->Add(new wxButton(this, ID_Cancel_click, _("&Cancel")));
	SetEscapeId(ID_Cancel_click);
	hsizer->Add(new wxButton(this, ID_Ok_click, _("O&k")));

	vsizer->Add(hsizer);
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
}
