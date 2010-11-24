pkglib_LTLIBRARIES= libobjects.la


libobjects_la_SOURCES = \
		levelWall.cpp	levelWall.hpp	\
		wall.cpp	wall.hpp	\
		infobox.cpp	infobox.hpp	\
		vortex.cpp	vortex.hpp	\
		rectangular.cpp	rectangular.hpp	\
		planet.cpp	planet.hpp	\
		spherical.cpp	spherical.hpp	\
		spaceItem.cpp	spaceItem.hpp

libobjects_la_LIBADD = @Cairomm_LIBS@
libobjects_la_CXXFLAGS = @Cairomm_CFLAGS@
libobjects_la_LDFLAGS      = -no-undefined -version-info 0:0:0
