SUBDIRS = misc objects levelrw
bin_PROGRAMS = spaceedit

spaceedit_SOURCES = \
		levelInfoEditor.cpp	levelInfoEditor.hpp	\
		matrixHelper.cpp	matrixHelper.hpp	\
		spacePanel.cpp		spacePanel.hpp		\
		cairoPanel.cpp		cairoPanel.hpp		\
		spaceFrame.cpp		spaceFrame.hpp		\
		spaceApp.cpp		spaceApp.hpp

spaceedit_LDADD = @WX_LIBS@ @Cairomm_LIBS@ @tinyxml_LIBS@ objects/libobjects.la misc/libmisc.la levelrw/liblevelrw.la
spaceedit_CXXFLAGS = @WXCPPFLAGS@ @Cairomm_CFLAGS@ @tinyxml_CFLAGS@ -Iobjects -Imisc

if WIN32

spaceedit.res: spaceedit.rc
	i586-mingw32msvc-windres spaceedit.rc -O coff -o spaceedit.res

spaceedit_LDADD += spaceedit.res
endif

EXTRA_DIST = spaceedit.rc
CLEANFILES = spaceedit.res
