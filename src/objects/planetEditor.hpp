#ifndef PLANET_EDITOR_H
#define PLANET_EDITOR_H

#include <wx/dialog.h>
#include "planet.hpp"

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

				class PlanetBitmap : public wxBitmap
				{
					public:
						PlanetBitmap(std::string picture, double density, double bounciness, bool selected = false, int width = PLANETEDITOR_IMG_X, int height = PLANETEDITOR_IMG_Y);
						void select(bool select = true);

					protected:
						void draw();

						bool selected;

						Cairo::RefPtr<Cairo::ImageSurface> img, bounceicon;
						int imgWidth, imgHeight;
						double density, bounciness;

						Cairo::RefPtr<Cairo::ImageSurface> surface;
						Cairo::RefPtr<Cairo::Context> cr;          

						unsigned char *data;
						int dataSize;
						int width, height;
				};

				std::vector<PlanetBitmap> bitmaps;
		};
	}
}

#endif
