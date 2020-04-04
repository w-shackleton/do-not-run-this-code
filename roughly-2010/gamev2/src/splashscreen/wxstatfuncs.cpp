#include "wxstatfuncs.hpp"

#include <wx/dir.h>
#include <wx/stdpaths.h>

#include <iostream>
using namespace std;

// Possible search locations...
#define DATA "data"
#define d1DATA "../data"
#define d2DATA "../../data"
#define d3DATA "../../../data"

string statfuncs::locateGameResource(string file)
{
	return locateGameDataDir() + "/" + file;
}

string statfuncs::locateGameDataDir()
{
	wxStandardPaths wxSP;
	#ifndef __WXMSW__
	// Only run on non unix machines
	wxSP.SetInstallPrefix(wxT(PREFIX));
	#endif
	if(wxDir::Exists(wxSP.GetDataDir() + wxT("/data")))
		return string(wxSP.GetDataDir().mb_str()) + "/data";
	if(wxDir::Exists(wxT(DATA)))
		return DATA;
	if(wxDir::Exists(wxT(d1DATA)))
		return d1DATA;
	if(wxDir::Exists(wxT(d2DATA)))
		return d2DATA;
	if(wxDir::Exists(wxT(d3DATA)))
		return d3DATA;
	cout << "** Could not find data directory **" << endl;
	return "";
}

