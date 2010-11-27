#include <cairoPanel.hpp>

using namespace std;

#include <wx/brush.h>

BEGIN_EVENT_TABLE(CairoPanel, wxPanel)
	EVT_PAINT(CairoPanel::paintEvent)
	EVT_SIZE(CairoPanel::sizeEvent)
END_EVENT_TABLE()

CairoPanel::CairoPanel(wxWindow* parent)
	: wxPanel(parent, wxID_ANY, wxDefaultPosition, wxDefaultSize, wxTAB_TRAVERSAL | wxFULL_REPAINT_ON_RESIZE)
{
	SetBackgroundStyle(wxBG_STYLE_CUSTOM);
	wxSize pSize = GetSize();
	surface = Cairo::ImageSurface::create(Cairo::FORMAT_RGB24, pSize.GetWidth(), pSize.GetHeight());
	cr = Cairo::Context::create(surface);
}

void CairoPanel::paintEvent(wxPaintEvent& evt)
{
	//wxAutoBufferedPaintDC dc(this);
	wxPaintDC dc(this);
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

	redraw(true, true);
}

void CairoPanel::redraw(bool toCairo, bool toScreen)
{
	redraw_pre();
	if(toCairo) redraw_draw();
	redraw_post(toScreen);
}

void CairoPanel::redraw_pre()
{
	cr->set_matrix(matrix.get_matrix());
}

void CairoPanel::redraw_post(bool toScreen)
{
	if(toScreen)
		paintNow();
}

ostream& operator<<(ostream& out, const Col& r)
{
	return out << "(" << (int)r.r << "," << (int)r.g << "," << (int)r.b << "," << (int)r.a << ")";
}
