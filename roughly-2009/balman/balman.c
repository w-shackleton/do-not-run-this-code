/* 
   balman - Plays bal against a human

   Copyright (C) 2009 William Shackleton

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software Foundation,
   Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.  

#include <termios.h>
#include <grp.h>
#include <pwd.h>
*/

#include <stdio.h>
#include <sys/types.h>
#include <getopt.h>
#include "system.h"

#include "balmantypes.h"

#define EXIT_FAILURE 1


static void usage (int status);

/* The name the program was run with, stripped of any leading path. */
char *program_name;
int pots = POTS, beads = BEADS, moves = MOVES;
bool verbose_mode;
/* getopt_long return codes */
enum {DUMMY_CODE=129
};

/* Option flags and variables */


static struct option const long_options[] =
{
  {"help", no_argument, 0, 'h'},
  {"version", no_argument, 0, 'V'},
  {"beads", required_argument, 0, 'b'},
  {"pots", required_argument, 0, 'p'},
  {"moves", required_argument, 0, 'm'},
  {"verbose", no_argument, 0, 'v'},
  {NULL, 0, NULL, 0}
};

static int decode_switches (int argc, char **argv);

int
main (int argc, char **argv)
{
  int i, j, moveToTake, humChoice;
  ubyte cScore = 0, hScore = 0;
  bool cleared, canContHumLoop = true;

  program_name = argv[0];

  printf("\n");

  i = decode_switches (argc, argv);
  
  printf("Loading...\n\n\n\n");
  ubyte nums[pots * 2];
  int tempStats[pots];
    for(j = 0; j < pots * 2; j++)
  {
    nums[j] = beads;
    tempStats[j % pots] = 0;
  }
  while(true)
  {
    SET_COLOUR_RESET();
    if(checkmove(nums, true, pots) == true)
    {
      moveToTake = makefirsttree(moves, true, beads, pots, verbose_mode, nums, 0, 0, tempStats);
      SET_COLOUR_C();
      movetree(nums, moveToTake, pots, &cScore, &hScore, true, &cleared);
      
      printtree(nums, pots, cScore, hScore);
      printf("  ");
      for(j = pots; j > 0; j--)
      {
        printf("%-3d ",tempStats[j - 1]);
      }
      printf("\n");
      printf("Move Taken: %d\n",moveToTake + 1);
      printf("You Should Take: ");
      printf("%d\n", makefirsttree(moves, false, beads, pots, verbose_mode, nums, 0, 0, tempStats) + 1);
      printf("Human Outcomes (minus good):\n");
      for(j = 0; j < pots; j++)
      {
        printf("%-3d ",tempStats[j]);
      }
      printf("\n\n");
    }
    else
    {
      printf(_("End of Game - Scores:\n")
             _("                     Computer: %d\n")
             _("                     Human   : %d\n"), cScore, hScore);
      exit(0);
    }
    printf("\n");
    do
    {
      SET_COLOUR_H();
      canContHumLoop = true;
      printf(_("Choose Pot (0 for exit): "));
      if(scanf("%d",&humChoice) == 0)
      {
        SET_COLOUR_E();
        printf(_("ERROR: Please enter a number.\n\n"));
        canContHumLoop = false;
      }
      else
      {
        if(humChoice == 0)
        {
          SET_COLOUR_RESET();
          exit(0);
        }
        if((humChoice > 0) && (humChoice <= pots))
        {
          if(nums[humChoice - 1 + pots] == 0)
          {
            SET_COLOUR_E();
            printf(_("ERROR: Pot Empty.\n\n"));
            canContHumLoop = false;
          }
        }
        else
        {
          SET_COLOUR_E();
          printf(_("ERROR: Please enter a number in the range of 1 to %d.\n\n"), pots);
          canContHumLoop = false;
        }
      }
      SKIPGARB();
    }
    while(canContHumLoop == false);
    
    SET_COLOUR_H();
    printf(_("\nMoving piece %d.\n\n"),humChoice);
    
    movetree(nums, humChoice - 1 + pots, pots, &cScore, &hScore, false, &cleared);
    
    printtree(nums, pots, cScore, hScore);
    if(checkmove(nums, false, pots) == false)
    {
      printf(_("End of Game - Scores:\n")
             _("                     Computer: %d\n")
             _("                     Human   : %d\n"), cScore, hScore);
      exit(0);
    }
  }
  
  exit (0);
}




/* Set all the option flags according to the switches specified.
   Return the index of the first non-option argument.  */

static int
decode_switches (int argc, char **argv)
{
  int c;


  while ((c = getopt_long (argc, argv, 
			   "hVb:p:m:v",
			   long_options, (int *) 0)) != EOF)
    {
      switch (c)
	{
	case 'V':
	  printf (_("balman %s\n"), VERSION);
	  exit (0);

	case 'h':
	  usage (0);

	case 'b':
	  if(sscanf(optarg,"%d",&beads) == 0)
	  {
	    printf(PACKAGE _(": Malformed number in -b \"%s\"\n\n"),optarg);
	    usage (EXIT_FAILURE);
	  }
	  break;

	case 'p':
	  if(sscanf(optarg,"%d",&pots) == 0)
	  {
	    printf(PACKAGE _(": Malformed number in -p \"%s\"\n\n"),optarg);
	    usage (EXIT_FAILURE);
	  }
	  break;

	case 'm':
	  if(sscanf(optarg,"%d",&moves) == 0)
	  {
	    printf(PACKAGE _(": Malformed number in -m \"%s\"\n\n"),optarg);
	    usage (EXIT_FAILURE);
	  }
	  break;

	case 'v':
	  verbose_mode = true;
	  break;

	default:
	  usage (EXIT_FAILURE);
	}
    }

  return optind;
}


static void
usage (int status)
{
  printf (_("%s - \
Plays bal against a human\n"), PACKAGE);
  printf (_("Usage: %s [OPTION]...\n"), program_name);
  printf (_("\
Options:\n\
  -h, --help                 display this help and exit\n\
  -V, --version              output version information and exit\n\
  -v, --verbose              be verbose\n\
Game Options:\n\
  -b, --beads  beads         set the amount of beads in each pot    (default %d)\n\
  -p, --pots   pots          set the number of pots each palyer has (default %d)\n\
  -m, --moves  moves         set the number of moves to plan ahead  (default %d)\n\
"), BEADS, POTS, MOVES);
  exit (status);
}
