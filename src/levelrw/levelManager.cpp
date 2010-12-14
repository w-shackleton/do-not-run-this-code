#include <levelManager.hpp>

using namespace Levels;
using namespace std;
using namespace Misc;

#include "spaceItems.hpp"

#include <fstream>

LevelManager::LevelManager() :
	objs(_objs)
{
	newLevel();
}

LevelManager::~LevelManager()
{
	cleanObjs();
}

void LevelManager::newLevel()
{
	cleanObjs();

	border = Point(500, 500);
	position = Point(0, 0);
	speed = Point(0, 0);
	levelPath = "";
	levelChanged = true;
}

bool LevelManager::openLevel(std::string filename)
{
	cleanObjs();

	if(!ifstream(filename.c_str()))
		return false;
	return reader.open(filename, &objs, levelName, creator, position.x, position.y, speed.x, speed.y, border.x, border.y);
}

bool LevelManager::save()
{
	if(levelPath == "")
		return false;
	saveLevel(levelPath);
	return true;
}

void LevelManager::saveLevel(std::string filename)
{
	if(!(filename.length() - Misc::stringToLower(filename).rfind(".slv") == 4)) // If name doesn't end in ending
	{
		filename += ".slv";
	}
	// INSERT FILENAME CHECK CODE
	writer.write(filename, &objs, levelName, creator, position.x, position.y, speed.x, speed.y, border.x, border.y);
	levelChanged = false;
	levelPath = filename;
}

void LevelManager::change()
{
	levelChanged = true;
}

void LevelManager::cleanObjs()
{
	for(list<Objects::SpaceItem *>::iterator it = objs.begin(); it != objs.end(); it++)
	{
		delete *it;
	}
	objs.clear();
}
