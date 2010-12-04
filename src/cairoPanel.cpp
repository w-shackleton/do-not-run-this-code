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

	cairoWidth = surface->get_width();
	cairoHeight = surface->get_height();

	invdata = new unsigned char[cairoWidth * cairoHeight * 3];
	invdataSize = cairoWidth * cairoHeight * 3;
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
