#include<levelrw.hpp>

#include <iostream>
using namespace Levels;
using namespace Objects;
using namespace std;

#include <typeinfo>

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
}

void LevelWriter::cleanup()
{
	doc.RemoveChild(level);
}
