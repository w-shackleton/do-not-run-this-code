#include "planet.hpp"

using namespace Objects;
using namespace Objects::Helpers;
using namespace Misc;
using namespace std;

#ifdef HAVE_MATH_H
# include <cmath>
#endif

#define PLANET_MIN 30
#define PLANET_MAX 200

#define IMG_RADIUS 150

#include "../misc/data.hpp"
#include "planetEditor.hpp"

// Type HERE is array pos, not ID
Planet::Planet(EditorCallbacks &callbacks, int type, double sx, double sy, double sradius) :
	Spherical(callbacks, sx, sy, sradius, PLANET_MIN, PLANET_MAX),
	type(type)
{
	planetType = planetTypes[type];
	cout << "Type of new planet: " << type << endl;
//	planetType = planetTypes[0];
//	for(vector<PlanetType>::iterator it = planetTypes.begin(); it != planetTypes.end(); it++)
//		if(it->id == type)
//			planetType = *it;

	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath(planetType.filename));
	shadow = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("planet-s.png"));

	contextMenu->Append(contextMenuNextAvailableSlot++, _("&Edit"));
}

// Type HERE is ID, not simple array pos
Planet::Planet(EditorCallbacks &callbacks, TiXmlElement &item) :
	Spherical(callbacks, item, PLANET_MIN, PLANET_MAX)
{
	int typeId;
	item.QueryIntAttribute("type", &typeId);
	planetType = planetTypes[0];
	int typeNum = 0;
	for(vector<PlanetType>::iterator it = planetTypes.begin(); it != planetTypes.end(); it++)
	{
		if(it->id == typeId)
		{
			planetType = *it;
			type = typeNum;
		}
		typeNum++;
	}

	img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath(planetType.filename));
	shadow = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath("planet-s.png"));

	contextMenu->Append(contextMenuNextAvailableSlot++, _("&Edit"));
}

void Planet::saveXMLChild(TiXmlElement* item)
{
	Spherical::saveXMLChild(item);
	item->SetAttribute("type", planetType.id);
}

void Planet::draw(Cairo::RefPtr<Cairo::Context> &cr)
{
	cr->translate(x, y);
//	cr->rotate(rotation);
	cr->scale(radius / IMG_RADIUS, radius / IMG_RADIUS);

	if(!planetType.bgCol.noColour)
	{
		cr->set_source_rgb(planetType.bgCol.r, planetType.bgCol.g, planetType.bgCol.b);
		cr->arc(0, 0, IMG_RADIUS, 0, M_PI * 2);
		cr->fill();
	}

	cr->set_source(img, -IMG_RADIUS, -IMG_RADIUS);
	cr->rectangle(-IMG_RADIUS, -IMG_RADIUS, IMG_RADIUS * 2, IMG_RADIUS * 2);
	cr->fill();

	cr->set_source(shadow, -IMG_RADIUS, -IMG_RADIUS);
	cr->rectangle(-IMG_RADIUS, -IMG_RADIUS, IMG_RADIUS * 2, IMG_RADIUS * 2);
	cr->fill();

	cr->scale(IMG_RADIUS / radius, IMG_RADIUS / radius);
//	cr->rotate(-rotation);
	cr->translate(-x, -y);
}

void Planet::onCMenuItemClick(int id)
{
	SpaceItem::onCMenuItemClick(id);
	switch(id)
	{
		case ID_CMenu_2:
			cout << "Editing..." << endl;
			PlanetEditor editor(NULL, type);
			if(editor.ShowModal() == 0)
			{
				type = editor.type;
				planetType = planetTypes[type];
				img = Cairo::ImageSurface::create_from_png(Misc::Data::getFilePath(planetType.filename));
				callbacks.onRefresh();
			}
			return;
	}
}

PlanetType::PlanetType(int id, std::string filename, std::string planetName, double bounciness, double density, int minSize, int maxSize, Colour bgCol) :
	id(id),
	filename(filename),
	planetName(planetName),
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
	// Bounciness & density from 0 - 1.5?
			//   ID			Filename	Name			bn   dn   min max col
	push_back(PlanetType(PLANET_nobounce1,	"planet1.png",	"Gas planet",		0.2, 0.5, 30, 250, Colour(true)));
	push_back(PlanetType(PLANET_sticky1,	"planet5.png",	"Sticky",		0,   0.9, 40, 250, Colour(255, 128, 0)));
	push_back(PlanetType(PLANET_bounce2,	"planet2.png",	"Slightly Bouncy",	1,   0.1, 20, 200, Colour(100, 100, 255)));
	push_back(PlanetType(PLANET_n1,		"planet3.png",	"Normal type 1",	0.6, 0.6, 20, 200, Colour(0, 100, 200)));
	push_back(PlanetType(PLANET_n2,		"planet4.png",	"Normal type 2",	0.7, 0.7, 20, 200, Colour(0, 100, 200)));
	push_back(PlanetType(PLANET_n3,		"planet6.png",	"Normal type 3",	0.8, 0.8, 20, 200, Colour(50, 60, 60)));
	push_back(PlanetType(PLANET_bounce1,	"planetbouncy.png","Bouncy",		1.3, 0.8, 20, 150, Colour(true)));
}

