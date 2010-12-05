#ifndef LEVELRW_H
#define LEVELRW_H

#include <tinyxml.h>
#include <list>
#include <iostream>

#include "../objects/spaceItems.hpp"

namespace Levels
{
	class LevelWriter
	{
		public:
			LevelWriter();
			void write(std::string filename, std::list<Objects::SpaceItem *>* objs, std::string levelName, std::string creator,
					double px, double py,
					double ssx, double ssy,
					double bx, double by);
		protected:
			void cleanup();
			TiXmlDocument doc;

			TiXmlDeclaration *decl;

			TiXmlElement *level;
			std::list<Objects::SpaceItem *>* objs;
	};
};

#endif
