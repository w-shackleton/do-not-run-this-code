#ifndef SE_DATA_H
#define SE_DATA_H

#include <iostream>
#include "config.h"

namespace Misc
{
	class Data
	{
		public:
			static bool initialise();
			static std::string datadir;
			static std::string getFilePath(std::string file);
		private:
			Data(); // To stop initialising static class
	};
}

#endif
