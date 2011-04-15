#ifndef LEVELRW_H
#define LEVELRW_H

#include <tinyxml.h>
#include <list>
#include <iostream>

#include "../objects/spaceItem.hpp"
#include "../objects/levelBounds.hpp"
#include "../editorCallbacks.hpp"

#include "../objects/player.hpp"
#include "../objects/portal.hpp"

namespace Levels
{
	class LevelWriter
	{
		public:
			LevelWriter();
			void write(std::string filename, std::list<Objects::SpaceItem *>* objs, std::string levelName, std::string creator,
					const Objects::Player &p,
					const Objects::Portal &portal,
					Objects::LevelBounds &bounds,
					int& numberStars);
		protected:
			void cleanup();
			TiXmlDocument doc;

			TiXmlDeclaration *decl;

			TiXmlElement *level;
			std::list<Objects::SpaceItem *>* objs;
	};

	class LevelReader
	{
		public:
			LevelReader();
			bool open(const std::string &filename, std::list<Objects::SpaceItem *>* objs, std::string &levelName, std::string &levelCreator,
					Objects::Player& p,
					Objects::Portal& portal,
					Objects::LevelBounds &bounds,
					int* numberStars);
			bool open(const std::string &filename, std::string &levelName, std::string &levelCreator);
			void setEditorCallbacks(EditorCallbacks *callbacks);
		protected:
			EditorCallbacks *callbacks;
	};
};

#endif
