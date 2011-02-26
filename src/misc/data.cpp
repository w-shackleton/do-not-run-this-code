#include "data.hpp"

#include <fstream>
#include <vector>

#include <wx/stdpaths.h>
#include <wx/textfile.h>
#include <wx/tokenzr.h>

using namespace std;

std::string Misc::Data::datadir = "";
std::string Misc::Data::saveLocation = "";
std::string Misc::Data::confLocation = "";

#define CONF_FILE "spacegame.conf"

bool Misc::Data::initialise()
{
	std::vector<std::string> datadirs;
	string testFile = "icon.xpm";

	// TODO: make this code better, to use app directory rather than cwd

	datadirs.push_back(PREFIX "/share/" PACKAGE_NAME);
	datadirs.push_back("data");
	datadirs.push_back("../data");
	datadirs.push_back(".");

	//string dataDir;

	for(int i = 0; i < datadirs.size(); i++)
	{
		ifstream tstream((datadirs[i] + "/" + testFile).c_str());
		if(tstream)
		{
			cout << "Found data folder @ \"" << datadirs[i] << "\"" << endl;
			datadir = datadirs[i];
		}
	}
	if(datadir == "")
	{
		cout << "ERROR: Could not find data!" << endl;
		return false;
	}

	confLocation = wxStandardPaths::Get().GetUserDataDir().mb_str();
	wxMkdir(wxString(confLocation.c_str(), wxConvUTF8));

	wxTextFile config(wxString(confLocation.c_str(), wxConvUTF8) + wxT("/") + wxT(CONF_FILE));
	if(config.Open())
	{ // Process config file
		for(wxString line = config.GetFirstLine(); !config.Eof(); line = config.GetNextLine())
		{
			wxStringTokenizer tkz(line, wxT(";"));
			if(!tkz.HasMoreTokens()) continue; // Malformed line
			wxString key = tkz.GetNextToken();

			if(!tkz.HasMoreTokens()) continue; // Malformed line
			wxString value = tkz.GetNextToken();

			if(key.Cmp(wxT("savelocation")) == 0)
			{
				saveLocation = string(value.mb_str());
			}
		}
		config.Close();
	}
	else
	{
		saveLocation = string(wxStandardPaths::Get().GetDocumentsDir().mb_str()) + "/Space Hopper Levels";
		cout << "Save location set to " << saveLocation << endl;
		config.Create();
		cout << "Created new config file." << endl;
		savePreferences();
	}

	return true;
}

string Misc::Data::getFilePath(string file)
{
	return datadir + "/" + file;
}

//wxString Misc::Data::getFilePath(wxString file)
//{
	//return wxString(getFilePath(file.c_str()), wxConvUTF8);
//}

void Misc::Data::savePreferences()
{
	wxTextFile config(wxString(confLocation.c_str(), wxConvUTF8) + wxT("/") + wxT(CONF_FILE));
	if(config.Open())
	{
		config.Clear();
		config.AddLine(wxT("savelocation;") + wxString(saveLocation.c_str(), wxConvUTF8));

		config.Write();

		wxMkdir(wxString(saveLocation.c_str(), wxConvUTF8));
	}
	else
		cout << "ERROR: Couldn't save configuration to file!" << endl;
}

string Misc::stringToUpper(string strToConvert)
{
	for(unsigned int i=0;i<strToConvert.length();i++)
	{
		strToConvert[i] = toupper(strToConvert[i]);
	}
	return strToConvert;
}

string Misc::stringToLower(string strToConvert)
{
	for(unsigned int i=0;i<strToConvert.length();i++)
	{
		strToConvert[i] = tolower(strToConvert[i]);
	}
	return strToConvert;
}

