#ifndef CAIROPANEL_H
#define CAIROPANEL_H

#include <wx/wx.h> // TODO: Remove!
#include <wx/dcbuffer.h>
#include <cairomm/surface.h>
#include <cairomm/context.h>
#include <iostream>

#include "matrixHelper.hpp"

#define COL_SIZE 4

#ifdef __WXGTK__
#define CAIRO_NATIVE_GTK
#define CAIRO_NATIVE
#endif

class CairoPanel : public wxPanel
{
	public:
		CairoPanel(wxWindow* parent, wxSize size = wxDefaultSize);

		void redraw();

		inline wxSize getMovedPos()
		{
			double x = 0, y = 0;
			matrix.get_inverse_matrix().transform_point(x, y);
			return wxSize(x, y);
		}

		DECLARE_EVENT_TABLE();
	private:
		void paintNow();
		void paintEvent(wxPaintEvent& evt);

		void cairoToScreen(wxDC& dc);

		void render();
		virtual void render_pre();
		virtual void render_draw() = 0;
		virtual void render_post();

		unsigned char *invdata;
		int invdataSize;
		int cairoWidth, cairoHeight;

		bool nativeRendering;
	protected:
		void sizeEvent(wxSizeEvent& evt); // Used to reset panel

		Cairo::RefPtr<Cairo::ImageSurface> surface;
		Cairo::RefPtr<Cairo::Context> cr;

		MatrixHelper matrix;
};
struct Col
{
unsigned char b;
unsigned char g;
unsigned char r;
unsigned char a;
};
std::ostream& operator<<(std::ostream& out, const Col& r);

#endif
