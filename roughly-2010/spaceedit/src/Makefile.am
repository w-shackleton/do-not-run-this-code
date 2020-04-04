SUBDIRS = misc objects levelrw
bin_PROGRAMS = spaceedit

spaceedit_SOURCES = \
		levelInfoEditor.cpp	levelInfoEditor.hpp	\
		preferences.cpp		preferences.hpp		\
		matrixHelper.cpp	matrixHelper.hpp	\
		openLevelList.cpp	openLevelList.hpp	\
		editorCallbacks.cpp	editorCallbacks.hpp	\
		newLevelDialog.cpp	newLevelDialog.hpp	\
		spacePanel.cpp		spacePanel.hpp		\
		cairoPanel.cpp		cairoPanel.hpp		\
		spaceFrame.cpp		spaceFrame.hpp		\
		levelSetManager.cpp	levelSetManager.hpp	\
		spaceApp.cpp		spaceApp.hpp

spaceedit_LDADD = @WX_LIBS@ @gdk_LIBS@ @Cairomm_LIBS@ @tinyxml_LIBS@ objects/libobjects.la misc/libmisc.la levelrw/liblevelrw.la
spaceedit_CXXFLAGS = @WXCPPFLAGS@ @gdk_CFLAGS@ @Cairomm_CFLAGS@ @tinyxml_CFLAGS@ -Iobjects -Imisc

if WIN32

# http://www.gnu.org/software/hello/manual/automake/Options.html
pkg_verbose = $(pkg_verbose_$(V))
pkg_verbose_ = $(pkg_verbose_$(AM_DEFAULT_VERBOSITY))
pkg_verbose_0 = @echo "  CXX    $@";

spaceedit.res: spaceedit.rc
	$(pkg_verbose)i586-mingw32msvc-windres spaceedit.rc -O coff -o spaceedit.res

spaceedit_LDADD += spaceedit.res
spaceedit_LDFLAGS = -Wl,--enable-auto-import
endif

EXTRA_DIST = spaceedit.rc
CLEANFILES = spaceedit.res
