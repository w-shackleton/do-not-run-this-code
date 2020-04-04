#ifndef BLACKHOLE_H
#define BLACKHOLE_H

#include "spherical.hpp"

namespace Objects
{
	class BlackHole : public Spherical
	{
		protected:
			inline std::string getName() { return "blackhole"; }
			void saveXMLChild(TiXmlElement* item);
		public:
			BlackHole(EditorCallbacks &callbacks, double sx, double sy);
			BlackHole(EditorCallbacks &callbacks, TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

			Cairo::RefPtr<Cairo::ImageSurface> img;
	};
};
#endif
