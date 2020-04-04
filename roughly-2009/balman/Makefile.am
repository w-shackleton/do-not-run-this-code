bin_PROGRAMS=balman
balman_SOURCES=balman.c balman.1 system.h aclocal.m4 getopt.c getopt1.c getopt.h maketree.c maketree.h movetree.c movetree.h balmantypes.h

#  uncomment the following if balman requires the math library
#balman_LDADD=-lm

EXTRA_DIST=balman.lsm.in balman.spec.in balman.texinfo

#  if you write a self-test script named `chk', uncomment the
#  following and add `chk' to the EXTRA_DIST list
#TESTS=chk

#  build and install the .info pages
info_TEXINFOS = balman.texinfo
balman_TEXINFOS = gpl.texinfo

#  install the man pages
man_MANS=balman.1
