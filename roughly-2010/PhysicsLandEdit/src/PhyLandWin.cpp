#include "PhyLandWin.h"


IMPLEMENT_APP(PhyLandWin)

bool PhyLandWin::OnInit()
{
	wxInitAllImageHandlers();
	PhyLandFrame *frame = new PhyLandFrame(_(APP_NAME), wxDefaultPosition, wxSize(500,450));
	frame->Show(true);
	SetTopWindow(frame);
	return true;
}
