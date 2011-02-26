#ifndef SE_DATA_H
#define SE_DATA_H

#include <iostream>
#include "config.h"

//#include <wx/string.h>

#define STD_TO_WX_STRING(_str) wxString(_str.c_str(), wxConvUTF8)

namespace Misc
{
	class Data
	{
		public:
			static bool initialise();
			static std::string datadir;
			static std::string getFilePath(std::string file);
			//static wxString getFilePath(wxString file);

			static std::string saveLocation;
			static void savePreferences();
		protected:
			static std::string confLocation;
		private:
			Data(); // To stop initialising static class

	};

	std::string stringToUpper(std::string strToConvert);
	std::string stringToLower(std::string strToConvert);
}

#endif
