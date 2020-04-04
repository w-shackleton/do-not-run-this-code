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


int totDepth, pots;
bool verbose_mode;



int maketree(int depth, ubyte move, bool c, ubyte numsPrev[], ubyte cScore, ubyte hScore)
{
  ubyte i, j;
  ubyte nums[pots * 2];
  bool cleared = true;
  int curBDScore, BDScore;
  for(i = 0; i < pots * 2; i++)
  {
    nums[i] = numsPrev[i];
  }
  
  
  
  // INIT
  depth--;
  if(c == true)
    c = false;
  else
    c = true;
  
  if(c == true)
  {
    BDScore = -2000;
    curBDScore = -2000;
  }
  else
  {
    BDScore = 2000;
    curBDScore = 2000;
  }
  // MOVE PIECES
  if(c == false)
  {
    move += pots;
  }
  
  if(nums[move] != 0)
  {
    movetree(nums, move, pots, &cScore, &hScore, c, &cleared);
  }
  else
  {
    //RETURN
VE
    printf(" ");
    for(i = depth + 2;i < totDepth; i++)
    {
      printf(BAR_V " ");
      //printf(" ");
    }
    printf("%d\n",-1000);
VE_END
    return -1000;
  }
  
  // PRINT STATUS
VE
  printf(" ");
  for(i = depth + 2;i < totDepth; i++)
  {
    printf(BAR_V " ");
    //printf(" ");
  }
  //printf(".%d.%d\n",move, depth);
  printf(BAR_V_BRANCH "%-3d   " CORNER_T_L "  ",depth);
  
  for(i = 0;i < pots;i++)
  {
    printf("%-3d",nums[pots * 2 - i - 1]);
  }
  
  printf(CORNER_T_R " %-10s" SCORES_L _("Scores") SCORES_R "\n ",c?_("Computer"):_("Human"));
  for(i = depth + 2;i < totDepth; i++)
  {
    printf(BAR_V" ");
  }
  printf(CROSS_BAR "  ");
  
  for(i = 0;i < pots;i++)
  {
    printf("%-3d",nums[i]);
  }
  
  printf(CORNER_B_R_2 _(" Move: %-3d ") CORNER_B_L _("c:%-3d") BAR_H BAR_H _("%3d:h") CORNER_B_R "\n",move,cScore,hScore);
  
VE_END
  
  //NEXT TREE
  
  
  if(cleared == true)
  {
    curBDScore = cScore - hScore;
    //RETURN
    return (int) curBDScore;
  }
  
  if(depth != 0)
  {
    for(i = 0; i < pots; i++)
    {
      BDScore = (int) maketree(depth, i, c, nums, cScore, hScore);
      if(BDScore != -1000)
      {
        if(c == true)
        {
          if(BDScore >= curBDScore)
          {
            curBDScore = BDScore;
          }
        }
        else
        {
          if(BDScore <= curBDScore)
          {
            curBDScore = BDScore;
          }
        }
      }
    }
  }
  else
  {
    return cScore - hScore;
  }
  return curBDScore;
}    

int makefirsttree(int depth,bool hFirst, int beads, int p, bool verbose, ubyte numsPrev[], ubyte cScore, ubyte hScore, int tempStats[])
{
  ubyte i, *nums;
  bool c;
  int curBD, BD;
  pots = p;
  ubyte branch = 0;
  verbose_mode = verbose;
  
  c = hFirst;
  if(c == true)
  {
    curBD = -20000;
    BD = -20000;
  }
  else
  {
    curBD = 20000;
    BD = 20000;
  }
  totDepth = depth;
  depth--;
  
  nums = numsPrev;
  
VE
  printf("%-3d   " CORNER_T_L "  ",depth);
  for(i = 0;i < pots;i++)
  {
    printf("%-3d",nums[pots * 2 - i - 1]);
  }
  
  printf(CORNER_T_R "\n");
  
  printf(CROSS_BAR2 "  ");
  
  for(i = 0;i < pots;i++)
  {
    printf("%-3d",nums[i]);
  }
  
  printf(CORNER_B_R "\n");
  
VE_END
  
  
  if(c == true)
    c = false;
  else
    c = true;
  
  for(i = 0; i < pots; i++)
  {
    BD = maketree(depth, i, c, nums, cScore, hScore);
    tempStats[i] = (int) BD;
VE
    printf("%d\n",BD);
VE_END

    if(BD != -1000)
    {
      if(c == false)
      {
        if(BD >= curBD)
        {
          curBD = BD;
          branch = (int) i;
        }
      }
      else
      {
        if(BD <= curBD)
        {
          curBD = BD;
          branch = (int) i;
        }
      }
    }
  }
VE
  printf("...%d...%d...",curBD, branch);
VE_END
  return branch;
}

