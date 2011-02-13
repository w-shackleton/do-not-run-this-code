#include "planetEditor.hpp"

#include <wx/sizer.h>
#include <wx/button.h>
#include <wx/bmpbuttn.h>
#include <wx/image.h>

#include <wx/dcmemory.h>

#include "../misc/data.hpp"

#include <iostream>

using namespace std;
using namespace Objects;
using namespace Objects::Helpers;

BEGIN_EVENT_TABLE(PlanetEditor, wxDialog)
	EVT_BUTTON(ID_Cancel_click, PlanetEditor::OnCancel)
	EVT_BUTTON(ID_Ok_click, PlanetEditor::OnOk)
END_EVENT_TABLE()

#define BUTTON_ID_START (wxID_HIGHEST + 10)

PlanetEditor::PlanetEditor(wxWindow* parent) :
	wxDialog(parent, -1, _("Edit Planet")),
	type(0),
	tempType(0)
{
	planetShadow = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("planet-s.png"));
	if(planetShadow == NULL)
		cout << "ERROR: planet shadow image not found!" << endl;
	bounceicon = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("bounceicon.png"));
	if(bounceicon == NULL)
		cout << "ERROR: bounce icon not found!" << endl;
	densityicon = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("densityicon.png"));
	if(densityicon == NULL)
		cout << "ERROR: density icon not found!" << endl;

	wxBoxSizer *vsizer = new wxBoxSizer(wxVERTICAL);

	wxGridSizer *grid = new wxGridSizer(4);

	// This is the list in planet.hpp
	for(int i = 0; i < planetTypes.size(); i++)
	{
		wxBitmapButton *b1 = new wxBitmapButton(this, BUTTON_ID_START + i, createPlanetBitmap(planetTypes[i].filename, planetTypes[i].density, planetTypes[i].bounciness, planetTypes[i].bgCol));

		b1->Connect(wxEVT_COMMAND_BUTTON_CLICKED, wxCommandEventHandler(PlanetEditor::OnPlanetSelect));

		grid->Add(b1);
	}

	vsizer->Add(grid);

	// Current planet selection
	wxBoxSizer *pSizer = new wxBoxSizer(wxHORIZONTAL);

//	wxStaticText *pText = new wxStaticText(this, -1, _("Current planet:"));
//	pSizer->Add(pText, 0, wxEXPAND | wxALIGN_CENTRE_VERTICAL);

//	ppanel = new PlanetPanel(this, GetBackgroundColour()); // Not working, so don't use
//	ppanel->SetPlanet(planetTypes[0].id);
//	pSizer->Add(ppanel);
	vsizer->Add(pSizer, 0, wxALL, 5);

	wxBoxSizer *hsizer = new wxBoxSizer(wxHORIZONTAL);
	hsizer->Add(new wxButton(this, ID_Cancel_click, _("&Cancel")), 1, wxEXPAND);
	SetEscapeId(ID_Cancel_click);
	hsizer->Add(new wxButton(this, ID_Ok_click, _("O&k")), 1, wxEXPAND);

	vsizer->Add(hsizer, 1, wxEXPAND | wxALL, 5);
	SetSizer(vsizer);
	vsizer->SetSizeHints(this);
}

void PlanetEditor::OnCancel(wxCommandEvent& event)
{
	EndModal(1);
}

void PlanetEditor::OnOk(wxCommandEvent& event)
{
	cout << type << "THISTHIS2" << endl;
	// type = tempType;
	EndModal(0);
}

void PlanetEditor::OnPlanetSelect(wxCommandEvent& event)
{
//	ppanel->SetPlanet(event.GetId() - BUTTON_ID_START);
//	tempType = planetTypes[event.GetId() - BUTTON_ID_START].id;
	tempType = event.GetId() - BUTTON_ID_START;
	type = event.GetId() - BUTTON_ID_START;
	cout << type << "THISTHIS" << endl;
}

wxBitmap PlanetEditor::createPlanetBitmap(std::string picture, double density, double bounciness, Misc::Colour&col, int width, int height)
{
	Cairo::RefPtr<Cairo::ImageSurface> img;

	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath(picture));
	int imgWidth = img->get_width(); int imgHeight = img->get_height();

	if(img == NULL)
		cout << "ERROR: Could not load image " << picture << "." << endl;

	Cairo::RefPtr<Cairo::ImageSurface> surface = Cairo::ImageSurface::create(Cairo::FORMAT_RGB24, width, height);
	Cairo::RefPtr<Cairo::Context> cr = Cairo::Context::create(surface);

	cr->set_source_rgb(0, 0, 0);
	cr->paint();

	// DRAW HERE
	cr->set_source_rgb(col.r / 256, col.g / 256, col.b / 256); // These are 0 anyway if invalid, so no need to add 'if' no bg
	cr->arc(width / 2, height / 2, width / 2, 0, 2.0 * M_PI);
	cr->fill();
//	cout << "W: " << width << "," << imgWidth << ";" << height << "," << imgHeight << endl;
	cr->scale((double)width / (double)imgWidth, (double)height / (double)imgHeight);
//	cr->scale(img->get_width() / width, img->get_height() / height);

	cr->set_source(img, 0, 0);
	cr->rectangle(0, 0, img->get_width(), img->get_height());
	cr->fill();

	cr->set_source(planetShadow, 0, 0);
	cr->rectangle(0, 0, img->get_width(), img->get_height());
	cr->fill();

	cr->set_identity_matrix();
	
	// Standard scale here
	cr->scale((double)width / (double)220, (double)height / (double)220); // With a 10px border
	cr->translate(10, 10);

	// Energy bars should be from 50px to 200px (5px vertical border in the 30px space)
	cr->set_source(bounceicon, 0, 170);
	cr->paint();

	cr->set_source_rgb(0, 0, 0);
	cr->rectangle(50, 175, 150, 20); cr->fill();

	Misc::trimMinMax(bounciness, 0, PLANET_BN_MAX);
	cr->set_source_rgb(1, 0, 0);
	cr->rectangle(50, 175, bounciness * 150 / PLANET_BN_MAX, 20); cr->fill();

	cr->set_source_rgb(1, 1, 1);
	cr->rectangle(50, 175, 150, 20); cr->stroke();

	// Second bar
	cr->set_source(densityicon, 0, 130);
	cr->paint();

	cr->set_source_rgb(0, 0, 0);
	cr->rectangle(50, 135, 150, 20); cr->fill();

	Misc::trimMinMax(density, 0, PLANET_DN_MAX);
	cr->set_source_rgb(1, 0, 0);
	cr->rectangle(50, 135, density * 150 / PLANET_DN_MAX, 20); cr->fill();

	cr->set_source_rgb(1, 1, 1);
	cr->rectangle(50, 135, 150, 20); cr->stroke();

	cr->set_identity_matrix();
	// END DRAW

	unsigned char *d = surface->get_data();

	unsigned char *data = new unsigned char[width * height * 3];
	int dataSize = width * height * 3;               

	unsigned int size = 0;
	while(size < dataSize)
	{
		*(data + size++) = *(d+ 2);
		*(data + size++) = *(d+ 1);
		*(data + size++) = *d;

		d += 4;
	}

	// This goes cairo->data->wxImage->wxBitmap->wxMemoryDC->wxBitmap! (memory efficient much!)
	return wxBitmap(wxImage(width, height, data));
}

PlanetPanel::PlanetPanel(wxWindow *window, wxColour bgCol) :
	CairoPanel(window, wxSize(100, 100)),
	bgCol(bgCol)
{
	shadow = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("planet-s.png"));
}

void PlanetPanel::SetPlanet(int id)
{
//	wxSize sz;
//	wxSizeEvent evt(sz);
//	sizeEvent(evt); // clear?
	cout << Misc::Data::getFilePath(planetTypes.begin()->filename) << "LOLOL" <<  endl;
	imgFName = string(Misc::Data::getFilePath(string(planetTypes.begin()->filename)));

	for(vector<PlanetType>::iterator it = planetTypes.begin(); it != planetTypes.end(); it++)
		if(it->id == id)
		{
			imgFName = Misc::Data::getFilePath(it->filename);
		}
	redraw();
}

void PlanetPanel::redraw_draw()
{
	cr->set_source_rgb(bgCol.Red() / 256.0, bgCol.Green() / 256.0, bgCol.Blue() / 256.0); 
	cr->paint();
	Cairo::RefPtr<Cairo::ImageSurface> img = Cairo::ImageSurface::create_from_png(imgFName);
	int imgWidth = img->get_width(); int imgHeight = img->get_height();

	int width, height;
	GetSize(&width, &height);

	cr->scale((double)width / (double)imgWidth, (double)height / (double)imgHeight);
	cr->set_source(img, 0, 0);
	cr->paint();

	cr->set_source(shadow, 0, 0);
	cr->paint();
}

