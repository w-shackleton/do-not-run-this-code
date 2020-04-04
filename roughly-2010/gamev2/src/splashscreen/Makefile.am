lib_LTLIBRARIES = libsplashscreen.la

libsplashscreen_la_SOURCES = \
		../header.h			\
		wxstatfuncs.cpp	wxstatfuncs.hpp	\
		PrefDialog.cpp	PrefDialog.hpp	\
		GameFrame.cpp	GameFrame.hpp	\
		GameApp.cpp	GameApp.hpp	\
		main.cpp	main.hpp
libsplashscreen_la_CPPFLAGS	= @WXCPPFLAGS@ -DPREFIX='"$(prefix)"'
libsplashscreen_la_LDFLAGS	= -no-undefined -version-info 0:0:0
libsplashscreen_la_LIBADD	= @WXLIBS@
