#include "statfuncs.h"

wxColour getNextCol(int id)
{
	unsigned int num1 = id % 256;
	unsigned int num2 = (id / 256) % 256;
	unsigned int num3 = (id / 256 / 256) % 256;
	return wxColour(num1, num2, num3);
}

int nextColId(wxColour col)
{
	return  (unsigned int)col.Red() +
		((unsigned int)col.Green() * 256) + 
		((unsigned int)col.Blue() * 256 * 256);
}

int signum(double num)
{
	if(num < 0)
		return -1;
	if(num > 0)
		return 1;
	return 0;
}

int trimMinMax(int i, int min, int max)
{
	if(i < min)
		return min;
	if(i > max)
		return max;
	return i;
}

wxPoint rotatePoint(wxPoint point, float rad)
{
	float dist = sqrt(point.x * point.x + point.y * point.y);
	float initRad = atan2(point.y, point.x);
	
	rad += initRad;
	
	wxPoint newPoint;
	newPoint.x = dist * cos(rad);
	newPoint.y = dist * sin(rad);
	return newPoint;
}

wxString getPathData() // Portability function etc
{
	wxStandardPaths wxSP;
//	wxSP.SetInstallPrefix(wxT(PREFIX));
	if(wxDir::Exists(wxSP.GetDataDir()))
		return wxSP.GetDataDir();
	return wxPathOnly(wxPathOnly(wxSP.GetExecutablePath())) + wxT("/data");
}
