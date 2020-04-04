#ifndef PHYLANDJOINTLIST_H
#define PHYLANDJOINTLIST_H

#include <wx/list.h>
#include <wx/gdicmn.h>
#include "PhyLandDList.h"

enum {
PLDJL_Dist,
};

class PhyLandJointListI
{
public:
	PhyLandJointListI(wxPoint p1, PhyLandDListE* ref1);
	
	int jointType;
	bool jointComplete;
	
	wxPoint point1, point2;
	PhyLandDListE *refJ1, *refJ2;
};

WX_DECLARE_LIST(PhyLandJointListI, PhyLandJointList);

PhyLandJointListI* getJointListItem(PhyLandJointList list, int place);

#endif
