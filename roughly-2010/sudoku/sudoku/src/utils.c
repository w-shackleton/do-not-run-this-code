#include "utils.h"

#include <stdio.h>
#include <string.h>

int primes[] = {2, 3, 5, 7, 11, 13, 17, 19, 23};

void skipgarb()
{
	while(getchar() != '\n') { }
}

void getData(unsigned short int data[9][9])
{
	int i, j;
	char number[2];
	char line[10];
	
	printf("Data:\n");
	for(i = 0; i < 9; i++)
	{
		memset(line, 0, sizeof(line));
		if(!scanf("%9s", line))
			sscanf("000000000", "%9s", line);
		skipgarb();
		
		for(j = 0; j < 9; j++)
		{
			data[i][j] = atoi(memcpy(number, &(line[j]), 1));
			printf("%u ", data[i][j]);
		}
		printf("\n");
	}
	printf("\n");
}

void computeRCB(unsigned int r[9], unsigned int c[9], unsigned int b[9], unsigned int dataC[9][9])
{
	unsigned short int x, y;
	
	for(x = 0; x < 9; x++)
	{
		for(y = 0; y < 9; y++)
		{
			r[y] *= dataC[x][y];
			c[x] *= dataC[x][y];
			b[3*(int)(y/3) + (int)(x/3)] *= dataC[x][y];
			//printf("%d ", 3*(int)(y/3) + (int)(x/3));
		}
		//printf("\n");
	}
}

void convertGrid(unsigned int dataC[9][9], unsigned short int data[9][9])
{
	unsigned short int x, y;
	printf("Data after priming:\n");
	for(x = 0; x < 9; x++)
	{
		for(y = 0; y < 9; y++)
		{
			dataC[x][y] = primes[data[x][y] - 1];
			printf("%3u", dataC[x][y]);
		}
		printf("\n");
	}
}

void algorithm1(unsigned int dataC[9][9], unsigned int r[9], unsigned int c[9], unsigned int b[9])
{
	unsigned short int x, y, i;
	unsigned int possible;
	
	printf("Data after Alg1:\n");
	for(x = 0; x < 9; x++)
	{
		for(y = 0; y < 9; y++)
		{
			if(!IS_PRIME(dataC[x][y]))
			{
				printf("p");
				possible = TOTAL_NUMS;
				for(i = 0; i < 9; i++)
				{
					/*if(IS_PRIME(r[i]) && (possible % r[i]) == 0)
						possible /= r[i];
					if(IS_PRIME(c[i]) && (possible % c[i]) == 0)
						possible /= c[i];
					if(IS_PRIME(b[i]) && (possible % b[i]) == 0)
						possible /= b[i];*/
				}
				dataC[x][y] = possible;
			}
			else
				printf(" ");
			printf("%10d ", dataC[x][y]);
		}
		printf("\n");
	}
}
