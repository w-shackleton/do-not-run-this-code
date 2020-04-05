#include "mem.hpp"

#include <iostream>

using namespace mandel::mem;
using namespace std;

void mandel::mem::freeCMem(cl_mem memobj, void *userData) {
	cout << "Freeing memory" << endl;
	free(userData);
}
