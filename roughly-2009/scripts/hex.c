#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv)
{
	if(argc != 2)
	{
		printf("No file supplied!\n");
		return 1;
	}
	FILE *fp;
	unsigned char c;
	fp = fopen(argv[1], "r");
	char *tmp;
	tmp = malloc(sizeof(char) * 3);

	while(!feof(fp))
	{
		c = getc(fp);
		printf("\\x%.2X", c);
	}

	free(tmp);
	
	fclose(fp);
}
