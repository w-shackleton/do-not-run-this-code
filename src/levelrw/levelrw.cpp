#include<levelrw.hpp>

#include <iostream>
using namespace Levels;
using namespace Objects;
using namespace std;

#include <typeinfo>

#include "../objects/spaceItems.hpp"

LevelWriter::LevelWriter()
{
	decl = new TiXmlDeclaration("1.0", "", "");
	doc.LinkEndChild(decl);
}

void LevelWriter::write(string filename, std::list<Objects::SpaceItem *>* objs, string levelName, string creator,
		const Objects::Player &p,
		const Objects::Portal &portal,
		LevelBounds &bounds,
		int& numberStars)
{
	cout << "Saving level" << endl;
	this->objs = objs;
	level = new TiXmlElement("level");

	// Title
	{
		TiXmlElement *name = new TiXmlElement("name");
		TiXmlText *text = new TiXmlText(levelName);
		name->LinkEndChild(text);
		level->LinkEndChild(name);
	}

	// Creator
	{
		TiXmlElement *name = new TiXmlElement("creator");
		TiXmlText *text = new TiXmlText(creator);
		name->LinkEndChild(text);
		level->LinkEndChild(name);
	}

	// Start
	{
		TiXmlElement *name = new TiXmlElement("start");
		name->SetDoubleAttribute("x", p.x);
		name->SetDoubleAttribute("y", -p.y);
		level->LinkEndChild(name);
	}

	// Bounds
	{
		TiXmlElement *name = new TiXmlElement("bounds");
		name->SetDoubleAttribute("x", bounds.sx);
		name->SetDoubleAttribute("y", bounds.sy);
		level->LinkEndChild(name);
	}

	// Portal
	{
		TiXmlElement *name = new TiXmlElement("portal");
		name->SetDoubleAttribute("x", portal.x);
		name->SetDoubleAttribute("y", -portal.y);
		level->LinkEndChild(name);
	}

	// Stars
	{
		TiXmlElement *name = new TiXmlElement("stars");
		name->SetAttribute("value", numberStars);
		level->LinkEndChild(name);
	}

	// Items
	{
		TiXmlElement *items = new TiXmlElement("items");

		// Loop
		{
			for(list<SpaceItem *>::iterator it = objs->begin(); it != objs->end(); it++)
			{
				(*it)->saveXML(*items);
			}
		}

		level->LinkEndChild(items);
	}

	doc.LinkEndChild(level);

	doc.SaveFile(filename);
	// Clean for next time
	cleanup();

	cout << "Level Saved" << endl;
}

void LevelWriter::cleanup()
{
	doc.RemoveChild(level);
}

LevelReader::LevelReader()
{
}

bool LevelReader::open(const std::string &filename, std::list<Objects::SpaceItem *>* objs, std::string &levelName, std::string &levelCreator,
		Objects::Player& p,
		Objects::Portal& portal,
		LevelBounds &bounds,
		int* numberStars)
{
	TiXmlDocument doc(filename);
	if(!doc.LoadFile()) return false;

	cout << "Loading level..." << endl;

	TiXmlElement *level = doc.FirstChildElement("level");

	TiXmlElement *name = level->FirstChildElement("name");
	if(name)
	{
		if(const char *n = name->GetText())
			levelName = n;
		else
			levelName = "";
	}

	TiXmlElement *creator = level->FirstChildElement("creator");
	if(creator)
	{
		if(const char *n = creator->GetText())
			levelCreator = n;
		else
			levelCreator = "";
	}

	TiXmlElement *start = level->FirstChildElement("start");
	if(start)
	{
		start->QueryDoubleAttribute("x", &p.x);
		start->QueryDoubleAttribute("y", &p.y);
		p.y = -p.y;
	}

	TiXmlElement *levelBounds = level->FirstChildElement("bounds");
	if(levelBounds)
	{
		levelBounds->QueryDoubleAttribute("x", &bounds.sx);
		levelBounds->QueryDoubleAttribute("y", &bounds.sy);
	}

	TiXmlElement *portalElem = level->FirstChildElement("portal");
	if(portalElem)
	{
		portalElem->QueryDoubleAttribute("x", &portal.x);
		portalElem->QueryDoubleAttribute("y", &portal.y);
		portal.y = -portal.y;
	}

	TiXmlElement *starsElem = level->FirstChildElement("stars");
	if(starsElem)
	{
		starsElem->QueryIntAttribute("value", numberStars);
	}

	TiXmlElement *items = level->FirstChildElement("items");
	if(items)
	{
		TiXmlElement *item = items->FirstChildElement();
		while(item)
		{
			string itemName = item->ValueStr();
			/**/ if(itemName == "infobox")
				objs->push_back(new Objects::InfoBox(*callbacks, *item));
			else if(itemName == "planet")
				objs->push_back(new Objects::Planet(*callbacks, *item));
			else if(itemName == "blackhole")
				objs->push_back(new Objects::BlackHole(*callbacks, *item));
			else if(itemName == "gravity")
				objs->push_back(new Objects::Vortex(*callbacks, *item));
			else if(itemName == "wall")
				objs->push_back(new Objects::Wall(*callbacks, *item));
			else if(itemName == "star")
				objs->push_back(new Objects::Star(*callbacks, *item));

			item = item->NextSiblingElement();
		}
	}

	return true;
}

bool LevelReader::open(const std::string &filename, std::string &levelName, std::string &levelCreator)
{
	TiXmlDocument doc(filename);
	if(!doc.LoadFile()) return false;

	TiXmlElement *level = doc.FirstChildElement("level");

	TiXmlElement *name = level->FirstChildElement("name");
	if(name)
	{
		if(const char *n = name->GetText())
			levelName = n;
		else
			levelName = "";
	}

	TiXmlElement *creator = level->FirstChildElement("creator");
	if(creator)
	{
		if(const char *n = creator->GetText())
			levelCreator = n;
		else
			levelCreator = ""; }
	return true;
}

void LevelReader::setEditorCallbacks(EditorCallbacks *callbacks)
{
	this->callbacks = callbacks;
}

