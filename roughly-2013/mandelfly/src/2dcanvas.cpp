#include "2dcanvas.hpp"

using namespace mandel::gui;
using namespace Cairo;
using namespace Gtk;
using namespace Gdk;

bool Canvas2D::on_draw(const Cairo::RefPtr<Cairo::Context>& cr) {
	if(currentSurface) {
		cr->set_source(currentSurface, 0, 0);
		cr->paint();
	} else {
	}
	return true;
}
