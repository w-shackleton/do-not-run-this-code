#ifndef INFOBOX_H
#define INFOBOX_H

#include "rectangular.hpp"

namespace Objects
{
	class InfoBox : public Rectangular
	{
		public:
			InfoBox(double x, double y, double rotation);
			void draw(Cairo::RefPtr<Cairo::Context> &cr);
	};
};

#endif
