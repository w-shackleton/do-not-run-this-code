#include "PhyLandFrame.h"
#include "config.h"
#include <iostream>
using namespace std;

BEGIN_EVENT_TABLE(PhyLandFrame, wxFrame)
	EVT_MENU(ID_File_Quit, PhyLandFrame::OnQuit)
	EVT_MENU(ID_Help_About, PhyLandFrame::OnAbout)
	//EVT_COMMAND(ID_Item_Changed, plaEVT_CHANGED, PhyLandFrame::plaChanged)
	EVT_SPINCTRL(ID_Value_Changing, PhyLandFrame::valueChanged)
	
	EVT_BUTTON(ID_Tool_Circle, PhyLandFrame::AddCircle)
	EVT_BUTTON(ID_Tool_Rect, PhyLandFrame::AddRect)
	EVT_BUTTON(ID_Tool_Poly, PhyLandFrame::AddPoly)
	EVT_BUTTON(ID_Tool_Joint_Dist, PhyLandFrame::AddjDist)
	
END_EVENT_TABLE()

PhyLandFrame::PhyLandFrame(const wxString& title, const wxPoint& pos, const wxSize& size)
: wxFrame( NULL, -1, title, pos, size )
{
	//panel = new wxPanel(this, wxID_ANY);
	wxMenu *menuFile = new wxMenu;
	wxMenu *menuHelp = new wxMenu;
	
	menuFile->Append(ID_File_Quit, _("E&xit"));
	menuHelp->Append(ID_Help_About, _("&About"));
	
	wxMenuBar *menuBar = new wxMenuBar;
	menuBar->Append(menuFile,_("&File"));
	menuBar->Append(menuHelp,_("&Help"));
	
	SetMenuBar(menuBar);
	wxToolBar *toolbar = CreateToolBar();
	circleB = new wxButton(toolbar, ID_Tool_Circle, _("Add Circle"));
	rectB = new wxButton(toolbar, ID_Tool_Rect, _("Add Rect"));
	polyB = new wxButton(toolbar, ID_Tool_Poly, _("Add Polygon"));
	jDistB = new wxButton(toolbar, ID_Tool_Joint_Dist, _("Distance Joint"));
        toolbar->AddControl(circleB);
        toolbar->AddControl(rectB);
        toolbar->AddControl(polyB);
        toolbar->AddSeparator();
        toolbar->AddControl(jDistB);
	toolbar->Realize();

	
	CreateStatusBar();
	SetStatusText(_(APP_NAME));
	////////////////////////////////////////////////
	hcontainer = new wxBoxSizer(wxHORIZONTAL);
	
	pla = new PhyLandArea(this);
	hcontainer->Add(pla, 1, wxEXPAND);
	
	//hcontainer->Add(bt);
	setupParts();
	//wxPanel *propPanel = new wxPanel(this, -1);
	hcontainer->Add(circleProps, 0, wxALL | wxEXPAND, 5);
	hcontainer->Add(rectProps, 0, wxALL | wxEXPAND, 5);
	hcontainer->Add(polyProps, 0, wxALL | wxEXPAND, 5);
	hcontainer->Hide(circleProps);
	hcontainer->Hide(rectProps);
	hcontainer->Hide(polyProps);
	
	SetBackgroundStyle(wxBG_STYLE_SYSTEM);
	
	SetSizer(hcontainer);
	SetAutoLayout(true);
	pla->receiveMsgHandle(this);
}

void PhyLandFrame::OnQuit(wxCommandEvent& WXUNUSED(event))
{
	Close(TRUE);
}

void PhyLandFrame::OnAbout(wxCommandEvent& WXUNUSED(event))
{
	wxMessageBox(wxT("Data Dir: ") + getPathData(),
				  _("About"),
				  wxOK | wxICON_INFORMATION, this);
	if(
		wxFileExists(getPathData() + wxT("/Pattern1.png"))
	  )
	{
		wxMessageBox(wxT("File found @ " + getPathData() + wxT("/Pattern1.png")),
					  _("About"),
					  wxOK | wxICON_INFORMATION, this);
	}
	else
	{
		wxMessageBox(wxT("File not found @ " + getPathData() + wxT("/Pattern1.png")),
					  _("About"),
					  wxOK | wxICON_INFORMATION, this);
	}
}

void PhyLandFrame::OnButton(wxCommandEvent& WXUNUSED(event))
{
	wxColourData* colD = new wxColourData();
	if(pla->currSelected == 0)
		iCol = new wxColourDialog(this);
	else
	{
		colD->SetColour(pla->currItem->colour);
		iCol = new wxColourDialog(this, colD);
	}
	if(iCol->ShowModal() == wxID_OK)
	{
		wxColour col = iCol->GetColourData().GetColour();
		pla->currItem->colour = col;
		cout << "Colour set to {" << (int)col.Red() << ","<< (int)col.Green() << ","<< (int)col.Blue() << "}." << endl;
		pla->Refresh();
	}
}

void PhyLandFrame::setupParts()
{
	setupCircle();
	setupRect();
	setupPoly();
}

void PhyLandFrame::setupCircle()
{
	circleProps = new wxFlexGridSizer(5, 2, 9, 25);
	
	wxStaticText *radiusT = new wxStaticText(this, -1, _("Radius"));
	wxStaticText *densityT = new wxStaticText(this, -1, _("Density"));
	wxStaticText *restT = new wxStaticText(this, -1, _("Bounciness"));
	wxStaticText *colT = new wxStaticText(this, -1, _("Colour"));
	wxStaticText *fixedT = new wxStaticText(this, -1, wxT(""));
	
	cRadius = new wxSpinCtrl(this, ID_Value_Changing);
	cRadius->SetRange(MIN_SIZE, MAX_SIZE);
	cDensity = new wxSpinCtrl(this, ID_Value_Changing);
	cDensity->SetRange(1, 20);
	cRest = new wxSpinCtrl(this, ID_Value_Changing);
	cRest->SetRange(1, 10);
	
	wxButton *colbutton = new wxButton(this, ID_ColButton, _("Set Colour"));
	Connect(ID_ColButton, wxEVT_COMMAND_BUTTON_CLICKED, wxCommandEventHandler(PhyLandFrame::OnButton));
	
	cFixed = new wxCheckBox(this, ID_Fixed_Check, _("Fixed"));
	Connect(ID_Fixed_Check, wxEVT_COMMAND_CHECKBOX_CLICKED, wxCommandEventHandler(PhyLandFrame::OnFCheck));
	
	circleProps->Add(radiusT);
	circleProps->Add(cRadius);
	circleProps->Add(densityT);
	circleProps->Add(cDensity);
	circleProps->Add(restT);
	circleProps->Add(cRest);
	circleProps->Add(colT);
	circleProps->Add(colbutton);
	
	circleProps->Add(fixedT);
	circleProps->Add(cFixed);
	
	//circleProps->SetBackgroundStyle(wxBG_STYLE_SYSTEM);
}

void PhyLandFrame::setupRect()
{
	rectProps = new wxFlexGridSizer(8, 2, 9, 25);
	
	wxStaticText *widthT = new wxStaticText(this, -1, _("Width"));
	wxStaticText *heightT = new wxStaticText(this, -1, _("Height"));
	wxStaticText *rotT = new wxStaticText(this, -1, _("Rotation"));
	wxStaticText *densityT = new wxStaticText(this, -1, _("Density"));
	wxStaticText *restT = new wxStaticText(this, -1, _("Bounciness"));
	wxStaticText *fricT = new wxStaticText(this, -1, _("Friction"));
	wxStaticText *colT = new wxStaticText(this, -1, _("Colour"));
	wxStaticText *fixedT = new wxStaticText(this, -1, wxT(""));
	
	rWidth = new wxSpinCtrl(this, ID_Value_Changing);
	rWidth->SetRange(MIN_SIZE * 2, MAX_SIZE * 2);
	rHeight = new wxSpinCtrl(this, ID_Value_Changing);
	rHeight->SetRange(MIN_SIZE * 2, MAX_SIZE * 2);
	rRot = new wxSpinCtrl(this, ID_Value_Changing);
	rRot->SetRange(0, 360);
	rDensity = new wxSpinCtrl(this, ID_Value_Changing);
	rDensity->SetRange(0, 20);
	rRest = new wxSpinCtrl(this, ID_Value_Changing);
	rRest->SetRange(0, 10);
	rFric = new wxSpinCtrl(this, ID_Value_Changing);
	rFric->SetRange(0, 50);
	
	wxButton *colbutton = new wxButton(this, ID_ColButton, _("Set Colour"));
	Connect(ID_ColButton, wxEVT_COMMAND_BUTTON_CLICKED, wxCommandEventHandler(PhyLandFrame::OnButton));
	
	rFixed = new wxCheckBox(this, ID_Fixed_Check, _("Fixed"));
	Connect(ID_Fixed_Check, wxEVT_COMMAND_CHECKBOX_CLICKED, wxCommandEventHandler(PhyLandFrame::OnFCheck));
	
	rectProps->Add(widthT);
	rectProps->Add(rWidth);
	rectProps->Add(heightT);
	rectProps->Add(rHeight);
	rectProps->Add(rotT);
	rectProps->Add(rRot);
	
	rectProps->Add(densityT);
	rectProps->Add(rDensity);
	rectProps->Add(restT);
	rectProps->Add(rRest);
	rectProps->Add(fricT);
	rectProps->Add(rFric);
	rectProps->Add(colT);
	rectProps->Add(colbutton);
	
	rectProps->Add(fixedT);
	rectProps->Add(rFixed);
}

void PhyLandFrame::setupPoly()
{
	polyProps = new wxFlexGridSizer(7, 2, 9, 25);
	
	wxStaticText *editT = new wxStaticText(this, -1, wxT(""));
	wxStaticText *rotT = new wxStaticText(this, -1, _("Rotation"));
	wxStaticText *densityT = new wxStaticText(this, -1, _("Density"));
	wxStaticText *restT = new wxStaticText(this, -1, _("Bounciness"));
	wxStaticText *fricT = new wxStaticText(this, -1, _("Friction"));
	wxStaticText *colT = new wxStaticText(this, -1, _("Colour"));
	wxStaticText *fixedT = new wxStaticText(this, -1, wxT(""));
	
	wxButton *pEdit = new wxButton(this, ID_PolyEdit, _("Edit"));
	Connect(ID_PolyEdit, wxEVT_COMMAND_BUTTON_CLICKED, wxCommandEventHandler(PhyLandFrame::OnPolyEdit));
	
	pRot = new wxSpinCtrl(this, ID_Value_Changing);
	pRot->SetRange(0, 360);
	pDensity = new wxSpinCtrl(this, ID_Value_Changing);
	pDensity->SetRange(0, 20);
	pRest = new wxSpinCtrl(this, ID_Value_Changing);
	pRest->SetRange(0, 10);
	pFric = new wxSpinCtrl(this, ID_Value_Changing);
	pFric->SetRange(0, 50);
	
	wxButton *colbutton = new wxButton(this, ID_ColButton, _("Set Colour"));
	Connect(ID_ColButton, wxEVT_COMMAND_BUTTON_CLICKED, wxCommandEventHandler(PhyLandFrame::OnButton));
	
	pFixed = new wxCheckBox(this, ID_Fixed_Check, _("Fixed"));
	Connect(ID_Fixed_Check, wxEVT_COMMAND_CHECKBOX_CLICKED, wxCommandEventHandler(PhyLandFrame::OnFCheck));
	
	polyProps->Add(editT);
	polyProps->Add(pEdit);
	
	polyProps->Add(rotT);
	polyProps->Add(pRot);
	
	polyProps->Add(densityT);
	polyProps->Add(pDensity);
	polyProps->Add(restT);
	polyProps->Add(pRest);
	polyProps->Add(fricT);
	polyProps->Add(pFric);
	polyProps->Add(colT);
	polyProps->Add(colbutton);
	
	polyProps->Add(fixedT);
	polyProps->Add(pFixed);
}

void PhyLandFrame::OnPolyEdit(wxCommandEvent& WXUNUSED(event))
{
	pla->currItem->polyComputeCentre(POLYEDIT_WINSIZE / 2, POLYEDIT_WINSIZE / 2);
	polyEdit = new PhyLandPolyEdit(_("New Polygon"), pla->currItem->polyPoints, pla->currItem->polyCount);
	if(polyEdit->ShowModal() == 2)
	{
		pla->currItem->polyCount = polyEdit->resultCount;
		for(int i = 0; i < pla->currItem->polyCount; pla->currItem->polyPoints[i] = polyEdit->resultPoints[i++]);
	}
	polyEdit->Destroy();
	delete polyEdit;
	pla->currItem->polyComputeCentre(0, 0);
	pla->Refresh();
}

void PhyLandFrame::sendMsg(int what)
{
	if(what == MSG_Sidebar)
	{
		if(pla->currSelected == 0)
		{
			hcontainer->Hide(rectProps);
			hcontainer->Hide(circleProps);
			hcontainer->Hide(polyProps);
		}
		else
		{
			if(pla->currItem->itemType == PLDLE_Rect)
			{
				hcontainer->Hide(circleProps);
				hcontainer->Hide(polyProps);
				hcontainer->Show(rectProps);
				
				rWidth->SetValue(pla->currItem->rectSizeX);
				rHeight->SetValue(pla->currItem->rectSizeY);
				rRot->SetValue(pla->currItem->rotation / M_PI * 180);
				rDensity->SetValue(pla->currItem->density);
				rRest->SetValue(pla->currItem->rest);
				rFric->SetValue(pla->currItem->friction);
				rFixed->SetValue(pla->currItem->fix);
			}
			else if(pla->currItem->itemType == PLDLE_Circle)
			{
				hcontainer->Hide(rectProps);
				hcontainer->Hide(polyProps);
				hcontainer->Show(circleProps);
				
				cRadius->SetValue(pla->currItem->circleRad);
				cDensity->SetValue(pla->currItem->density);
				cRest->SetValue(pla->currItem->rest);
				cFixed->SetValue(pla->currItem->fix);
			}
			else if(pla->currItem->itemType == PLDLE_Poly)
			{
				hcontainer->Hide(rectProps);
				hcontainer->Hide(circleProps);
				hcontainer->Show(polyProps);
				
				pRot->SetValue(pla->currItem->rotation / M_PI * 180);
				pDensity->SetValue(pla->currItem->density);
				pRest->SetValue(pla->currItem->rest);
				pFixed->SetValue(pla->currItem->fix);
			}
		}
		//panel->Layout();
		Layout();
		//panel->Refresh();
		Refresh();
	}
	else if(what == MSG_Enable)
	{
		circleB->Enable();
		rectB->Enable();
		polyB->Enable();
		jDistB->Enable();
	}
	else if(what == MSG_Disable)
	{
		circleB->Disable();
		rectB->Disable();
		polyB->Disable();
		jDistB->Disable();
	}
}

void PhyLandFrame::valueChanged(wxSpinEvent& event)
{
	if(pla->currSelected != 0)
	{
		if(pla->currItem->itemType == PLDLE_Rect)
		{
			pla->currItem->rectSizeX = rWidth->GetValue();
			pla->currItem->rectSizeY = rHeight->GetValue();
			pla->currItem->rotation = (float)rRot->GetValue() / 180 * M_PI;
			pla->currItem->density = rDensity->GetValue();
			pla->currItem->rest = rRest->GetValue();
			pla->currItem->friction = rFric->GetValue();
		}
		else if(pla->currItem->itemType == PLDLE_Circle)
		{
			pla->currItem->circleRad = cRadius->GetValue();
			pla->currItem->density = cDensity->GetValue();
			pla->currItem->rest = cRest->GetValue();
		}
		else if(pla->currItem->itemType == PLDLE_Poly)
		{
			pla->currItem->density = pDensity->GetValue();
			pla->currItem->rest = pRest->GetValue();
			pla->currItem->friction = pFric->GetValue();
			pla->currItem->rotation = (float)pRot->GetValue() / 180 * M_PI;
		}
		pla->Refresh();
	}
}

void PhyLandFrame::AddCircle(wxCommandEvent& WXUNUSED(event))
{
	pla->addCircle();
}

void PhyLandFrame::AddRect(wxCommandEvent& WXUNUSED(event))
{
	pla->addRect();
}

void PhyLandFrame::AddPoly(wxCommandEvent& WXUNUSED(event))
{
	polyEdit = new PhyLandPolyEdit(_("New Polygon"));
	if(polyEdit->ShowModal() == 2)
	{
		pla->addPoly(polyEdit->resultPoints, polyEdit->resultCount);
	}
	polyEdit->Destroy();
	delete polyEdit;
}

void PhyLandFrame::AddjDist(wxCommandEvent& WXUNUSED(event))
{
	pla->addjDist();
}

void PhyLandFrame::OnFCheck(wxCommandEvent& event)
{
	pla->currItem->fix = event.IsChecked();
	pla->Refresh();
}
