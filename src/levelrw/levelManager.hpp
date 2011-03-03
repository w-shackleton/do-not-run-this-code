#ifndef LEVELMANAGER_H
#define LEVELMANAGER_H

#include <list>
#include <memory>

#include "levelrw.hpp"

#include "geometry.hpp"
#include "data.hpp"

#include "../objects/levelBounds.hpp"

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

			bool openLevel(std::string filename);
			void newLevel(std::string filename = "");
			bool save();

			void change();
			bool levelChanged;
			void cleanObjs();

			void setEditorCallbacks(EditorCallbacks *callbacks);

			std::list<Objects::SpaceItem *>& objs;
			std::auto_ptr<Objects::LevelBounds> levelBounds;

			std::string levelPath;

			std::string levelName;
			std::string creator;
		protected:
			friend class LevelInfoEditor;

			void saveLevel(std::string filename);

			LevelWriter writer;
			LevelReader reader;
			std::list<Objects::SpaceItem *> _objs;

			EditorCallbacks *callbacks;

			Misc::Point position, speed;

			double borderMin, borderMax;
	};
};

#endif
