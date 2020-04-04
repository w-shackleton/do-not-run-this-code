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
				PlanetEditor(wxWindow* parent, int typePosition = 0);
				DECLARE_EVENT_TABLE();
			public:
				int type;
			protected:

				enum
				{
					ID_Cancel_click = wxID_HIGHEST + 1,
					ID_Ok_click,
					ID_Radiobox,
				};

				void OnCancel(wxCommandEvent& event);
				void OnOk(wxCommandEvent& event);
				void OnPlanetSelect(wxCommandEvent& event);

				PlanetPanel *ppanel;

		};
		class PlanetPanel : public CairoPanel
		{
			public:
				PlanetPanel(wxWindow *window, int type, int width = PLANETEDITOR_IMG_X, int height = PLANETEDITOR_IMG_Y);
				void SetPlanet(int position);
			protected:
				Cairo::RefPtr<Cairo::ImageSurface> planetShadow, bounceicon, densityicon;
				virtual void render_draw();
				Cairo::RefPtr<Cairo::ImageSurface> shadow;
				Cairo::RefPtr<Cairo::ImageSurface> img;
				std::string imgFName;

				double density;
				double bounciness;
				Misc::Colour col;
				int width, height;
		};
	}
}

#endif
