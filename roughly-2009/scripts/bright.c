#include<stdio.h>

int main(int argc, char **argv) {
	if(argc <= 1) {
		return 0;
	}
	char* bright = argv[1];
	char buf[300];
	int n = snprintf(buf, 300, "sh -c \"echo %s > /sys/class/backlight/acpi_video0/brightness\"", bright);
	if(n < 0 || n > 300) {
		puts("Error formatting string\n");
		return 1;
	}
	system(buf);
	return 0;
}
