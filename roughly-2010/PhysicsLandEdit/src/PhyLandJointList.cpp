#include "PhyLandJointList.h"
#include "statfuncs.h"
#include <iostream>
using namespace std;

PhyLandJointListI::PhyLandJointListI(wxPoint p1, PhyLandDListE* ref1)
{
	point1 = p1;
	point2 = wxPoint(0, 0);
	
	refJ1 = ref1;
	refJ2 = NULL;
	
	jointType = PLDJL_Dist;
	jointComplete = false;
}

#include <wx/listimpl.cpp>
WX_DEFINE_LIST(PhyLandJointList);

PhyLandJointListI* getJointListItem(PhyLandJointList list, int place)
{
	PhyLandJointList::iterator elem;
	elem = list.begin();
	for(int i = 0; i < place; i++)
		elem++;
	return *elem;
}
