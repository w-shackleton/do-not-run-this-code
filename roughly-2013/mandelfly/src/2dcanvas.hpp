/**
 * This is a DrawingArea that displays the given Gdk::PixBuf
 */
#ifndef CANVAS_2D_H
#define CANVAS_2D_H

#include <gtkmm/drawingarea.h>

namespace mandel {
	namespace gui {
		class Canvas2D : public Gtk::DrawingArea {
			public:
				virtual bool on_draw(const Cairo::RefPtr<Cairo::Context>& cr);
				// TODO: Handle threading
				inline void setSurface(Cairo::RefPtr<Cairo::ImageSurface> surface) {
					currentSurface = surface;
					// surface->write_to_png("fractal.png");
					queue_draw();
				}
			protected:
				Cairo::RefPtr<Cairo::ImageSurface> currentSurface;
		};
	}
}

#endif
