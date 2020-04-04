SUBDIRS = splashscreen engine

bin_PROGRAMS = game

game_SOURCES =				\
	header.h			\
	splashscreen/main.hpp		\
	engine/EngineMain.hpp		\
					\
	main.cpp

game_LDADD = @WX_LIBS@ splashscreen/libsplashscreen.la engine/libengine.la

INCLUDES = @WX_CPPFLAGS@ @IRRLICHT_INCLUDES@
