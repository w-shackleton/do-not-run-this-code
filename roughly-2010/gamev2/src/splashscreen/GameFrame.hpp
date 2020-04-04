#ifndef GAME_FRAME_H
#define GAME_FRAME_H

#include <wx/wx.h>

class GameFrame: public wxFrame
{
protected:
	wxPanel *splashPanel;
	wxStaticBitmap *splashLogo;
	/*wxButton *buttonStart, *buttonOptions, *buttonQuit;*/ // Not needed?
	
	void onButtonStartPressed(wxCommandEvent& WXUNUSED(event));
	void onButtonOptionsPressed(wxCommandEvent& WXUNUSED(event));
	void onButtonQuitPressed(wxCommandEvent& WXUNUSED(event));
public:
	GameFrame(const wxString& title, const wxPoint& pos, const wxSize& size);
	~GameFrame();
	
	DECLARE_EVENT_TABLE()
};
#endif
