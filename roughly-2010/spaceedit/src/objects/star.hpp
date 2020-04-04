#ifndef STAR_H
#define STAR_H

#include "spherical.hpp"

namespace Objects
{
	class Star : public Spherical
	{
		protected:
			inline std::string getName() { return "star"; }
			void saveXMLChild(TiXmlElement* item);
		public:
			Star(EditorCallbacks &callbacks, double sx, double sy);
			Star(EditorCallbacks &callbacks, TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

			Cairo::RefPtr<Cairo::ImageSurface> img;
	};
};
#endif
