#ifndef BLOCK_H
#define BLOCK_H

#include "rectangular.hpp"

namespace Objects
{
	enum BlockType {
		BLOCK_CENTER,
		BLOCK_EDGE,
		BLOCK_CORNER,
		BLOCK_FADE,

		BLOCK_WALLJOIN1,
		BLOCK_WALLJOIN2,
		BLOCK_WALLJOIN3,
		BLOCK_WALL_CORNER,
		BLOCK_WALL_CORNER2,

		BLOCK_CONCAVE1,
	};

	/**
	 * A block item, in a square grid.
	 */
	class Block : public Rectangular
	{
		public:
			Block(EditorCallbacks &callbacks, double x, double y, double sx, double sy, int type);
			Block(EditorCallbacks &callbacks, TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

			void onCMenuItemClick(int id);
		protected:
			inline std::string getName() { return "block"; }
			void saveXMLChild(TiXmlElement* item);

			int type;

			Cairo::RefPtr<Cairo::SurfacePattern> pattern;
			Cairo::RefPtr<Cairo::ImageSurface> image;
		private:
			void loadImageForType(int type);
			static Misc::Point getMinSizeForType(int type);
			static Misc::Point getMaxSizeForType(int type);

			void setupContext();

			wxMenuItem* hasVortexMenuItem;
			bool hasVortex;
	};
};

#endif
