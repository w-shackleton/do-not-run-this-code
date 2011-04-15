pkglib_LTLIBRARIES= libobjects.la


libobjects_la_SOURCES = \
		player.cpp	player.hpp	\
		portal.cpp	portal.hpp	\
		star.cpp	star.hpp	\
		levelBounds.cpp	levelBounds.hpp	\
		wall.cpp	wall.hpp	\
		infobox.cpp	infobox.hpp	\
		vortex.cpp	vortex.hpp	\
		rectangular.cpp	rectangular.hpp	\
		planet.cpp	planet.hpp	\
		blackhole.cpp	blackhole.hpp	\
		spherical.cpp	spherical.hpp	\
		spaceItem.cpp	spaceItem.hpp	\
		spaceItems.hpp			\
		infoboxEditor.cpp	infoboxEditor.hpp	\
		sliderEditor.cpp	sliderEditor.hpp	\
		planetEditor.cpp	planetEditor.hpp

libobjects_la_LIBADD = @WX_LIBS@ @Cairomm_LIBS@ @tinyxml_LIBS@ ../misc/libmisc.la
libobjects_la_CXXFLAGS = @WXCPPFLAGS@ @Cairomm_CFLAGS@ @tinyxml_CFLAGS@
libobjects_la_LDFLAGS      = -no-undefined -version-info 0:0:0
