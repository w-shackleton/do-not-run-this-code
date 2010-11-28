pkglib_LTLIBRARIES= liblevelrw.la


liblevelrw_la_SOURCES = \
		     levelrw.cpp	levelrw.hpp		\
		     levelManager.cpp	levelManager.hpp

liblevelrw_la_LIBADD = @tinyxml_LIBS@ @Cairomm_LIBS@ ../objects/libobjects.la
liblevelrw_la_CXXFLAGS = @tinyxml_CFLAGS@ @Cairomm_CFLAGS@ -I../objects
liblevelrw_la_LDFLAGS = -no-undefined -version-info 0:0:0
