#include "player.hpp"

using namespace Objects;

#include "../misc/data.hpp"

#ifdef HAVE_MATH_H
# include <cmath>
#endif

#define PLAYER_SIZE_X 28
#define IMG_SIZE_X 58

#define PLAYER_SIZE_Y 40
#define IMG_SIZE_Y 82

Player::Player(EditorCallbacks &callbacks, double sx, double sy, double rotation) :
	Rectangular(callbacks, sx, sy, PLAYER_SIZE_X, PLAYER_SIZE_Y, rotation, Misc::Point(PLAYER_SIZE_X, PLAYER_SIZE_Y), Misc::Point(PLAYER_SIZE_X, PLAYER_SIZE_Y))
{
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("player.png"));
}

Player::Player(EditorCallbacks &callbacks, TiXmlElement &item) :
	Rectangular(callbacks, item, Misc::Point(PLAYER_SIZE_X, PLAYER_SIZE_Y), Misc::Point(PLAYER_SIZE_X, PLAYER_SIZE_Y))
{
	sx = PLAYER_SIZE_X;
	sy = PLAYER_SIZE_Y;
	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("player.png"));
}

void Player::saveXMLChild(TiXmlElement* item)
{
	Rectangular::saveXMLChild(item);
}

void Player::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(x, y);
	cr->rotate(rotation);
	cr->scale((float)PLAYER_SIZE_X / IMG_SIZE_X, (float)PLAYER_SIZE_X / IMG_SIZE_X);

	cr->set_source(img, -IMG_SIZE_X / 2, -IMG_SIZE_Y / 2);
	cr->rectangle(-IMG_SIZE_X / 2, -IMG_SIZE_Y / 2, IMG_SIZE_X, IMG_SIZE_Y);
	cr->fill();

	cr->scale((float)IMG_SIZE_X / PLAYER_SIZE_X, (float)IMG_SIZE_X / PLAYER_SIZE_X);
	cr->rotate(-rotation);
	cr->translate(-x, -y);
}
