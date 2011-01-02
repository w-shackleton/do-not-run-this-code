#include "infoboxEditor.hpp"

#include <wx/sizer.h>
#include <wx/button.h>

using namespace Objects;
using namespace Objects::Helpers;

BEGIN_EVENT_TABLE(InfoBoxEditor, wxDialog)
	EVT_BUTTON(ID_Cancel_click, InfoBoxEditor::OnCancel)
	EVT_BUTTON(ID_Ok_click, InfoBoxEditor::OnOk)
END_EVENT_TABLE()

InfoBoxEditor::InfoBoxEditor(wxWindow* parent, std::string &text, bool &initialShow) :
	wxDialog(parent, -1, _("Edit Message box")),
	text(text),
	initialShow(initialShow)
{
	wxBoxSizer *vsizer = new wxBoxSizer(wxVERTICAL);

	textEdit = new wxTextCtrl(this, -1, wxString(text.c_str(), wxConvUTF8), wxDefaultPosition, wxSize(200, 100), wxTE_MULTILINE);
	vsizer->Add(textEdit);

	initialShowBox = new wxCheckBox(this, -1, _("Show at level start"));
	initialShowBox->SetValue(initialShow);
	vsizer->Add(initialShowBox);

	wxBoxSizer *hsizer = new wxBoxSizer(wxHORIZONTAL);
	hsizer->Add(new wxButton(this, ID_Cancel_click, _("&Cancel")), 1, wxEXPAND);
	SetEscapeId(ID_Cancel_click);
	hsizer->Add(new wxButton(this, ID_Ok_click, _("O&k")), 1, wxEXPAND);

	vsizer->Add(hsizer, 1, wxEXPAND | wxALL, 5);
	SetSizer(vsizer);
	vsizer->SetSizeHints(this);
}

void InfoBoxEditor::OnCancel(wxCommandEvent& event)
{
	EndModal(1);
}

void InfoBoxEditor::OnOk(wxCommandEvent& event)
{
	text = textEdit->GetValue().mb_str();
	initialShow = initialShowBox->GetValue();
	EndModal(0);
}
