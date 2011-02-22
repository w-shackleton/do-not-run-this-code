#ifndef VORTEX_H
#define VORTEX_H

#include "rectangular.hpp"

#include <cairomm/refptr.h>

namespace Objects
{
	class Vortex : public Rectangular
	{
		protected:
			inline std::string getName() { return "gravity"; }
			void saveXMLChild(TiXmlElement* item);
			
			Cairo::RefPtr<Cairo::ImageSurface> img;

			/*
			  The 'power' of the vortex
			 */
			double power;
		public:
			Vortex(EditorCallbacks &callbacks, double x, double y, double sx, double sy, double rotation);
			Vortex(EditorCallbacks &callbacks, TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

			void onCMenuItemClick(int id);
	};
};

#endif
