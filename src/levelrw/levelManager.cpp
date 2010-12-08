#include <levelManager.hpp>

using namespace Levels;
using namespace std;
using namespace Misc;

#include "spaceItems.hpp"

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
	// Load level here...
	cleanObjs();
	objs.push_back(new Objects::Planet(100, 100, 60));
	objs.push_back(new Objects::Wall(200, 200, 300, M_PI / 8 * 1));
	objs.push_back(new Objects::InfoBox(200, 200, M_PI / 8 * -2));
	objs.push_back(new Objects::Vortex(400, 100, 200, 200, M_PI / 8 * -2));

	levelName = "Test level";
	creator = "Will";
	border = Point(1000, 1000);
	return true; // Return false if failed to load
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
	writer.write(filename, &objs, levelName, creator, position.x, position.y, speed.x, speed.y, border.x, border.y);
	levelChanged = false;
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
