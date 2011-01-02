#ifndef PLANET_EDITOR_H
#define PLANET_EDITOR_H

#include <wx/dialog.h>
#include <wx/bitmap.h>

#include "planet.hpp"
#include "../misc/geometry.hpp"

#include <cairomm/surface.h>
#include <cairomm/context.h>
#include <cairomm/refptr.h>

#define PLANETEDITOR_IMG_X 165
#define PLANETEDITOR_IMG_Y 165

namespace Objects
{
	namespace Helpers
	{
		class PlanetEditor : public wxDialog
		{
			public:
				PlanetEditor(wxWindow* parent, int &type);
				DECLARE_EVENT_TABLE();
			protected:

				int &type;
				int tempType;

				enum
				{
					ID_Cancel_click = wxID_CANCEL,
					ID_Ok_click = wxID_OK,
				};

				void OnCancel(wxCommandEvent& event);
				void OnOk(wxCommandEvent& event);
				void OnPlanetSelect(wxCommandEvent& event);

				Cairo::RefPtr<Cairo::ImageSurface> planetShadow, bounceicon, densityicon;
				wxBitmap createPlanetBitmap(std::string picture, double density, double bounciness, Misc::Colour& col, int width = PLANETEDITOR_IMG_X, int height = PLANETEDITOR_IMG_Y);
		};
	}
}

#endif
