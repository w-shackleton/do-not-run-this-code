#include "tests.hpp"

#include "bignum.hpp"

using namespace mandel::bignum;
using namespace std;

TEST(Bignum, Allocation) {
	EXPECT_EQ(1, BN4_4(1).toFloat());
	EXPECT_EQ(2, BN4_4(2).toFloat());
	EXPECT_EQ(-1, BN4_4(-1).toFloat());
	EXPECT_EQ(-2, BN4_4(-2).toFloat());
	EXPECT_EQ(0.1, BN4_4(0.1).toFloat());
	EXPECT_EQ(-0.1, BN4_4(-0.1).toFloat());
	EXPECT_EQ(8, BN4_2(8).toFloat());
	EXPECT_FLOAT_EQ(0.123456789L, BN4_4(0.123456789L).toFloat());
	EXPECT_FLOAT_EQ(1.987e-20, BN4_4(1.987e-20).toFloat());
}

// Tests addition of positive numbers
TEST(Bignum, Addition) {
	EXPECT_EQ(BN4_4(100), BN4_4(40) + BN4_4(60));
	EXPECT_EQ(BN4_4(0.03), BN4_4(0.01) + BN4_4(0.02));
	EXPECT_EQ(BN4_4(4.612e-15), BN4_4(3.9e-15) + BN4_4(7.12e-16));
	EXPECT_EQ(BN4_4(4.612e-13), BN4_4(3.9e-13) + BN4_4(7.12e-14));
	EXPECT_EQ(BN4_4(4.612e-11), BN4_4(3.9e-11) + BN4_4(7.12e-12));
	EXPECT_EQ(BN4_4(4.612e-9), BN4_4(3.9e-9) + BN4_4(7.12e-10));
	EXPECT_EQ(BN4_4(4.612e-7), BN4_4(3.9e-7) + BN4_4(7.12e-8));
	EXPECT_EQ(BN4_4(4.612e-5), BN4_4(3.9e-5) + BN4_4(7.12e-6));
	EXPECT_EQ(BN4_4(4.612e-3), BN4_4(3.9e-3) + BN4_4(7.12e-4));
}

// Tests subtraction resulting in positive
TEST(Bignum, Subtraction) {
	EXPECT_EQ(BN4_4(100), BN4_4(140) - BN4_4(40));
	EXPECT_EQ(BN4_4(0.0123), BN4_4(0.0247) - BN4_4(0.0124));
}

// Tests addition and subtraction of positive and negative numbers - all the rest of the addition logic
TEST(Bignum, AdditionOther) {
	EXPECT_EQ(BN4_4(-100), BN4_4(40) - BN4_4(140));
	EXPECT_EQ(BN4_4(0.0123), BN4_4(0.0247) + BN4_4(-0.0124));
	EXPECT_EQ(BN4_4(4.612e-15), BN4_4(3.9e-15) - BN4_4(-7.12e-16));
	EXPECT_EQ(BN4_4(4.612e-13), BN4_4(3.9e-13) - BN4_4(-7.12e-14));
	EXPECT_EQ(BN4_4(4.612e-11), BN4_4(3.9e-11) - BN4_4(-7.12e-12));

	EXPECT_EQ(BN4_4(-4.612e-15), BN4_4(-3.9e-15) + BN4_4(-7.12e-16));
	EXPECT_EQ(BN4_4(-4.612e-13), BN4_4(-3.9e-13) + BN4_4(-7.12e-14));
	EXPECT_EQ(BN4_4(-4.612e-11), BN4_4(-3.9e-11) + BN4_4(-7.12e-12));

	EXPECT_EQ(BN4_4(-1.56e-9), BN4_4(0.50e-9) - BN4_4(2.06e-9));
	EXPECT_EQ(BN4_4(1.56e-9), BN4_4(2.06e-9) - BN4_4(0.50e-9));
}

// Tests the expanding of Bignums into wider sized ones
TEST(Bignum, Expansion) {
	EXPECT_EQ(BN4_4(4), BN4_2(4).expand());
	EXPECT_EQ(BN4_4(4*pow(2, -32)), BN4_2(4).expand(1));
	EXPECT_EQ(BN4_4(4), BN4_2(4*pow(2, -32)).expand(-1));
	EXPECT_EQ(BN4_4({1, 0, 0, 0}), BN4_2({0, 1}).expand(-1));
	EXPECT_EQ(BN4_4({0, 1, 0, 0}), BN4_2({0, 1}).expand(0));
	EXPECT_EQ(BN4_4({0, 0, 1, 0}), BN4_2({0, 1}).expand(1));
	EXPECT_EQ(BN4_4({0, 0, 0, 1}), BN4_2({0, 1}).expand(2));
}
// Tests the halving of Bignums into two smaller bignums
TEST(Bignum, Halving) {
	EXPECT_EQ(BN4_2(4), BN4_4(4).halvePrecision());
	EXPECT_EQ(BN4_2(4*pow(2, -32)), BN4_4(4*pow(2, -32)).halvePrecision());

	EXPECT_EQ(BN4_2(4*pow(2, -32)), BN4_4(4).halvePrecision(1));
	EXPECT_EQ(BN4_2(4), BN4_4(4*pow(2, -32)).halvePrecision(-1));

	EXPECT_EQ(BN4_2(4*pow(2, -32)), BN4_4(4*pow(2, -32)).getHigh());
	EXPECT_EQ(BN4_2(4*pow(2, -64)), BN4_4(4).getLow());
}

// Tests trivial multiplication
TEST(Bignum, Multiply1) {
	EXPECT_EQ(BN4_2(8*pow(2, -32)), multiplyUp(BN4_1(2), BN4_1(4)));
	EXPECT_EQ(BN4_2(8), multiplyUp(BN4_1(2*pow(2, 18)), BN4_1(4*pow(2, 14))));

	EXPECT_EQ(BN4_1(8), BN4_1(2) * BN4_1(4));
	EXPECT_EQ(BN4_1(-8), BN4_1(-2) * BN4_1(4));
	EXPECT_EQ(BN4_1(24), BN4_1(-6) * BN4_1(-4));
}

// Tests multiplication of BN2
TEST(Bignum, Multiply2) {
	EXPECT_EQ(BN4_4({0, 0, 0, 0}), multiplyUp(BN4_2({0, 0}), BN4_2({0, 0})));
	EXPECT_EQ(BN4_4({0, 1, 0, 0}), multiplyUp(BN4_2({1, 0}), BN4_2({1, 0})));
	EXPECT_EQ(BN4_4({0, 0, 0, 1}), multiplyUp(BN4_2({0, 1}), BN4_2({0, 1})));
	EXPECT_EQ(BN4_4({0, 15, 38, 24}), multiplyUp(BN4_2({3, 4}), BN4_2({5, 6})));
}

// Tests proper multiplication
TEST(Bignum, MultiplyAll) {
	EXPECT_EQ(BN4_4(3.399522102), BN4_4(1.342542) * BN4_4(2.53215326));
	cout << BN4_4(1.342542) * BN4_4(2.53215326) << endl;
	cout << BN4_4(3.399522102) << endl;
}
