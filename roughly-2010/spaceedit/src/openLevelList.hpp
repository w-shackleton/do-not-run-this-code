#ifndef OPENLEVELLIST_H
#define OPENLEVELLIST_H

#include <list>
#include "spaceFrame.hpp"

class OpenLevelList
{
	public:
		OpenLevelList();

		void newLevel(wxString levelSetName, wxString fileName);
		void openLevel(wxString levelSetName, wxString fileName);
		void removeFromList(SpaceFrame *item);
		inline bool isEmpty()
		{
			return editors.empty();
		}

		bool isOpen(wxString filename);
	protected:
		std::list<SpaceFrame*> editors;
};

#endif
