#ifndef PORTAL_H
#define PORTAL_H

#include "spherical.hpp"

namespace Objects
{
	class Portal : public Spherical
	{
		protected:
			inline std::string getName() { return "portal"; }
			void saveXMLChild(TiXmlElement* item);
		public:
			Portal(EditorCallbacks &callbacks, double sx, double sy);
			Portal(EditorCallbacks &callbacks, TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

			Cairo::RefPtr<Cairo::ImageSurface> img;
	};
};
#endif
