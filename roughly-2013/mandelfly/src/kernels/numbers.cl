__kernel void floatTest(__global float *in, __global float *out) {
	size_t ix = get_global_id(0);
	out[ix] = in[ix] + 2;
}

__kernel void float4Test(__global float4 *in, __global float4 *out) {
	size_t ix = get_global_id(0);
	out[ix] = in[ix] * 2;
}
__kernel void float8Test(__global float8 *in, __global float8 *out) {
	size_t ix = get_global_id(0);
	out[ix] = in[ix] * 2;
}
__kernel void float16Test(__global float16 *in, __global float16 *out) {
	size_t ix = get_global_id(0);
	out[ix] = in[ix] * 2;
}
