// This file uses a series of #defines to transform the OpenCL
// code for BN mandelbrot into native C, to check if it's
// working.

#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <math.h>

// Some defs to make this work
#define uint uint32_t
#define ulong uint64_t
#define bool char
#define false (0)
#define true (1)
#define inline
#define __kernel
#define __write_only
#define image2d_t char*
#define __constant const
#include "bignum.h"

#define BN_SIZE 2
// Taken from mandel_bn_impl.h

// These should be defined before inclusion:
// BN_SIZE - the BN width we are defining for

// MNAME - define the name of a mandelbrot function
#define MNAME_HELP1(_x, _num) _x##_##_num
#define MNAME_HELP2(_x, _num) MNAME_HELP1(_x, _num)
#define MNAME(_x) MNAME_HELP2(_x, BN_SIZE)

#define BN_N MNAME(BN)
#define CPLX MNAME(complex_bn)

#define BN_MAKE(_val) MNAME(bn_make)(_val)
#define BN_VAL(_val) MNAME(bn_val)(_val)
#define BN_ADD(_l, _r) MNAME(bn_add)(_l, _r)
#define BN_SUB(_l, _r) MNAME(bn_sub)(_l, _r)
#define BN_MUL(_l, _r) MNAME(bn_mul)(_l, _r)

typedef struct {
	BN_N re;
	BN_N im;
} CPLX;

inline CPLX complex_add(CPLX l, CPLX r) {
	CPLX result;
	result.re = BN_ADD(l.re, r.re);
	result.im = BN_ADD(l.im, r.im);
	return result;
}

inline CPLX complex_mul(CPLX l, CPLX r) {
	CPLX result;
	result.re = BN_SUB(BN_MUL(l.re, r.re), BN_MUL(l.im, r.im));
	result.im = BN_ADD(BN_MUL(l.re, r.im), BN_MUL(l.im, r.re));
	return result;
}
inline CPLX complex_square(CPLX l) {
	CPLX result;
	result.re = BN_SUB(BN_MUL(l.re, l.re), BN_MUL(l.im, l.im));
	BN_N halfIm = BN_MUL(l.re, l.im);
	result.im = BN_ADD(halfIm, halfIm);
	return result;
}
inline BN_N complex_norm(CPLX l) {
	return BN_ADD(BN_MUL(l.re, l.re), BN_MUL(l.im, l.im));
}

typedef uint32_t DWORD;
typedef uint16_t WORD;
typedef int32_t LONG;
typedef char BYTE;

#pragma pack(push, 1)

// Simple BMP output library
typedef struct tagBITMAPFILEHEADER {
  WORD  bfType;
  DWORD bfSize;
  WORD  bfReserved1;
  WORD  bfReserved2;
  DWORD bfOffBits;
} BITMAPFILEHEADER, *PBITMAPFILEHEADER;
typedef struct tagBITMAPINFOHEADER {
  DWORD biSize;
  LONG  biWidth;
  LONG  biHeight;
  WORD  biPlanes;
  WORD  biBitCount;
  DWORD biCompression;
  DWORD biSizeImage;
  LONG  biXPelsPerMeter;
  LONG  biYPelsPerMeter;
  DWORD biClrUsed;
  DWORD biClrImportant;
} BITMAPINFOHEADER, *PBITMAPINFOHEADER;
typedef struct tagRGBQUAD {
  BYTE rgbBlue;
  BYTE rgbGreen;
  BYTE rgbRed;
  BYTE rgbReserved;
} RGBQUAD;
typedef struct tagBITMAPINFO {
  BITMAPINFOHEADER bmiHeader;
  RGBQUAD          bmiColors[1];
} BITMAPINFO, *PBITMAPINFO;

typedef struct {
	unsigned char b, g, r;
} col;

#pragma pack(pop)
int main(int argc, char **argv) {
	// Begin BMP output for simple testing
	if(argc < 2) {
		fprintf(stderr, "Please give a BMP file name to write to\n");
		exit(1);
	}
	FILE *file = fopen(argv[1], "w");

	int width = 800;
	int height = 600;

	BITMAPFILEHEADER bmpFile;
	bmpFile.bfType = 0x4D42;
	bmpFile.bfSize = sizeof(BITMAPFILEHEADER) + sizeof(BITMAPINFOHEADER) + (width*height*sizeof(col));
	bmpFile.bfOffBits = sizeof(BITMAPFILEHEADER) + sizeof(BITMAPINFOHEADER);
	BITMAPINFOHEADER bmpHeader;
	bmpHeader.biSize = 40;
	bmpHeader.biWidth = width;
	bmpHeader.biHeight = height;
	bmpHeader.biPlanes = 1;
	bmpHeader.biBitCount = 24;
	bmpHeader.biCompression = 0;
	bmpHeader.biSizeImage = width*height*sizeof(col);
	// bmpHeader.biXPels
	bmpHeader.biClrUsed = 0;
	bmpHeader.biClrImportant = 0;

	col *data = calloc(width*height, sizeof(col));

	CPLX size, centre;
	float centreX = 0, centreY = 0, sizeX = 2, sizeY = 2;
	centre.re = BN_MAKE(centreX);
	centre.im = BN_MAKE(centreY);
	size.re = BN_MAKE(sizeX);
	size.im = BN_MAKE(sizeY);

	// Construct data
	for(int x = 0; x < width; x++) {
		for(int y = 0; y < height; y++) {
			int iterations = 50;
			BN_N reFactor = BN_MAKE((float)x / (float)width - 0.5);
			BN_N imFactor = BN_MAKE((float)y / (float)height - 0.5);
			CPLX pos;
			pos.re = BN_ADD(BN_MUL(reFactor, size.re), centre.re);
			pos.im = BN_ADD(BN_MUL(imFactor, size.im), centre.im);
//			printf("Should be %f, is %f\n", ((float)x / (float)width - 0.5) * sizeX + centreX, BN_VAL(pos.re));
//			printf("Should be %f, is %f\n", ((float)y / (float)height - 0.5) * sizeY + centreY, BN_VAL(pos.im));
//			printf("Inputs: %f*%f\n", BN_VAL(reFactor), BN_VAL(size.re));
//			printf("Intermediate: %f\n", BN_VAL(BN_MUL(reFactor, size.re)));

			CPLX z;
			z.re = BN_MAKE(0);
			z.im = BN_MAKE(0);
			while(iterations--) {
				z = complex_square(z);
				z = complex_add(z, pos);
				if(BN_VAL(complex_norm(z)) > 4) { // End loop
					data[y*width+x].r = iterations * 175 % 225;
					data[y*width+x].g = iterations * 53 % 225;
					data[y*width+x].b = iterations * 89 % 225;
					break;
				}
			}
		}
	}

	fwrite(&bmpFile, 1, sizeof(BITMAPFILEHEADER), file);
	fwrite(&bmpHeader, 1, sizeof(BITMAPINFOHEADER), file);
	fwrite(data, width*height, sizeof(col), file);

	fclose(file);
}
