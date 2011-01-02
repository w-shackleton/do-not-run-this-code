#include "planetEditor.hpp"

#include <wx/sizer.h>
#include <wx/button.h>
#include <wx/bmpbuttn.h>

#include <wx/dcmemory.h>
#include <wx/image.h>

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

PlanetEditor::PlanetEditor(wxWindow* parent, int &type) :
	wxDialog(parent, -1, _("Edit Planet")),
	type(type)
{
	wxBoxSizer *vsizer = new wxBoxSizer(wxVERTICAL);

	wxGridSizer *grid = new wxGridSizer(3);

	// This is the list in planet.hpp
	for(int i = 0; i < planetTypes.size(); i++)
	{
		PlanetBitmap bmp(planetTypes[i].filename, planetTypes[i].density, planetTypes[i].bounciness, type == planetTypes[i].id);
		bitmaps.push_back(bmp); // Maintain list for deselecting
		wxBitmapButton *b1 = new wxBitmapButton(this, BUTTON_ID_START + planetTypes[i].id, bmp);
		b1->Connect(wxEVT_COMMAND_BUTTON_CLICKED, wxCommandEventHandler(PlanetEditor::OnPlanetSelect));
		grid->Add(b1);
	}

	vsizer->Add(grid);

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
	type = tempType;
	EndModal(0);
}

void PlanetEditor::OnPlanetSelect(wxCommandEvent& event)
{
	tempType = planetTypes[event.GetId() - BUTTON_ID_START].id;
	for(int i = 0; i < bitmaps.size(); i++)
		bitmaps[i].select(false); // Deselect all previous

//	((PlanetBitmap *)event.GetEventObject())->select();
}

PlanetEditor::PlanetBitmap::PlanetBitmap(std::string picture, double density, double bounciness, bool selected, int width, int height) :
	wxBitmap(width, height),
	width(width),
	height(height),
	selected(selected)
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath(picture));
	imgWidth = img->get_width(); imgHeight = img->get_height();
	bounceicon = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("bounceicon.png"));
	if(img == NULL)
		cout << "ERROR: Could not load image " << picture << "." << endl;

	surface = Cairo::ImageSurface::create(Cairo::FORMAT_RGB24, width, height);
	cr = Cairo::Context::create(surface);

	draw();
}

void PlanetEditor::PlanetBitmap::select(bool select)
{
	selected = select;
	draw();
}

void PlanetEditor::PlanetBitmap::draw()
{
	// DRAW HERE
	cr->scale((double)width / (double)imgWidth, (double)height / (double)imgHeight);
//	cr->scale(img->get_width() / width, img->get_height() / height);

	cr->set_source_rgb(0, 0, 0);
	cr->paint();

	cr->set_source(img, 0, 0);
	cr->rectangle(0, 0, img->get_width(), img->get_height());
	cr->fill();

	cr->set_identity_matrix();
	
	// Standard scale here
	cr->scale((double)width / (double)220, (double)height / (double)220); // With a 10px border
	cr->translate(10, 10);

	cr->set_source(bounceicon, 0, 170);
	cr->paint();

	// Energy bars should be from 50px to 200px (5px vertical border in the 30px space)
	cr->set_source_rgb(0, 0, 0);
	cr->rectangle(50, 175, 150, 20); cr->fill();

	Misc::trimMinMax(bounciness, 0, 1.5);
	cr->set_source_rgb(1, 0, 0);
	cr->rectangle(50, 175, bounciness * 150, 20); cr->fill();

	cr->set_source_rgb(1, 1, 1);
	cr->rectangle(50, 175, 150, 20); cr->stroke();

	// Selected tick
	if(selected)
	{
		cr->set_source_rgb(0, 1, 0);
		cr->move_to(0, 10);
		cr->line_to(10, 20);
		cr->line_to(20, 0);
		cr->stroke();
	}

	cr->set_identity_matrix();
	// END DRAW

	unsigned char *d = surface->get_data();

	data = new unsigned char[width * height * 3];
	dataSize = width * height * 3;               

	unsigned int size = 0;
	while(size < dataSize)
	{
		*(data + size++) = *(d+ 2);
		*(data + size++) = *(d+ 1);
		*(data + size++) = *d;

		d += 4;
	}

	// This goes cairo->data->wxImage->wxBitmap->wxMemoryDC->wxBitmap! (memory efficient much!)
	wxBitmap bmp(wxImage(width, height, data));

	wxMemoryDC dc(*this);
	dc.DrawBitmap(bmp, 0, 0, false);
}

