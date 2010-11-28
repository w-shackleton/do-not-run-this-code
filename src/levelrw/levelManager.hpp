#ifndef LEVELMANAGER_H
#define LEVELMANAGER_H

#include <list>
#include "spaceItems.hpp"

#include "levelrw.hpp"

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
	};
};

#endif
