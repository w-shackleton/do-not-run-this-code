#include "data.hpp"

#include <fstream>
#include <vector>

using namespace std;

std::string Misc::Data::datadir = "";

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
	return true;
}

string Misc::Data::getFilePath(string file)
{
	return datadir + "/" + file;
}
