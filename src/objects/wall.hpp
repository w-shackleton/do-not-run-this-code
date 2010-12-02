#ifndef WALL_H
#define WALL_H

#include "rectangular.hpp"

namespace Objects
{
	class Wall : public Rectangular
	{
		public:
			Wall(double x, double y, double sx, double rotation);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);
		protected:
			inline std::string getName() { return "wall"; }
			void saveXMLChild(TiXmlElement* item);

			Cairo::RefPtr<Cairo::ImageSurface> wallside;
			Cairo::RefPtr<Cairo::ImageSurface> wall;
	};
};

#endif
