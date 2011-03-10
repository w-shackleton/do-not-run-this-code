#include <cairoPanel.hpp>

using namespace std;

#include <wx/brush.h>

#include "misc/data.hpp"

#ifdef CAIRO_NATIVE_GTK
#include <gdk/gdk.h>
#endif

BEGIN_EVENT_TABLE(CairoPanel, wxPanel)
	EVT_PAINT(CairoPanel::paintEvent)
	EVT_SIZE(CairoPanel::sizeEvent)
END_EVENT_TABLE()

CairoPanel::CairoPanel(wxWindow* parent, wxSize size)
	: wxPanel(parent, wxID_ANY, wxDefaultPosition, size, wxTAB_TRAVERSAL | wxFULL_REPAINT_ON_RESIZE),
	nativeRendering(Misc::Data::nativeRendering)
{
	SetBackgroundStyle(wxBG_STYLE_CUSTOM);
#ifndef CAIRO_NATIVE
	this->nativeRendering = false;
	nativeRendering = false;
#endif
	cout << (nativeRendering ? "Using native rendering" : "Not using native rendering") << endl;
	if(!nativeRendering)
	{
		wxSize pSize = GetSize();
		surface = Cairo::ImageSurface::create(Cairo::FORMAT_RGB24, pSize.GetWidth(), pSize.GetHeight());
		cr = Cairo::Context::create(surface);

		cairoWidth = surface->get_width();
		cairoHeight = surface->get_height();

		invdata = new unsigned char[cairoWidth * cairoHeight * 3];
		invdataSize = cairoWidth * cairoHeight * 3;
	}
}

void CairoPanel::paintEvent(wxPaintEvent& evt)
{
	if(!nativeRendering)
	{
		wxPaintDC dc(this);
		render();
		cairoToScreen(dc);
	}
	else
	{
#ifdef CAIRO_NATIVE_GTK
		wxPaintDC dc(this);
		cairo_t* cairo_image = gdk_cairo_create(dc.m_window);
		gdk_drawable_get_size(dc.m_window, &cairoWidth, &cairoHeight);

		Cairo::Context* context = new Cairo::Context(cairo_image);
		cr = Cairo::RefPtr<Cairo::Context>(context);

		render();
#endif
	}
}

void CairoPanel::paintNow()
{
	if(!nativeRendering)
	{
		wxClientDC dc(this);
		render();
		cairoToScreen(dc);
	}
	else
	{
#ifdef CAIRO_NATIVE_GTK
		Refresh();
#endif
	}
}

/**
  Render this panel from the cairo object. - Only for non-native rendering
  */
void CairoPanel::cairoToScreen(wxDC& dc)
{
	unsigned char *data = surface->get_data();
	
	// Data goes BGRA - wxImage wants RGB
//	for(int i = 0; i < datas * datah; i += COL_SIZE)
//	{
//		*(invdataP++) = data[i + 2];
//		*(invdataP++) = data[i + 1];
//		*(invdataP++) = data[i];
//	}

	unsigned int size = 0;
	while(size < invdataSize)
	{
//		memcpy(invdata + size++, data + 2, 1);
//		memcpy(invdata + size++, data + 1, 1);
//		memcpy(invdata + size++, data, 1);
		*(invdata + size++) = *(data + 2);
		*(invdata + size++) = *(data + 1);
		*(invdata + size++) = *data;

		data+=4;
	}

	wxBitmap img(wxImage(cairoWidth, cairoHeight, invdata, true));
	dc.DrawBitmap(img, 0, 0);
}

void CairoPanel::sizeEvent(wxSizeEvent& evt)
{
	if(!nativeRendering)
	{
		surface.~RefPtr();
		cr.~RefPtr();

		wxSize pSize = GetSize();
		surface = Cairo::ImageSurface::create(Cairo::FORMAT_RGB24, pSize.GetWidth(), pSize.GetHeight());
		cr = Cairo::Context::create(surface);

		cairoWidth = surface->get_width();
		cairoHeight = surface->get_height();

		delete[] invdata;
		invdata = new unsigned char[cairoWidth * cairoHeight * 3];
		invdataSize = cairoWidth * cairoHeight * 3;
	}
}

void CairoPanel::redraw()
{
	paintNow();
}

void CairoPanel::render()
{
	render_pre();
	render_draw();
	render_post();
}

void CairoPanel::render_pre()
{
	cr->set_matrix(matrix.get_matrix());
}

void CairoPanel::render_post()
{

}

ostream& operator<<(ostream& out, const Col& r)
{
	return out << "(" << (int)r.r << "," << (int)r.g << "," << (int)r.b << "," << (int)r.a << ")";
}

