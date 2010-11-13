#include "matrixHelper.hpp"

#include <iostream>
using namespace std;

MatrixHelper::MatrixHelper() :
	sx(1),
	sy(1),
	tx(0),
	ty(0)
{
}

Cairo::Matrix& MatrixHelper::get_matrix()
{
	matrix = Cairo::identity_matrix();
	matrix.translate(tx, ty);
	matrix.scale(sx, sy);
	return matrix;
}

Cairo::Matrix& MatrixHelper::get_inverse_matrix()
{
	invmatrix = get_matrix();
	invmatrix.invert();
	return invmatrix;
}

void MatrixHelper::reset()
{
	sx = 1;
	sy = 1;
	tx = 0;
	ty = 0;
}

void MatrixHelper::transform(double x, double y)
{
	tx += x;
	ty += y;
}

void MatrixHelper::transform(wxPoint p)
{
	tx += p.x;
	ty += p.y;
}

void MatrixHelper::scale(double s)
{
	sx *= s;
	sy *= s;
}

void MatrixHelper::scale_rotation(int r)
{
	if(r < 0)
		scale(1.1);
	else if(r > 0)
		scale(1 / 1.1);
}

MatrixHelper::operator Cairo::Matrix&()
{
	return get_matrix();
}
