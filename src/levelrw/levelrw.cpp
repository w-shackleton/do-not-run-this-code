#include<levelrw.hpp>

#include <iostream>
using namespace Levels;
using namespace std;

LevelWriter::LevelWriter()
{
	decl = new TiXmlDeclaration("1.0", "", "");
	doc.LinkEndChild(decl);
}

void LevelWriter::write(string filename, std::list<Objects::SpaceItem *>* objs)
{
	this->objs = objs;
	level = new TiXmlElement("level");

	cout << "Test" << endl;

	doc.LinkEndChild(level);

	// Clean for next time
	cleanup();
}

void LevelWriter::cleanup()
{
	doc.RemoveChild(level);
}
