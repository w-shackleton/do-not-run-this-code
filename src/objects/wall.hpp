#ifndef WALL_H
#define WALL_H

#include "rectangular.hpp"

namespace Objects
{
	class Wall : public Rectangular
	{
		public:
			Wall(EditorCallbacks &callbacks, double x, double y, double sx, double rotation);
			Wall(EditorCallbacks &callbacks, TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

			void onCMenuItemClick(int id);
		protected:
			inline std::string getName() { return "wall"; }
			void saveXMLChild(TiXmlElement* item);

			Cairo::RefPtr<Cairo::ImageSurface> wallside;
			Cairo::RefPtr<Cairo::ImageSurface> wallImage;
			Cairo::RefPtr<Cairo::SurfacePattern> wallPattern;

			bool hasEnds;
			wxMenuItem *hasEndsMenuItem;
	};
};

#endif
