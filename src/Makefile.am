SUBDIRS = objects
bin_PROGRAMS = spaceedit

spaceedit_SOURCES = \
		matrixHelper.cpp	matrixHelper.hpp	\
		spacePanel.cpp		spacePanel.hpp		\
		cairoPanel.cpp		cairoPanel.hpp		\
		spaceFrame.cpp		spaceFrame.hpp		\
		spaceApp.cpp		spaceApp.hpp
spaceedit_LDADD = @WX_LIBS@ @Cairomm_LIBS@ objects/libobjects.a
spaceedit_CXXFLAGS = @WXCPPFLAGS@ @Cairomm_CFLAGS@
AM_CPPFLAGS = -DPREFIX='"$(prefix)"'
