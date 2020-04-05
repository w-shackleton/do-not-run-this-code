
bin_PROGRAMS = mandelfly test native_transform

lib_LTLIBRARIES = libgtest.la

kernels/numbers.h: kernels/numbers.cl
	bin2c -c --name kernel_numbers $< > $@
kernels/mandel_bn.h: kernels/mandel_bn.cl
	bin2c -c --name kernel_mandel_bn $< > $@
kernels/mandel_float.h: kernels/mandel_float.cl
	bin2c -c --name kernel_mandel_float $< > $@
kernels/bn.h: kernels/include/bn.h
	bin2c -c --name kernel_bn $< > $@
kernels/bn_defs.h: kernels/include/bn_defs.h
	bin2c -c --name kernel_bn_defs $< > $@
kernels/bignum.h: kernels/include/bignum.h
	bin2c -c --name kernel_bignum $< > $@
kernels/mandel_bn_impl.h: kernels/include/mandel_bn_impl.h
	bin2c -c --name kernel_mandel_bn_impl $< > $@

BUILT_SOURCES = kernels/numbers.h kernels/mandel_bn.h	\
		kernels/mandel_float.h			\
		kernels/bn.h	kernels/bignum.h kernels/bn_defs.h \
		kernels/mandel_bn_impl.h

CLEANFILES =	kernels/numbers.h \
		kernels/mandel_bn.h \
		kernels/mandel_float.h \
		kernels/bn.h \
		kernels/bignum.h \
		kernels/bn_defs.h \
		kernels/mandel_bn_impl.h

mandelfly_SOURCES = bignum.cpp	bignum.hpp	\
		    main.cpp	clhead.hpp	\
		    tests.cpp	tests.hpp	\
		    cl.cpp	cl.hpp	\
		    gui.cpp	gui.hpp	\
		    clUtil.c	clUtil.h	\
		    2dcanvas.cpp	2dcanvas.hpp	\
		    fractal2d.cpp	fractal2d.hpp	\
		    fractalManager.cpp	fractalManager.hpp	\
		    cl/test.cpp	cl/test.hpp	\
		    cl/load.cpp	cl/load.hpp	\
		    cl/mem.cpp	cl/mem.hpp	\
		    cl/mandel.cpp	cl/mandel.hpp	\
		    kernels/numbers.h		\
		    kernels/mandel_bn.h		\
		    kernels/mandel_float.h	\
		    kernels/bn.h		\
		    kernels/bignum.h		\
		    kernels/bn_defs.h		\
		    kernels/mandel_bn_impl.h

mandelfly_LDADD = @GTKMM_LIBS@ -lOpenCL libgtest.la
mandelfly_CXXFLAGS = @GTKMM_CFLAGS@ -std=c++11

libgtest_la_SOURCES = /usr/src/gtest/src/gtest-all.cc

libgtest_la_CXXFLAGS = -I/usr/src/gtest

test_SOURCES = ../test/test.cpp
test_LDADD = -lOpenCL libgtest.la
test_CXXFLAGS = -std=c++11

native_transform_SOURCES = ../test/native_transform/bn_native_transform.c	\
			   kernels/mandel_bn.cl	\
			   kernels/include/bn.h	\
			   kernels/include/bignum.h	\
			   kernels/include/bn_defs.h	\
			   kernels/include/mandel_bn_impl.h
native_transform_CFLAGS = -Ikernels/include --std=c99 -lm
