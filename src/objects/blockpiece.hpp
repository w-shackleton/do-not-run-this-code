#ifndef BLOCK_H
#define BLOCK_H

#include "rectangular.hpp"

namespace Objects
{
	class Block : public Rectangular
	{
		public:
			Block(EditorCallbacks &callbacks, double x, double y, double sx, double sy, int type);
			Block(EditorCallbacks &callbacks, TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);
		protected:
			inline std::string getName() { return "wall"; }
			void saveXMLChild(TiXmlElement* item);

			Cairo::RefPtr<Cairo::ImageSurface> image;
	};
};

#endif
