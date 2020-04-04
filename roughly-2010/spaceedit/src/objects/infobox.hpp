#ifndef INFOBOX_H
#define INFOBOX_H

#include "rectangular.hpp"
#include <cairomm/surface.h>
#include <cairomm/refptr.h>

namespace Objects
{
	class InfoBox : public Rectangular
	{
		public:
			InfoBox(EditorCallbacks &callbacks, double x, double y, double rotation, std::string text, bool initialShow);
			InfoBox(EditorCallbacks &callbacks, TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

			void onCMenuItemClick(int id);
		protected:
			void saveXMLChild(TiXmlElement* item);
			Cairo::RefPtr<Cairo::ImageSurface> img;
			inline std::string getName() { return "infobox"; }

			std::string text;
			bool initialShow;
	};
};

#endif
