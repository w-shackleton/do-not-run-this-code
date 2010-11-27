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
		void transform(wxRealPoint p);
		void scale(double s);
		void scale_rotation(int r);

		operator Cairo::Matrix&();

		double tx, ty;
		double sx, sy;

	protected:
		Cairo::Matrix matrix, invmatrix;
};

#endif
