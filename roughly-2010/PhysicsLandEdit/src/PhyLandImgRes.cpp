#include "PhyLandImgRes.h"
#include "statfuncs.h"
#include <iostream>
using namespace std;

PhyLandImgRes::PhyLandImgRes(wxString fileN)
{
	fileName = fileN;
	image = wxImage(fileName);
}

#include <wx/listimpl.cpp>
WX_DEFINE_LIST(PhyLandImgResc);

void fillImgResc(PhyLandImgResc* resc)
{
	wxDir dir(getPathData());
	wxString tempString;
	
	dir.GetFirst(&tempString, wxT("*.png"));
	cout << "Resc found: " << tempString.mb_str() << endl;
	PhyLandImgRes* imgRes = new PhyLandImgRes(getPathData() + wxT("/") + tempString);
	resc->Append(imgRes);
	while(dir.GetNext(&tempString))
	{
		cout << "Resc found: " << tempString.mb_str() << endl;
		resc->Append((PhyLandImgRes*)new PhyLandImgRes(getPathData() + wxT("/") + tempString));
	}
}
