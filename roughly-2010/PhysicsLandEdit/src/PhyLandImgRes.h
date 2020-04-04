#ifndef PHYLANDIMGRES_H
#define PHYLANDIMGRES_H

#include <wx/list.h>
#include <wx/string.h>
#include <wx/image.h>
#include <wx/dir.h>


class PhyLandImgRes
{
public:
	PhyLandImgRes(wxString fileN);
	
	wxString fileName;
	wxImage image;
};

WX_DECLARE_LIST(PhyLandImgRes, PhyLandImgResc);

void fillImgResc(PhyLandImgResc* resc);

#endif
