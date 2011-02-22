#include "vortex.hpp"

using namespace Objects;
using namespace std;
#include <iostream>
#include <vector>

#include "../misc/data.hpp"
#include "sliderEditor.hpp"

#define VORTEX_MIN_X 100
#define VORTEX_MIN_Y 100
#define VORTEX_MAX_X 400
#define VORTEX_MAX_Y 400
#define VORTEX_POW_MIN 0.5
#define VORTEX_POW_MAX 3

#define IMG_SIZE_X 400
#define IMG_SIZE_Y 400
#define IMG_NAME "vortex.png"

Vortex::Vortex(EditorCallbacks &callbacks, double x, double y, double sx, double sy, double rotation) :
	Rectangular(callbacks, x, y, sx, sy, rotation, Misc::Point(VORTEX_MIN_X, VORTEX_MIN_Y), Misc::Point(VORTEX_MAX_X, VORTEX_MAX_Y)),
	power(1)
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath(IMG_NAME));
	contextMenu->Append(contextMenuNextAvailableSlot++, _("&Change power"));
}

Vortex::Vortex(EditorCallbacks &callbacks, TiXmlElement &item) :
	Rectangular(callbacks, item, Misc::Point(VORTEX_MIN_X, VORTEX_MIN_Y), Misc::Point(VORTEX_MAX_X, VORTEX_MAX_Y)),
	power(1)
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath(IMG_NAME));
	contextMenu->Append(contextMenuNextAvailableSlot++, _("&Change power"));
}

void Vortex::saveXMLChild(TiXmlElement* item)
{
	Rectangular::saveXMLChild(item);
}

void Vortex::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(x, y);
	cr->rotate(rotation);
//	cr->scale(sx / (double)IMG_SIZE_X, sy / (double)IMG_SIZE_Y); // This actually looks better without this scale

//	cr->set_source(img, -IMG_SIZE_X / 2, -IMG_SIZE_Y / 2); // Old method
//	cr->rectangle( - (IMG_SIZE_X / 2),  - (IMG_SIZE_Y / 2), IMG_SIZE_X, IMG_SIZE_Y); cr->fill();
	cr->set_source(img, -IMG_SIZE_X / 2, -IMG_SIZE_Y / 2);
	cr->rectangle( - (sx / 2),  - (sy / 2), sx, sy); cr->fill();

	cr->set_source_rgb(0, 0, 0);
	cr->rectangle( - (sx / 2),  - (sy / 2), sx, sy); cr->stroke();

//	cr->scale((double)IMG_SIZE_X / sx, (double)IMG_SIZE_Y / sy);
	cr->rotate(-rotation);
	cr->translate(-x, -y);
}

void Vortex::onCMenuItemClick(int id)
{
	SpaceItem::onCMenuItemClick(id);
	switch(id)
	{
		case ID_CMenu_2:
			cout << "Editing..." << endl;
			vector<Objects::Helpers::Slider> sliders;

			power *= 100; // needs a value in ints
			Objects::Helpers::Slider powerSettings(&power, VORTEX_POW_MIN * 100, VORTEX_POW_MAX * 100, "Power"); // TODO: wxString-ize

			sliders.push_back(powerSettings);
			Objects::Helpers::SliderEditor sliderEditor(sliders, std::string("Edit vortex"));
			sliderEditor.ShowModal();

			power /= 100;
			return;
	}
}
