// #pragma OPENCL EXTENSION cl_khr_fp64: enable

// These files are in 'include', but are copied to a includable location at
// host runtime, CL compile time.
typedef float re;
typedef float im;

typedef float2 complex;

#define C_ADD(_l, _r) ((_l) + (_r))
#define C_MUL(_l, _r) ((complex)(\
				  (_l).x * (_r).x - (_l).y * (_r).y,\
				  (_l).x * (_r).y + (_l).y * (_r).x))
#define C_SQUARE(_z) ((complex)(\
				  (_z).x * (_z).x - (_z).y * (_z).y,\
				  2 * (_z).x * (_z).y))
#define C_LENGTHSQUARED(_z) (_z.x * _z.x + _z.y * _z.y)
				     

// Main kernel
__kernel void mandelFloat(complex centre, complex size, int iterations, __write_only image2d_t image) {
	complex pos = (complex) {
		((re)get_global_id(0) / (re)get_global_size(0) - 0.5) * size.x + centre.x,
		((im)get_global_id(1) / (im)get_global_size(1) - 0.5) * size.y + centre.y
	};
	uint4 colour = (uint4)(0, 0, 0, 255);
	int2 coord = (int2)(get_global_id(0), get_global_id(1));

	complex z = 0;
	while(iterations--) {
		z = C_SQUARE(z);
		z = C_ADD(z, pos);
		if(C_LENGTHSQUARED(z) > 4) { // End loop
			colour.x = iterations * 175 % 225;
			colour.y = iterations * 53 % 225;
			colour.z = iterations * 89 % 225;
			break;
		}
	}

	write_imageui(image, coord, colour);
}
