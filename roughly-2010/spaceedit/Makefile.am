AM_MAKEFLAGS = --no-print-directory
SUBDIRS = data src

EXTRA_DIST = configure-mingw

clean-local:
	rm -rf winexport/


DATA_IMAGES = data/icon.xpm		\
	      data/bh.png		\
	      data/bounceicon.png	\
	      data/densityicon.png	\
	      data/message.png		\
	      data/planet1.png		\
	      data/planet2.png		\
	      data/planet3.png		\
	      data/planet4.png		\
	      data/planet5.png		\
	      data/planet6.png		\
	      data/planetbouncy.png	\
	      data/planet-s.png		\
	      data/vortex.png		\
	      data/wall.png		\
	      data/wallside.png

MINGW_FOLDER = /usr/i586-mingw32msvc
MINGW_FILES =	\
		$(MINGW_FOLDER)/bin/freetype6.dll		\
		$(MINGW_FOLDER)/bin/libcairo-2.dll		\
		$(MINGW_FOLDER)/bin/libcairomm-1.0-1.dll	\
		$(MINGW_FOLDER)/bin/libexpat-1.dll		\
		$(MINGW_FOLDER)/bin/libfontconfig-1.dll		\
		$(MINGW_FOLDER)/bin/libpng14-14.dll		\
		$(MINGW_FOLDER)/bin/libsigc-2.0-0.dll		\
		$(MINGW_FOLDER)/lib/libtinyxml.dll		\
		$(MINGW_FOLDER)/bin/mingwm10.dll		\
		$(MINGW_FOLDER)/bin/zlib1.dll			\
								\
		$(MINGW_FOLDER)/lib/wxbase28u_gcc_custom.dll	\
		$(MINGW_FOLDER)/lib/wxbase28u_xml_gcc_custom.dll	\
		$(MINGW_FOLDER)/lib/wxmsw28u_adv_gcc_custom.dll		\
		$(MINGW_FOLDER)/lib/wxmsw28u_core_gcc_custom.dll	\
		$(MINGW_FOLDER)/lib/wxmsw28u_html_gcc_custom.dll	\
		$(MINGW_FOLDER)/lib/wxmsw28u_xrc_gcc_custom.dll

winexport: all
	@echo "--- EXPORTING to 'winexport' for windows install ---"
	test -d "winexport" || mkdir winexport
	test -d "winexport/data" || mkdir winexport/data
	cp src/spaceedit.exe winexport/
	cp $(DATA_IMAGES) winexport/data
	cp $(MINGW_FILES) winexport/
