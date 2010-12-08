#ifndef LEVELMANAGER_H
#define LEVELMANAGER_H

#include <list>

#include "levelrw.hpp"

#include "geometry.hpp"

#define LEVEL_MIN 200
#define LEVEL_MAX 2000

namespace Levels
{
	class LevelManager
	{
		public:
			LevelManager();
			~LevelManager();

			void newLevel();
			bool openLevel(std::string filename);
			bool save();
			void saveLevel(std::string filename);

			void change();
			bool levelChanged;
			void cleanObjs();

			std::list<Objects::SpaceItem *>& objs;
		protected:
			LevelWriter writer;
			std::list<Objects::SpaceItem *> _objs;

			std::string levelPath;

			std::string levelName;
			std::string creator;

			Misc::Point position, speed, border;

			double borderMin, borderMax;
	};
};

#endif
