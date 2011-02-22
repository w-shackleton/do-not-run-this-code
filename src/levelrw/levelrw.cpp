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
		double px, double py,
		double ssx, double ssy,
		double bx, double by)
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
		name->SetDoubleAttribute("x", px);
		name->SetDoubleAttribute("y", py);
		level->LinkEndChild(name);
	}

	// Startspeed
	{
		TiXmlElement *name = new TiXmlElement("startspeed");
		name->SetDoubleAttribute("x", ssx);
		name->SetDoubleAttribute("y", ssy);
		level->LinkEndChild(name);
	}

	// Bounds
	{
		TiXmlElement *name = new TiXmlElement("bounds");
		name->SetDoubleAttribute("x", bx);
		name->SetDoubleAttribute("y", by);
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
		double &px, double &py,
		double &ssx, double &ssy,
		double &bx, double &by)
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
		start->QueryDoubleAttribute("x", &px);
		start->QueryDoubleAttribute("y", &py);
	}

	TiXmlElement *startspeed = level->FirstChildElement("startspeed");
	if(startspeed)
	{
		startspeed->QueryDoubleAttribute("x", &ssx);
		startspeed->QueryDoubleAttribute("y", &ssy);
	}

	TiXmlElement *bounds = level->FirstChildElement("bounds");
	if(bounds)
	{
		bounds->QueryDoubleAttribute("x", &bx);
		bounds->QueryDoubleAttribute("y", &by);
	}

	TiXmlElement *items = level->FirstChildElement("items");
	if(items)
	{
		cout << "Items valid" << endl;
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

			item = item->NextSiblingElement();
			cout << "Loaded item" << endl;
		}
	}

	cout << "Finished loading" << endl;

	return true;
}

void LevelReader::setEditorCallbacks(EditorCallbacks *callbacks)
{
	this->callbacks = callbacks;
}

