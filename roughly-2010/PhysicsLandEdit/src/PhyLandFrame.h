#ifndef PHYLANDFRAME_H
#define PHYLANDFRAME_H

#include <wx/wx.h>
#include <wx/spinctrl.h>
#include <wx/colordlg.h>
#include <wx/filefn.h>

#include "header.h"
#include "statfuncs.h"
#include "PhyLandArea.h"
#include "PhyLandPolyEdit.h"
#include "MsgSend.h"

class PhyLandFrame: public wxFrame, public MsgSend
{
private:
	
	//wxPanel *panel;
	PhyLandArea *pla;
	PhyLandPolyEdit *polyEdit;
	wxBoxSizer *hcontainer;
	wxFlexGridSizer *circleProps;
	wxFlexGridSizer *rectProps;
	wxFlexGridSizer *polyProps;
	
	wxSpinCtrl *cRadius;
	wxSpinCtrl *cDensity;
	wxSpinCtrl *cRest;
	wxCheckBox *cFixed;
	
	wxSpinCtrl *rWidth;
	wxSpinCtrl *rHeight;
	wxSpinCtrl *rRot;
	wxSpinCtrl *rDensity;
	wxSpinCtrl *rRest;
	wxSpinCtrl *rFric;
	wxCheckBox *rFixed;
	
	wxSpinCtrl *pRot;
	wxSpinCtrl *pDensity;
	wxSpinCtrl *pRest;
	wxSpinCtrl *pFric;
	wxCheckBox *pFixed;
	
	wxColourDialog *iCol;
	void keyReleased(wxKeyEvent& event);
	
	void OnQuit(wxCommandEvent& event);
	void OnAbout(wxCommandEvent& event);
	void OnButton(wxCommandEvent &event);
	void OnFCheck(wxCommandEvent &event);
	void OnPolyEdit(wxCommandEvent &event);
	void AddCircle(wxCommandEvent &event);
	void AddRect(wxCommandEvent &event);
	void AddPoly(wxCommandEvent &event);
	void AddjDist(wxCommandEvent &event);
	void setupCircle();
	void setupRect();
	void setupPoly();
	void setupParts();
	void valueChanged(wxSpinEvent& event);
	void plaChanged();
	
	wxButton *circleB;
	wxButton *rectB;
	wxButton *polyB;
	wxButton *jDistB;
public:
	PhyLandFrame(const wxString& title, const wxPoint& pos, const wxSize& size);
	
	virtual void sendMsg(int what);
	
	DECLARE_EVENT_TABLE()
};

enum
{
	ID_File_Quit = 1,
	ID_Help_About,
	ID_PLA_Changed_Rec,
	ID_ColButton,
	ID_PolyEdit,
	ID_Value_Changing,
	ID_Fixed_Check,
	ID_Item_Changed,
	
	ID_Tool_Circle,
	ID_Tool_Rect,
	ID_Tool_Poly,
	ID_Tool_Joint_Dist,
};
#endif

