#ifndef BIGNUM_H
#define BIGNUM_H
// This header defines the bignum system.

#define T uint
#define L ulong

// Should these be macros?
//static __constant int bitsPerValue = sizeof(T) * 8;
//static __constant L maximumValue = 1 << bitsPerValue;
//static __constant L mask = maximumValue - 1;
//static __constant int bitsPerValue = sizeof(T) * 8;
//static __constant L maximumValue = 1 << (sizeof(T) * 8);
//static __constant L mask = 1 << ((sizeof(T) * 8)) - 1;
static __constant int bitsPerValue = 32;
static __constant L maximumValue = 1L << 32;
static __constant L mask = (1L << 32) - 1;

// bn_defs.h first
// BN_1
#define N 1
#define D 2
#include "bn_defs.h"
#undef N
#undef D
// BN_2
#define H 1
#define N 2
#define D 4
#include "bn_defs.h"
#undef H
#undef N
#undef D
// BN_4
#define H 2
#define N 4
#define D 8
#include "bn_defs.h"
#undef H
#undef N
#undef D
// BN_8
#define H 4
#define N 8
#define D 16
#include "bn_defs.h"
#undef H
#undef N
#undef D
// BN_16
#define H 8
#define N 16
#define D 32
#include "bn_defs.h"
#undef H
#undef N
#undef D

// Implementations
// BN_1
#define N 1
#define D 2
#include "bn.h"
#undef N
#undef D
// BN_2
#define H 1
#define N 2
#define D 4
#include "bn.h"
#undef H
#undef N
#undef D
// BN_4
#define H 2
#define N 4
#define D 8
#include "bn.h"
#undef H
#undef N
#undef D
// BN_8 - end case
#define BN_END_CASE
#define H 4
#define N 8
#define D 16
#include "bn.h"
#undef H
#undef N
#undef D

#endif
