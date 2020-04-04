/* structs & types for balman  */
#ifndef _BALMANTYPES_H
#define _BALMANTYPES_H


#include "config.h"


#define	TRUE	(0==0)
#define	FALSE	(!TRUE)
#define	true	(0==0)
#define	false	(!true)

#define POTS 2
#define BEADS 2
#define MOVES 10

#define VE if(verbose_mode == true) {
#define VE_END }
#define SKIPGARB() while (getchar() != '\n')

#define SET_COLOUR_E()		printf("%c[%dm", 0x1B, 31)
#define SET_COLOUR_H()		printf("%c[%dm", 0x1B, 36)
#define SET_COLOUR_C()		printf("%c[%dm", 0x1B, 32)
#define SET_COLOUR_D()		printf("%c[%dm", 0x1B, 34)
#define SET_COLOUR_RESET()	printf("%c[%dm", 0x1B, 0)

//for compatibility issues
#ifdef PLATFORM_WIN_32

#define BAR_V		"|"
#define BAR_V_BRANCH	"}"
#define BAR_H		"-"
#define CORNER_T_L	"r"
#define CORNER_T_R	"¬"
#define CORNER_B_L	"L"
#define CORNER_B_R	"|"
#define CORNER_B_R_2	"~"
#define SCORES_L	"r---"
#define SCORES_R	"---¬"
#define CROSS_BAR	"|-~----~"
#define CROSS_BAR2	"-~----~"

#else

#define BAR_V		"│"
#define BAR_V_BRANCH	"├"
#define BAR_H		"─"
#define CORNER_T_L	"┌"
#define CORNER_T_R	"┐"
#define CORNER_B_L	"└"
#define CORNER_B_R	"┘"
#define CORNER_B_R_2	"┶"
#define SCORES_L	"┌───"
#define SCORES_R	"───┐"
#define CROSS_BAR	"│┗┯━━━━┵"
#define CROSS_BAR2	"┗┯━━━━┵"

#define DOUBLE_BAR_T	"═"
#define DOUBLE_BAR_L	"║"
#define DOUBLE_BAR_T_R	"╗"
#define DOUBLE_BAR_B_R	"╝"
#define DOUBLE_BAR_T_L	"╔"
#define DOUBLE_BAR_B_L	"╚"
#define DOUBLE_BAR_TsD	"╤"
#define DOUBLE_BAR_BsU	"╧"
#define DOUBLE_BAR_LdR	"╠"
#define DOUBLE_BAR_RdL	"╣"
#define DOUBLE_C_SINGLE	"╪"
#endif

typedef unsigned char	bool;
typedef char		byte;
typedef unsigned char	ubyte;


#include "maketree.h"
#include "movetree.h"


#endif
