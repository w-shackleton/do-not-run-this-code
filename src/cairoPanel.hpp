#ifndef CAIROPANEL_H
#define CAIROPANEL_H

#include <wx/wx.h>
#include <wx/dcbuffer.h>
#include <cairomm/surface.h>
#include <cairomm/context.h>
#include <iostream>

#include "matrixHelper.hpp"

#define COL_SIZE 4

class CairoPanel : public wxPanel
{
	public:
		CairoPanel(wxWindow* parent);

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
		void sizeEvent(wxSizeEvent& evt);

		void render(wxDC& dc);

		unsigned char *invdata;
		int invdataSize;
		int cairoWidth, cairoHeight;
	protected:
		Cairo::RefPtr<Cairo::ImageSurface> surface;
		Cairo::RefPtr<Cairo::Context> cr;

		void redraw(bool toCairo, bool toScreen);
		virtual void redraw_pre();
		virtual void redraw_draw() = 0;
		virtual void redraw_post(bool toScreen);

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
