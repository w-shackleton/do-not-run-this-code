/**
 * A display module, for showing fractals in 2D
 */
#ifndef FRACTAL_2D_H
#define FRACTAL_2D_H

#include "2dcanvas.hpp"
#include "fractalManager.hpp"
#include<glibmm/threads.h>
#include<glibmm/dispatcher.h>
#include<cairomm/cairomm.h>
#include<queue>

namespace mandel {
	typedef struct {
		int x, y;
		int width, height;
		Cairo::RefPtr<Cairo::ImageSurface> img;
	} ImagePart;

	class Fractal2D : public mandel::gui::Canvas2D {
		public:
			Fractal2D();
			virtual ~Fractal2D();

			inline void setWorker(mandel::comp::Computations *worker) {
				this->worker = worker;
				manager = new FractalManager(*worker);
				redraw();
			}

			inline void setIterations(int iterations) {
				if(iterations <= 0) return;
				manager->iterations = iterations;
				redraw();
			}
			inline int getIterations() {
				return manager->iterations;
			}
			inline void setTiles(int tiles) {
				if(tiles <= 0) return;
				this->tiles = tiles;
				redraw();
			}
			inline int getTiles() {
				return this->tiles;
			}
			inline void increasePrecision() {
				manager->increasePrecision();
				redraw();
			}
			inline void decreasePrecision() {
				manager->decreasePrecision();
				redraw();
			}

			virtual bool on_draw(const Cairo::RefPtr<Cairo::Context>& cr);

		private:
			comp::Computations *worker;
			FractalManager *manager;

			Glib::Threads::Thread *processThread;
			Glib::Dispatcher onNewTile_;
			Glib::Dispatcher onProcessThreadEnd_;
			int allocatedWidth, allocatedHeight;

			void computeMandelbrot();
			void onNewTile();
			void onProcessThreadEnd();

			gdouble startX, startY;
			bool validSelection;
			gdouble currentX, currentY;

			bool onMouseDown(GdkEventButton *event);
			bool onMouseUp(GdkEventButton *event);
			bool onMouseMove(GdkEventMotion *event);

			void redraw();

			bool cancelCompute;

			Cairo::RefPtr<Cairo::ImageSurface> drawingSurface;
			Cairo::RefPtr<Cairo::Context> drawingContext;

			std::queue<ImagePart> imagePartQueue;
			Glib::Threads::Mutex imagePartQueueMutex;

			int tiles;
	};
}

#endif
