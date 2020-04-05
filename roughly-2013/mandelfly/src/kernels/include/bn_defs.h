// This is the header for the OpenCL bignum library.
// This file shouldn't be included directly - it defines
// The implementations for each specific bignum using macros.

// The following must be defined before inclusion:

// N - the width of the bignum (1, 2, 4, 8 etc.)
// H - N / 2 (not needed when N=1)
// D - N * 2

// If not included by bignum.cl, do nothing
#ifdef BIGNUM_H

// Variable naming system
// This macro defines the name of a function using the current
// bignum width - think templates.
// eg. N=4
// NAME(bn_add) -> bn_add_4
// HNAME(bn_add) -> bn_add_2
// DNAME(bn_add) -> bn_add_8
#define NAME_HELP1(_x, _num) _x##_##_num
#define NAME_HELP2(_x, _num) NAME_HELP1(_x, _num)
#define NAME(_x) NAME_HELP2(_x, N)
#define HNAME(_x) NAME_HELP2(_x, H)
#define DNAME(_x) NAME_HELP2(_x, D)

//#define VEC_HELP1(_x, _num) _x##_num
//#define VEC_HELP2(_x, _num) VEC_HELP1(_x, _num)
//#if (N == 1)
//#define VEC(_x) struct { _x x; }
//#else
//#define VEC(_x) VEC_HELP2(_x, N)
//#endif

// Current BN size is BN_N, half is BN_H, double is BN_D
#define BNdata_N NAME(BNdata)
#define BNdata_H HNAME(BNdata)
#define BNdata_D DNAME(BNdata)

#define BN_N NAME(BN)
#define BN_H HNAME(BN)
#define BN_D DNAME(BN)

typedef struct {
	union {
		T s[N];
#if N != 1
		// Access to high and low
		struct {
			BNdata_H high;
			BNdata_H low;
		};

		// Access to result of multiplication -
		// half of the digits, missing the first one.
		struct {
			T _ignore;
			BNdata_H mul_result;
		};
#endif
	};
} NAME(BNdata);

typedef struct {
	char negative;
	union {
		T s[N];
		BNdata_N data;
	};
} NAME(BN);

#undef NAME_HELP1
#undef NAME_HELP2
#undef NAME
#undef HNAME
#undef DNAME

#undef VEC_HELP1
#undef VEC_HELP2
#undef VEC

#undef BN_N
#undef BN_H
#undef BN_D

#endif // ifdef BIGNUM_H
