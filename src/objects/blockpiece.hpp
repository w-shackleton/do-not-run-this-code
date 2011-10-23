#ifndef BLOCK_H
#define BLOCK_H

#include "rectangular.hpp"

namespace Objects
{
	enum BlockType {
		BLOCK_CENTER,
		BLOCK_EDGE,
		BLOCK_CORNER,
		BLOCK_FADE
	};

	class Block : public Rectangular
	{
		public:
			Block(EditorCallbacks &callbacks, double x, double y, double sx, double sy, int type);
			Block(EditorCallbacks &callbacks, TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);
		protected:
			inline std::string getName() { return "wall"; }
			void saveXMLChild(TiXmlElement* item);

			int type;

			Cairo::RefPtr<Cairo::SurfacePattern> pattern;
			Cairo::RefPtr<Cairo::ImageSurface> image;
		private:
			void loadImageForType(int type);
			static Misc::Point getMinSizeForType(int type);
			static Misc::Point getMaxSizeForType(int type);
	};
};

#endif
