#include "fractalManager.hpp"

using namespace std;
using namespace mandel;
using namespace mandel::comp;

FractalInstances::FractalInstances(Computations &worker) :
	bn1(worker),
	bn2(worker),
	bn4(worker)
{
}

FractalManager::FractalManager(Computations &worker) :
	FractalInstances(worker),
	worker(worker),
	floatFractal(worker),
	activeInstance(INSTANCE_BN2),
	iterations(50),
	generateOnHost(false) // TODO: Change
{
}

/**
 * Computes a section of a fractal.
 * Width and height are in pixels, and will be the size
 * of the resultant image.
 * x and y are the positions within the current bnN.size frame,
 * from 0 to 1.
 * cx and cy are the fractions of the big image (1/tiles)
 */
Cairo::RefPtr<Cairo::ImageSurface> FractalManager::redraw(float x, float y, float cx, float cy, int width, int height) {
	switch(activeInstance) {
		// Adjust aspect ratio to correct proportions.
#define IMPL(TYPE, inst)	\
		{ \
		complex<TYPE> newSize(inst.size.real() * TYPE(cx),	\
				inst.size.imag() * TYPE(cy));		\
		complex<TYPE> newPos(	\
				inst.pos.real() + TYPE(x - 0.5) * inst.size.real(),	\
				inst.pos.imag() + TYPE(y - 0.5) * inst.size.imag());	\
		return inst.generateFractal(newPos, newSize, width, height, generateOnHost, iterations); \
		}
		case INSTANCE_BN1: IMPL(cl_float, bn1);
		case INSTANCE_BN2: IMPL(bignum::BN4_2, bn2);
		case INSTANCE_BN4: IMPL(bignum::BN4_4, bn4);
	}
}

void FractalManager::adjustAspectRatio(int width, int height) {
	switch(activeInstance) {
#undef IMPL
#define IMPL(TYPE, inst)	\
		if(inst.size.imag() > inst.size.real() * TYPE((float)height / (float)width))	\
			inst.size = complex<TYPE>(	\
					inst.size.imag() * TYPE((float)width / (float)height),	\
					inst.size.imag());	\
		else	\
			inst.size = complex<TYPE>(	\
					inst.size.real(),	\
					inst.size.real() * TYPE((float)height / (float)width));	\
		break;
		case INSTANCE_BN1: IMPL(cl_float, bn1);
		case INSTANCE_BN2: IMPL(bignum::BN4_2, bn2);
		case INSTANCE_BN4: IMPL(bignum::BN4_4, bn4);
	}
}

void FractalManager::selectArea(float sizeX, float sizeY, float centreX, float centreY) {
	// TODO: Switch instance when necessary
	printf("Zooming in by (%f,%f)+%f+%f\n", sizeX, sizeY, centreX, centreY);
	switch(activeInstance) {
#undef IMPL
#define IMPL(TYPE, inst)	\
		{	\
		cout << "Old coordinates:" << endl;	\
		cout << "	Centre: " << inst.pos << endl;	\
		cout << "	Size: " << inst.size << endl;	\
		complex<TYPE> oldSize = inst.size, oldPos = inst.pos;	\
		inst.size = complex<TYPE>(oldSize.real() * TYPE(sizeX),	\
				oldSize.imag() * TYPE(sizeY));	\
		inst.pos = complex<TYPE>(	\
				oldPos.real() + TYPE(centreX - 0.5f) * oldSize.real(), \
				oldPos.imag() + TYPE(centreY - 0.5f) * oldSize.imag());\
		cout << "New coordinates:" << endl;	\
		cout << "	Centre: " << inst.pos << endl;	\
		cout << "	Size: " << inst.size << endl;	\
		}	\
		break;

		case INSTANCE_BN1: IMPL(cl_float, bn1);
		case INSTANCE_BN2: IMPL(bignum::BN4_2, bn2);
		case INSTANCE_BN4: IMPL(bignum::BN4_4, bn4);
	}
}

void FractalManager::increasePrecision() {
	activeInstance++;
	if(activeInstance > INSTANCE_BN4)
		activeInstance = INSTANCE_BN4;
	switch(activeInstance) {
		case INSTANCE_BN4:
			bn4.pos = complex<bignum::BN4_4>(
					bn2.pos.real().expand(),
					bn2.pos.imag().expand());
			bn4.size = complex<bignum::BN4_4>(
					bn2.size.real().expand(),
					bn2.size.imag().expand());
			break;
		case INSTANCE_BN2:
			bn2.pos = complex<bignum::BN4_2>(
					bn1.pos.real(),
					bn1.pos.imag());
			bn2.size = complex<bignum::BN4_2>(
					bn1.size.real(),
					bn1.size.imag());
			break;
	}
}
void FractalManager::decreasePrecision() {
	activeInstance--;
	if(activeInstance < INSTANCE_BN1)
		activeInstance = INSTANCE_BN1;
	switch(activeInstance) {
		case INSTANCE_BN2:
			bn2.pos = complex<bignum::BN4_2>(
					bn4.pos.real().halvePrecision(),
					bn4.pos.imag().halvePrecision());
			bn2.size = complex<bignum::BN4_2>(
					bn4.size.real().halvePrecision(),
					bn4.size.imag().halvePrecision());
			break;
		case INSTANCE_BN1:
			bn1.pos = complex<cl_float>(
					bn2.pos.real().toFloat(),
					bn2.pos.imag().toFloat());
			bn1.size = complex<cl_float>(
					bn2.size.real().toFloat(),
					bn2.size.imag().toFloat());
			break;
	}
}
