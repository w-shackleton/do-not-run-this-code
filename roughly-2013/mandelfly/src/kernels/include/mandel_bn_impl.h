// Defines an implementation of mandelbrot, for a certain size bignum
// #define inline
#include "bignum.h"

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

inline CPLX MNAME(complex_add)(CPLX l, CPLX r) {
	CPLX result;
	result.re = BN_ADD(l.re, r.re);
	result.im = BN_ADD(l.im, r.im);
	return result;
}

inline CPLX MNAME(complex_mul)(CPLX l, CPLX r) {
	CPLX result;
	result.re = BN_SUB(BN_MUL(l.re, r.re), BN_MUL(l.im, r.im));
	result.im = BN_ADD(BN_MUL(l.re, r.im), BN_MUL(l.im, r.re));
	return result;
}
inline CPLX MNAME(complex_square)(CPLX l) {
	CPLX result;
	result.re = BN_SUB(BN_MUL(l.re, l.re), BN_MUL(l.im, l.im));
	BN_N halfIm = BN_MUL(l.re, l.im);
	result.im = BN_ADD(halfIm, halfIm);
	return result;
}
inline BN_N MNAME(complex_norm)(CPLX l) {
	return BN_ADD(BN_MUL(l.re, l.re), BN_MUL(l.im, l.im));
}

__kernel void MNAME(mandel_bn)(CPLX centre, CPLX size, int iterations, __write_only image2d_t image) {
	BN_N reFactor = BN_MAKE((float)get_global_id(0) / (float)get_global_size(0) - 0.5);
	BN_N imFactor = BN_MAKE((float)get_global_id(1) / (float)get_global_size(1) - 0.5);
	CPLX pos;
	pos.re = BN_ADD(BN_MUL(reFactor, size.re), centre.re);
	pos.im = BN_ADD(BN_MUL(imFactor, size.im), centre.im);

	uint4 colour = (uint4)(0, 0, 0, 255);
	int2 coord = (int2)(get_global_id(0), get_global_id(1));
	
	write_imageui(image, coord, colour);
	colour = (uint4)(0, 255, 0, 255);

	CPLX z;
	z.re = BN_MAKE(0);
	z.im = BN_MAKE(0);
	while(iterations--) {
		z = MNAME(complex_square)(z);
		z = MNAME(complex_add)(z, pos);
		if(BN_VAL(MNAME(complex_norm)(z)) > 4) { // End loop
			colour.x = iterations * 175 % 225;
			colour.y = iterations * 53 % 225;
			colour.z = iterations * 89 % 225;
			write_imageui(image, coord, colour);
			return;
		}
	}
	
}
