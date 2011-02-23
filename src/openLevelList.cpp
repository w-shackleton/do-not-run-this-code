#include <openLevelList.hpp>

using namespace std;

OpenLevelList::OpenLevelList()
{
}

void OpenLevelList::openLevel(wxString levelSetName, wxString fileName)
{
}

void OpenLevelList::removeFromList(SpaceFrame *item)
{
	for(list<SpaceFrame*>::iterator iter = editors.begin(); iter != editors.end(); iter++)
	{
		if(*iter == item)
		{
			editors.erase(iter);
		}
	}
}
