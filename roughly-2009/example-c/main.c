#include <stdio.h>

int main()
{
	printf("Hello World!\nPlease enter a string: ");
	char str[20];
	scanf("%s", str);
	printf("Your string was %s.\n", str);
	return 0;
}
