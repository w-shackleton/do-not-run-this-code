#include "planet.hpp"

using namespace Objects;
using namespace Misc;
using namespace std;

#ifdef HAVE_MATH_H
# include <cmath>
#endif

#define PLANET_MIN 30
#define PLANET_MAX 200

Planet::Planet(int type, double sx, double sy, double sradius) :
	Spherical(sx, sy, sradius, PLANET_MIN, PLANET_MAX),
	type(type)
{
	planetType = planetTypes[0];
	for(vector<PlanetType>::iterator it = planetTypes.begin(); it != planetTypes.end(); it++)
		if(it->id == type)
			planetType = *it;
}

Planet::Planet(TiXmlElement &item) :
	Spherical(item, PLANET_MIN, PLANET_MAX)
{
	item.QueryIntAttribute("type", &type);
}

void Planet::saveXMLChild(TiXmlElement* item)
{
	Spherical::saveXMLChild(item);
	item->SetAttribute("type", type);
}

void Planet::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->set_source_rgb(0.1, 0.9, 0.1);
	cr->arc(x, y, radius, -1, 2 * M_PI);
	cr->fill();

	cr->set_source_rgb(0, 0, 0);
	cr->arc(x, y, radius, 0, 2 * M_PI);
	cr->stroke();
}

PlanetType::PlanetType(int id, std::string filename, double bounciness, double density, int minSize, int maxSize, Colour bgCol) :
	id(id),
	filename(filename),
	bounciness(bounciness),
	density(density),
	minSize(minSize),
	maxSize(maxSize),
	bgCol(bgCol)
{
}

PlanetType::PlanetType()
{
}

PlanetTypes::PlanetTypes() :
	vector<PlanetType>()
{
	push_back(PlanetType(PLANET_nobounce1,	"planet1.png", 0.2, 0.5, 30, 250, Colour(true)));
	push_back(PlanetType(PLANET_sticky1,	"planet5.png", 0, 0.9, 40, 250, Colour(255, 0, 0)));
	push_back(PlanetType(PLANET_bounce2,	"planet2.png", 1, 0.1, 20, 200, Colour(100, 100, 255)));
	push_back(PlanetType(PLANET_n1,		"planet3.png", 0.6, 0.6, 20, 200, Colour(0, 100, 200)));
	push_back(PlanetType(PLANET_n2,		"planet4.png", 0.7, 0.7, 20, 200, Colour(0, 100, 200)));
	push_back(PlanetType(PLANET_n3,		"planet6.png", 0.8, 0.8, 20, 200, Colour(50, 60, 60)));
}

