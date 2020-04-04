#ifndef LEVELBOUNDS_H
#define LEVELBOUNDS_H

#include "rectangular.hpp"

namespace Objects
{
	class LevelBounds : public Rectangular
	{
		public:
			LevelBounds(EditorCallbacks &callbacks, double sx, double sy);
			LevelBounds(EditorCallbacks &callbacks, TiXmlElement &item);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);

			bool isClicked(int cx, int cy);
		protected:
			inline std::string getName() { return "bounds"; }
			void saveXMLChild(TiXmlElement* item);
	};
};

#endif
