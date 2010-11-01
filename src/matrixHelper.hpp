#ifndef MATRIXHELPER_H
#define MATRIXHELPER_H

#include <cairomm/matrix.h>
#include <wx/gdicmn.h>

class MatrixHelper : public Cairo::Matrix
{
	public:
		MatrixHelper();
		Cairo::Matrix& get_matrix();
		Cairo::Matrix& get_inverse_matrix();
		void reset();
		void transform(double x, double y);
		void transform(wxPoint p);
		void scale(double s);

		operator Cairo::Matrix&();
	protected:
		double tx, ty;
		double sx, sy;

		Cairo::Matrix matrix, invmatrix;
};

#endif
