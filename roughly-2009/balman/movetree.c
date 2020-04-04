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

void movetree(ubyte *nums, int move, int pots, ubyte *cScore, ubyte *hScore, bool c, bool *cleared)
{
  int i;
  ubyte moveNum = nums[move];
  nums[move] = 0;
  *cleared = true;
  for(i = move + 1; i < moveNum + move + 1; i++)
  {
    if(nums[i % (pots * 2)]++ == 1)
    {
      // CHECK WIN
      if(c == true)
      {
        if(i % (pots * 2) >= pots)
        {
          nums[i % (pots * 2)] = 0;
          *cScore += 2;
        }
      }
      else
      {
        if(i % (pots * 2) < pots)
        {
          nums[i % (pots * 2)] = 0;
          *hScore += 2;
        }
      }
    }
  }
  // CHECK FOR BOARD CLEAR
  for(i = 0; i < pots; i++)
  {
    if(nums[i + (c == true ? 0 : pots)] != 0)
      *cleared = false;
  }
  if(*cleared == true)
  {
    for(i = 0; i < pots * 2; i++)
    {
      if(c == true)
        *cScore += nums[i];
      else
        *hScore += nums[i];
      nums[i] = 0;
    }
  }
}


bool checkmove(ubyte *nums, bool c, int pots)
{
  int i;
  bool go = false;
  for(i = (c == false ? pots : 0); i < (c == false ? pots * 2 : pots); i++)
  {
    if(nums[i] != 0)
    {
      go = true;
    }
  }
  return go;
}

void printtree(ubyte *nums, int pots, ubyte cScore, ubyte hScore)
{
  int i;
  printf("\033[2J");
  printf("\033[0;0f");
  SET_COLOUR_D();
  SET_COLOUR_RESET();
  
  printf(DOUBLE_BAR_T_L);
  for(i = 0;i < pots - 1;i++)
  {
    printf(DOUBLE_BAR_T DOUBLE_BAR_T DOUBLE_BAR_T DOUBLE_BAR_TsD);
  }
  printf(DOUBLE_BAR_T DOUBLE_BAR_T DOUBLE_BAR_T DOUBLE_BAR_T_R "\n" DOUBLE_BAR_L);
  for(i = 0;i < pots - 1;i++)
  {
    SET_COLOUR_D();
    printf(" %-2d", nums[pots - i - 1]);
    SET_COLOUR_RESET();
    printf(BAR_V);
  }
  
  SET_COLOUR_D();
  printf(" %-2d", nums[0]);
  SET_COLOUR_RESET();
  printf(DOUBLE_BAR_L _("CPU:   %-2d") "\n" DOUBLE_BAR_LdR, (int) cScore);
  
  for(i = 0;i < pots - 1;i++)
  {
    printf(DOUBLE_BAR_T DOUBLE_BAR_T DOUBLE_BAR_T DOUBLE_C_SINGLE);
  }
  printf(DOUBLE_BAR_T DOUBLE_BAR_T DOUBLE_BAR_T DOUBLE_BAR_RdL "\n" DOUBLE_BAR_L);
  for(i = 0;i < pots - 1;i++)
  {
    SET_COLOUR_D();
    printf(" %-2d",nums[pots + i]);
    SET_COLOUR_RESET();
    printf(BAR_V);
  }
  
  SET_COLOUR_D();
  printf(" %-2d",nums[pots + i]);
  SET_COLOUR_RESET();
  printf(DOUBLE_BAR_L _("Human: %d") "\n" DOUBLE_BAR_B_L, (int) hScore);
  for(i = 0;i < pots - 1;i++)
  {
    printf(DOUBLE_BAR_T DOUBLE_BAR_T DOUBLE_BAR_T DOUBLE_BAR_BsU);
  }
  printf(DOUBLE_BAR_T DOUBLE_BAR_T DOUBLE_BAR_T DOUBLE_BAR_B_R);
  
  printf("\n");
  SET_COLOUR_RESET();
}


