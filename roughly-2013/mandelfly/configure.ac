AC_PREREQ([2.64])
AC_INIT([mandelfly], [0.1], [w.shackleton@gmail.com])
AC_CONFIG_SRCDIR([src/main.cpp])
AC_CONFIG_HEADERS([config.h])
AM_INIT_AUTOMAKE

AH_TEMPLATE(VERSION_CODE, "Version code")
AC_DEFINE_UNQUOTED(VERSION_CODE, 1)

AH_TEMPLATE(V_MAJOR, "Major number")
AC_DEFINE_UNQUOTED(V_MAJOR, 0)

AH_TEMPLATE(V_MINOR, "Minor number")
AC_DEFINE_UNQUOTED(V_MINOR, 1)

AH_TEMPLATE(V_REVISION, "Revision")
AC_DEFINE_UNQUOTED(V_REVISION, 0)

# AC_CONFIG_MACRO_DIR([m4])
AC_CANONICAL_HOST

# Checks for programs.
AC_PROG_CXX
AC_PROG_CC
LT_INIT
AM_PROG_CC_C_O

AM_SILENT_RULES([yes])

AC_CONFIG_MACRO_DIR([m4])
AC_CANONICAL_HOST

dnl this macro is used to get the arguments supplied
dnl to the configure script (./configure --enable-debug)
dnl Check if we have enable debug support.
AC_MSG_CHECKING(whether to enable debugging)
debug_default="no"
AC_ARG_ENABLE(debug, [  --enable-debug=[no/yes] turn on debugging
	                             [default=$debug_default]],, enable_debug=$debug_default)

if test "x$enable_debug" = "xyes"; then
	CFLAGS="$CFLAGS -g -O0 -DDEBUG"
	CXXFLAGS="$CXXFLAGS -O0 -g -DDEBUG"
	AC_MSG_RESULT(yes)
else
	CFLAGS="$CFLAGS -O2 -s"
	CXXFLAGS="$CFLAGS -O2 -s"
	AC_MSG_RESULT(no)
fi

PKG_CHECK_MODULES([GTKMM], [gtkmm-3.0 >= 3.0])

AX_PTHREAD
AX_CHECK_CL
if test "X$no_cl" = "Xyes"; then
	AC_MSG_FAILURE([You need OpenCL])
fi

AC_LANG_PUSH([C++])
AC_CHECK_HEADER([gtest/gtest.h], [], [
		  ])
AC_LANG_POP([C++])

AC_CONFIG_FILES([
Makefile
src/Makefile
])
AC_OUTPUT
