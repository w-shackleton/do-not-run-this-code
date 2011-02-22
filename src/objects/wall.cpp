#include "wall.hpp"

using namespace Objects;

#define RECT_SIZE_Y 16
#define RECT_MIN_X 80
#define RECT_MAX_X 1000

#define IMG_SIZE 32

#include "../misc/data.hpp"

Wall::Wall(EditorCallbacks &callbacks, double x, double y, double sx, double rotation) :
	Rectangular(callbacks, x, y, sx, RECT_SIZE_Y, rotation, Misc::Point(RECT_MIN_X, RECT_SIZE_Y), Misc::Point(RECT_MAX_X, RECT_SIZE_Y))
{
	wallside = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("wallside.png"));
	wall= Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("wall.png"));
}

Wall::Wall(EditorCallbacks &callbacks, TiXmlElement &item) :
	Rectangular(callbacks, item, Misc::Point(RECT_MIN_X, RECT_SIZE_Y), Misc::Point(RECT_MAX_X, RECT_SIZE_Y))
{
	sy = RECT_SIZE_Y;
	wallside = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("wallside.png"));
	wall= Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("wall.png"));
}

void Wall::saveXMLChild(TiXmlElement* item)
{
	Rectangular::saveXMLChild(item);
}

void Wall::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(x, y);
	cr->rotate(rotation);
	cr->scale(.5, .5);

	cr->set_source(wall, -RECT_MAX_X, -sy);
	cr->rectangle(-sx + IMG_SIZE, -sy, (sx - IMG_SIZE) * 2, sy * 2);
	cr->fill();

	for(int i = 0; i < 2; i++) // draw on both sides by rotating round
	{
		cr->rotate(M_PI);
		cr->set_source(wallside, -sx, -sy);
		cr->rectangle( -sx, -sy, IMG_SIZE, IMG_SIZE);
		cr->fill();
	}

	cr->scale(2, 2);
	cr->rotate(-rotation);
	cr->translate(-x, -y);
}
