pkglib_LTLIBRARIES= libmisc.la


libmisc_la_SOURCES = \
		geometry.cpp	geometry.hpp	\
		data.cpp	data.hpp

#libmisc_la_LIBADD = @WX_LIBS@
#libmisc_la_CXXFLAGS = @WXCPPFLAGS@
libmisc_la_LDFLAGS = -no-undefined -version-info 0:0:0
AM_CPPFLAGS = -DPREFIX='"$(prefix)"'
