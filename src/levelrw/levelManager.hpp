#ifndef LEVELMANAGER_H
#define LEVELMANAGER_H

#include <list>

#include "levelrw.hpp"

#include "geometry.hpp"
#include "data.hpp"

#define LEVEL_MIN 200
#define LEVEL_MAX 2000

class LevelInfoEditor; // Friend

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

			void setEditorCallbacks(EditorCallbacks *callbacks);

			std::list<Objects::SpaceItem *>& objs;

			std::string levelName;
			std::string creator;
		protected:
			friend class LevelInfoEditor;

			LevelWriter writer;
			LevelReader reader;
			std::list<Objects::SpaceItem *> _objs;

			std::string levelPath;

			Misc::Point position, speed, border;

			double borderMin, borderMax;
	};
};

#endif
