#include <levelManager.hpp>

using namespace Levels;
using namespace std;

LevelManager::LevelManager() :
	objs(_objs)
{
}

LevelManager::~LevelManager()
{
	cleanObjs();
}

void LevelManager::openLevel(std::string filename)
{
	// Load level here...
	cleanObjs();
	objs.push_back(new Objects::LevelWall(100, 100));
	objs.push_back(new Objects::Planet(100, 100, 60));
	objs.push_back(new Objects::Wall(200, 200, 300, M_PI / 8 * 1));
	objs.push_back(new Objects::InfoBox(200, 200, M_PI / 8 * -2));
	objs.push_back(new Objects::Vortex(400, 100, 200, 200, M_PI / 8 * -2));
}

void LevelManager::saveLevel(std::string filename)
{
	writer.write(filename, &objs);
}

void LevelManager::cleanObjs()
{
	for(list<Objects::SpaceItem *>::iterator it = objs.begin(); it != objs.end(); it++)
	{
		delete *it;
		cout << "Deleting..." << endl;
	}
	objs.clear();
}
