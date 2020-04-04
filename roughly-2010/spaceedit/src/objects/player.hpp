#ifndef PLAYER_H
#define PLAYER_H

#include "rectangular.hpp"

namespace Objects
{
	class Player : public Rectangular
	{
		protected:
			inline std::string getName() { return "start"; }
			void saveXMLChild(TiXmlElement* item);
		public:
			Player(EditorCallbacks &callbacks, double sx, double sy, double rotation);
			Player(EditorCallbacks &callbacks, TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

			Cairo::RefPtr<Cairo::ImageSurface> img;
	};
};
#endif
