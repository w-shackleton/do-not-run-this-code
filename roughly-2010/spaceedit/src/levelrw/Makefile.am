pkglib_LTLIBRARIES= liblevelrw.la


liblevelrw_la_SOURCES = \
		     levelrw.cpp	levelrw.hpp		\
		     levelManager.cpp	levelManager.hpp

liblevelrw_la_LIBADD = @WX_LIBS@ @tinyxml_LIBS@ @Cairomm_LIBS@ ../objects/libobjects.la ../misc/libmisc.la
liblevelrw_la_CXXFLAGS = @WXCPPFLAGS@ @tinyxml_CFLAGS@ @Cairomm_CFLAGS@ -I../objects -I../misc
liblevelrw_la_LDFLAGS = -no-undefined -version-info 0:0:0
