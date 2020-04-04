#include "sudoku.h"

#include "utils.h"

#include <stdio.h>

int main()
{
	int i;
	unsigned short int data[9][9];
	unsigned int dataC[9][9];
	getData(data);
	
	convertGrid(dataC, data);
	
	unsigned int	r[9],
			c[9],
			b[9];
	for(i = 0; i < 9; i++)
	{
		r[i] = c[i] = b[i] = 1;
	}
	
	COMPUTE_RCB();
	
	/*for(i = 0; i < 9; i++)
	{
		printf("%10u, %10u, %10u\n", r[i], c[i], b[i]);
	}*/
	
	algorithm1(dataC, r, c, b);
	
	return 0;
}
