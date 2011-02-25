#include <openLevelList.hpp>

using namespace std;

OpenLevelList::OpenLevelList()
{
}

void OpenLevelList::openLevel(wxString levelSetName, wxString fileName)
{
	SpaceFrame *sf = new SpaceFrame(*this, levelSetName, string(fileName.mb_str()));
	editors.push_back(sf);
	sf->Show();

	cout << "Level loaded" << endl;
}

void OpenLevelList::removeFromList(SpaceFrame *item)
{
	for(list<SpaceFrame*>::iterator iter = editors.begin(); iter != editors.end(); ++iter)
	{
		cout << "ITEREITEIITEIER: " << *iter << ", " << item << endl;
		if(*iter == item)
		{
			editors.erase(iter);
			break;
		}
	}
}

bool OpenLevelList::isOpen(wxString filename)
{
	for(list<SpaceFrame*>::iterator iter = editors.begin(); iter != editors.end(); iter++)
	{
		cout << "Comparing " << wxString((*iter)->lmanager.levelPath.c_str(), wxConvUTF8).mb_str() << " to " << filename.mb_str() << endl;
		if(wxString((*iter)->lmanager.levelPath.c_str(), wxConvUTF8) == filename)
			return true;
	}
	return false;
}
