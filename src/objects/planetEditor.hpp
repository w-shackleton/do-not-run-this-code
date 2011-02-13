#ifndef PLANET_EDITOR_H
#define PLANET_EDITOR_H

#include <wx/dialog.h>
#include <wx/bitmap.h>

#include "planet.hpp"
#include "../misc/geometry.hpp"
#include "../cairoPanel.hpp"

#include <cairomm/surface.h>
#include <cairomm/context.h>
#include <cairomm/refptr.h>

#define PLANETEDITOR_IMG_X 165
#define PLANETEDITOR_IMG_Y 165

namespace Objects
{
	namespace Helpers
	{
		class PlanetPanel;
		class PlanetEditor : public wxDialog
		{
			public:
				PlanetEditor(wxWindow* parent);
				DECLARE_EVENT_TABLE();
			public:
				int type;
			protected:

				int tempType;

				enum
				{
					ID_Cancel_click = wxID_HIGHEST + 1,
					ID_Ok_click
				};

				void OnCancel(wxCommandEvent& event);
				void OnOk(wxCommandEvent& event);
				void OnPlanetSelect(wxCommandEvent& event);

				PlanetPanel *ppanel;

				Cairo::RefPtr<Cairo::ImageSurface> planetShadow, bounceicon, densityicon;
				wxBitmap createPlanetBitmap(std::string picture, double density, double bounciness, Misc::Colour& col, int width = PLANETEDITOR_IMG_X, int height = PLANETEDITOR_IMG_Y);
		};
		class PlanetPanel : public CairoPanel
		{
			public:
				PlanetPanel(wxWindow *window, wxColour bgCol);
				void SetPlanet(int id);
			protected:
				virtual void redraw_draw();
				Cairo::RefPtr<Cairo::ImageSurface> shadow;
				std::string imgFName;
				wxColour bgCol;
		};
	}
}

#endif
