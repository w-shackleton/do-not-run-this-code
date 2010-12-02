#ifndef LEVELMANAGER_H
#define LEVELMANAGER_H

#include <list>

#include "levelrw.hpp"

#include "geometry.hpp"

namespace Levels
{
	class LevelManager
	{
		public:
			LevelManager();
			~LevelManager();

			void openLevel(std::string filename);
			void saveLevel(std::string filename);

			void cleanObjs();

			std::list<Objects::SpaceItem *>& objs;
		protected:
			LevelWriter writer;
			std::list<Objects::SpaceItem *> _objs;

			std::string levelName;
			std::string creator;

			Misc::Point size, speed, border;
	};
};

#endif
