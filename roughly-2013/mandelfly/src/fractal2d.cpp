#include "fractal2d.hpp"

using namespace mandel;
using namespace mandel::gui;
using namespace Glib;
using namespace Glib::Threads;
using namespace std;

Fractal2D::Fractal2D() :
	tiles(10)
{
	add_events(Gdk::BUTTON_PRESS_MASK | Gdk::BUTTON_RELEASE_MASK | Gdk::POINTER_MOTION_MASK);
	signal_button_press_event().connect(sigc::mem_fun(*this, &Fractal2D::onMouseDown));
	signal_button_release_event().connect(sigc::mem_fun(*this, &Fractal2D::onMouseUp));
	signal_motion_notify_event().connect(sigc::mem_fun(*this, &Fractal2D::onMouseMove));

	onNewTile_.connect(mem_fun(*this, &Fractal2D::onNewTile));
	onProcessThreadEnd_.connect(mem_fun(*this, &Fractal2D::onProcessThreadEnd));
}
Fractal2D::~Fractal2D() {
	delete manager;
}

void Fractal2D::redraw() {
	if(!manager) {
		return;
	}

	// Tell thread to stop, if running
	cancelCompute = true;
	if(processThread) // Run clean-up function now.
		onProcessThreadEnd();

	allocatedWidth = get_allocated_width();
	allocatedHeight = get_allocated_height();

	if(!drawingSurface) { // Create first time
		drawingSurface = Cairo::ImageSurface::create(Cairo::FORMAT_ARGB32, allocatedWidth, allocatedHeight);
		setSurface(drawingSurface);
		drawingContext = Cairo::Context::create(drawingSurface);
		manager->adjustAspectRatio(allocatedWidth, allocatedHeight);
	}
	if(drawingSurface->get_width() != allocatedWidth ||
			drawingSurface->get_height() != allocatedHeight)
	{ // Create new image
		drawingSurface = Cairo::ImageSurface::create(Cairo::FORMAT_ARGB32, allocatedWidth, allocatedHeight);
		setSurface(drawingSurface);
		drawingContext = Cairo::Context::create(drawingSurface);
		manager->adjustAspectRatio(allocatedWidth, allocatedHeight);
	}


	processThread = Thread::create(mem_fun(*this, &Fractal2D::computeMandelbrot), "Fractal computing thread");
}

void Fractal2D::computeMandelbrot() {
	cancelCompute = false;
	// TODO: Make these configurable
	int tilesX = tiles;
	int tilesY = tiles;

	for(int y = 0; y < tilesX; y++) {
		for(int x = 0; x < tilesX; x++) {
			if(cancelCompute) return;
			ImagePart part;
			part.x = x * (allocatedWidth / tilesX);
			part.y = y * (allocatedHeight / tilesY);
//			if(x == tilesX - 1) // Edge pieces will be bigger due to rounding
//				part.width = allocatedWidth - part.x;
//			else
				part.width = allocatedWidth / tilesX;
//			if(y == tilesY - 1)
//				part.height = allocatedHeight - part.y;
//			else
				part.height = allocatedHeight / tilesY;
			Cairo::RefPtr<Cairo::ImageSurface> tile =
				manager->redraw(
						(float)part.x / (float)allocatedWidth,
						(float)part.y / (float)allocatedHeight,
						(float)part.width / (float)allocatedWidth,
						(float)part.height / (float)allocatedHeight,
						part.width, part.height);
			part.img = tile;
			{
				Threads::Mutex::Lock lock(imagePartQueueMutex);
				imagePartQueue.push(part);
			}
			onNewTile_();
			usleep(20000);
		}
	}
	onProcessThreadEnd_();
}
void Fractal2D::onNewTile() {
	ImagePart tile;
	{
		Threads::Mutex::Lock lock(imagePartQueueMutex);
		if(!imagePartQueue.empty()) {
			tile = imagePartQueue.front();
			imagePartQueue.pop();
		}
	}
	if(tile.img) {
		drawingContext->set_source(tile.img, tile.x, tile.y);
//		drawingContext->reset_clip();
//		drawingContext->begin_new_path();
//		drawingContext->move_to(tile.x, tile.y);
//		drawingContext->line_to(tile.x, tile.y + tile.height);
//		drawingContext->line_to(tile.x + tile.width, tile.y + tile.height);
//		drawingContext->line_to(tile.x + tile.width, tile.y);
//		drawingContext->close_path();
//		drawingContext->clip();
		drawingContext->paint();
		// Now, delete underlying array beneath tile.img
		delete[] tile.img->get_data();
		queue_draw();
	}
}
void Fractal2D::onProcessThreadEnd() {
	if(!processThread) return;
	processThread->join();
	processThread = NULL;
}

bool Fractal2D::onMouseDown(GdkEventButton *event) {
	cout << "Mouse down" << endl;
	startX = event->x;
	startY = event->y;
	validSelection = true;
	return true;
}
bool Fractal2D::onMouseUp(GdkEventButton *event) {
	if(!validSelection) {
		redraw();
		return false;
	}
	cout << "Mouse up" << endl;
	float width = get_allocated_width();
	float height = get_allocated_height();
	
	gdouble endX = event->x;
	gdouble endY = event->y;

	if(startX > endX) {
		float tmp = startX; startX = endX; endX = tmp;
	}
	if(startY > endY) {
		float tmp = startY; startY = endY; endY = tmp;
	}

	float centreX = (startX + endX) / 2;
	float centreY = (startY + endY) / 2;

	float sizeX = endX - startX;
	float sizeY = endY - startY;

	manager->selectArea(
			sizeX / width,
			sizeY / height,
			centreX / width,
			centreY / height);
	manager->adjustAspectRatio(allocatedWidth, allocatedHeight);
	validSelection = false;
	redraw();
	return true;
}

bool Fractal2D::onMouseMove(GdkEventMotion *event) {
	// TODO: Show iteration points
	currentX = event->x / (float)allocatedWidth;
	currentY = event->y / (float)allocatedHeight;
	queue_draw();
	return false;
}

bool Fractal2D::on_draw(const Cairo::RefPtr<Cairo::Context>& cr) {
	Canvas2D::on_draw(cr);

	if(manager) {
		// Draw iteration points
		// Use BN4_4 always
		auto pos = manager->getPos();
		auto size = manager->getSize();
		complex<bignum::BN4_4> c(
				bignum::BN4_4(currentX - 0.5) * size.real() + pos.real(),
				bignum::BN4_4(currentY - 0.5) * size.imag() + pos.imag());
		int iterations = 400;
		cr->set_source_rgb(1, 0, 0);
		complex<bignum::BN4_4> z;
		while(iterations--) {
			z = z*z + c;
			// Translate to local coords
			// z - pos
			// And just like that I have to perform a bignum division. Great.
			if(std::norm(z) > bignum::BN4_4(4)) {
				break;
			}
		}
	}

	return true;
}
