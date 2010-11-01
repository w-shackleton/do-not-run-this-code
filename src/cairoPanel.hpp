#ifndef CAIROPANEL_H
#define CAIROPANEL_H

#include <wx/wx.h>
#include <wx/dcbuffer.h>
#include <cairomm/surface.h>
#include <cairomm/context.h>
#include <iostream>

#include "matrixHelper.hpp"

using namespace std;

#define COL_SIZE 4

class CairoPanel : public wxPanel
{
	public:
		CairoPanel(wxWindow* parent);

		DECLARE_EVENT_TABLE();
	private:
		void paintNow();
		void paintEvent(wxPaintEvent& evt);
		void sizeEvent(wxSizeEvent& evt);

		void render(wxDC& dc);
	protected:
		Cairo::RefPtr<Cairo::ImageSurface> surface;
		Cairo::RefPtr<Cairo::Context> cr;

		virtual void redraw(bool repaint);

		MatrixHelper matrix;
};
struct Col
{
unsigned char b;
unsigned char g;
unsigned char r;
unsigned char a;
};
ostream& operator<<(ostream& out, const Col& r);

#endif
