
includes = $(wildcard *.h)

runner: y.tab.o lex.yy.o main.o proc.o
	gcc -o runner lex.yy.o y.tab.o main.o proc.o

%.o: %.c $(includes)
	gcc -c $(CFLAGS) $< -o $@

y.tab.c: parser.y
	yacc -d parser.y

lex.yy.c: lexer.l
	flex lexer.l
