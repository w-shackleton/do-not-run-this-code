#include <cairoPanel.hpp>


#include <wx/brush.h>

BEGIN_EVENT_TABLE(CairoPanel, wxPanel)
	EVT_PAINT(CairoPanel::paintEvent)
	EVT_SIZE(CairoPanel::sizeEvent)
END_EVENT_TABLE()

CairoPanel::CairoPanel(wxWindow* parent)
	: wxPanel(parent, wxID_ANY, wxDefaultPosition, wxDefaultSize, wxTAB_TRAVERSAL | wxFULL_REPAINT_ON_RESIZE)
{
	wxSize pSize = GetSize();
	surface = Cairo::ImageSurface::create(Cairo::FORMAT_RGB24, pSize.GetWidth(), pSize.GetHeight());
	cr = Cairo::Context::create(surface);
}

void CairoPanel::paintEvent(wxPaintEvent& evt)
{
	wxAutoBufferedPaintDC dc(this);
	render(dc);
}

void CairoPanel::paintNow()
{
	wxClientDC dc(this);
	render(dc);
}

/**
  Render this panel from the cairo object.
  */
void CairoPanel::render(wxDC& dc)
{
	unsigned char *data = surface->get_data();
	int datas = surface->get_stride();
	int dataw = surface->get_width();
	int datah = surface->get_height();
	unsigned char *invdata = new unsigned char[dataw * datah * 3];
	unsigned char *invdataP = invdata;
	
	// Data goes BGRA - wxImage wants RGB
	for(int i = 0; i < datas * datah; i += COL_SIZE)
	{
		*(invdataP++) = data[i + 2];
		*(invdataP++) = data[i + 1];
		*(invdataP++) = data[i];
	}

	wxBitmap img(wxImage(dataw, datah, invdata, true));
	dc.DrawBitmap(img, 0, 0);
	delete[] invdata;
}

void CairoPanel::sizeEvent(wxSizeEvent& evt)
{
	surface.~RefPtr();
	cr.~RefPtr();

	wxSize pSize = GetSize();
	surface = Cairo::ImageSurface::create(Cairo::FORMAT_RGB24, pSize.GetWidth(), pSize.GetHeight());
	cr = Cairo::Context::create(surface);

	redraw(false);
}

void CairoPanel::redraw(bool repaint)
{
	cr->set_matrix(matrix.get_matrix());
	if(repaint)
		Refresh();
}

ostream& operator<<(ostream& out, const Col& r)
{
	return out << "(" << (int)r.r << "," << (int)r.g << "," << (int)r.b << "," << (int)r.a << ")";
}
