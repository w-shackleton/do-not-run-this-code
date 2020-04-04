#ifndef UTILS_H
#define UTILS_H

extern int primes[];

void skipgarb();

void getData(unsigned short int data[9][9]);

void computeRCB(unsigned int r[9], unsigned int c[9], unsigned int b[9], unsigned int dataC[9][9]);

void convertGrid(unsigned int dataC[9][9], unsigned short int data[9][9]);

void algorithm1(unsigned int dataC[9][9], unsigned int r[9], unsigned int c[9], unsigned int b[9]);

#define TOTAL_NUMS (2*3*5*7*11*13*17*19*23)

#define COMPUTE_RCB() computeRCB(r, c, b, dataC)

#define IS_PRIME(_n)	(		\
			((_n) == 2 ) ||	\
			((_n) == 3 ) ||	\
			((_n) == 5 ) ||	\
			((_n) == 7 ) ||	\
			((_n) == 11) ||	\
			((_n) == 13) ||	\
			((_n) == 17) ||	\
			((_n) == 19) ||	\
			((_n) == 23)	\
			)

#endif
