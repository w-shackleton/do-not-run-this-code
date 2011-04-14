#include <levelManager.hpp>

using namespace Levels;
using namespace std;
using namespace Misc;
using namespace Objects;

#include <fstream>

LevelManager::LevelManager() :
	objs(_objs)
{
}

LevelManager::~LevelManager()
{
	cleanObjs();
	// Release pointer early as to avoid grumbles in spacePanel.cpp
	levelBounds.release();
	p.release();
	portal.release();
}

void LevelManager::newLevel(std::string filename)
{
	cleanObjs();

	levelBounds.reset(new LevelBounds(*callbacks, 1000, 1000));
	p.reset(new Player(*callbacks, 0, 0, 0));
	portal.reset(new Portal(*callbacks, -100, 0));
	levelPath = filename;
	levelChanged = true;

	save();
}

bool LevelManager::openLevel(std::string filename)
{
	levelPath = filename;
	cleanObjs();

	levelBounds.reset(new LevelBounds(*callbacks, 1000, 1000));
	p.reset(new Player(*callbacks, 0, 0, 0));
	portal.reset(new Portal(*callbacks, -100, 0));

	if(!ifstream(filename.c_str()))
	{
		newLevel(filename);
		return true;
	}
	return reader.open(filename, &objs, levelName, creator, *p, *portal, *levelBounds);
}

bool LevelManager::save()
{
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
	writer.write(filename, &objs, levelName, creator, *p, *portal, *levelBounds);
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

void LevelManager::setEditorCallbacks(EditorCallbacks *callbacks)
{
	this->callbacks = callbacks;
	reader.setEditorCallbacks(callbacks);
}
