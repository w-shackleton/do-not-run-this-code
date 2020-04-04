#include <stdio.h>


main()
{
 char buffer[10000] ;
 char *cur ;
 int buf_end = 0 ;
 char ch = (char)fgetc( stdin ) ;
 int depth = 1 ;
 int move[100] ;
 int mdepth = 0 ;
  

 printf( "#include <stdio.h>\n"
 "\n"
 "main()\n"
 "{\n"
 " int a[10000] , *p, i ;\n"
 " for (i = 0; i < 10000 ; i++ )\n"
 "    a[i] = 0 ;\n"
 "\n"
 " p = a ;\n"
 "\n" ) ;
 
 move[ mdepth ] = 0 ;
 while ( !feof( stdin ) )
 {
    if ( ch == '+' || ch == '-' ||
 ch == '<' || ch == '>' ||
 ch == '.' || ch == ',' ||
 ch == '[' || ch == ']' )
       buffer[ buf_end++ ] = ch ;
    ch = (char)fgetc( stdin ) ;
 }
 buffer[ buf_end ] = '\0' ;

 cur = buffer ;
     
 
 while ( *cur )
 {
    switch( *cur )
    {
       case '+':
       case '-':
       {
  int val = 0 ;
  for( ; *cur == '+' || *cur == '-'; cur++ )
     val += *cur == '+' ? 1 : -1 ;
  if ( val == 1 )
     printf("%*.*sp[%d]++;\n", depth, depth, "", move[mdepth] ) ; 
  else if ( val == -1 )
     printf("%*.*sp[%d]--;\n", depth, depth, "", move[mdepth] ) ; 
  else if ( val > 0 )
     printf("%*.*sp[%d] += %d;\n", depth, depth, "",
    move[mdepth], val) ; 
  else if ( val < 0 )
     printf("%*.*sp[%d] -= %d;\n", depth, depth, "",
    move[mdepth], -val) ; 
       }
       break ;
       case '<':
       case '>':
       {
  int val = 0 ;
  for( ; *cur == '<' || *cur == '>'; cur++ )
     val += *cur == '>' ? 1 : -1 ;
  move[mdepth] += val ;
       }
       break ;
       case '.':  
          printf("%*.*sfputc( p[%d], stdout ) ; fflush( stdout ) ;\n",
 depth, depth, "", move[mdepth] ) ; 
          cur++;
          break ;
       case ',':  
          printf("%*.*sp[%d] = fgetc( stdin ) ;\n",
 depth, depth, "", move[mdepth]) ; 
          cur++;
          break ;
       case '[':
       {
  int s = 1;
  int m = 0;
  int mval = 0;
  int vars[30];
  int added[30];
  int nr_vars = 0;
  int succ = 1;
             
  for ( s = 1; cur[s] == '+' || cur[s] == '-' ||
   cur[s] == '<' || cur[s] == '>' ; s++ )
  {
     if ( cur[s] == '<' )
m-- ;
     else if ( cur[s] == '>' )
m++ ;
     else if ( m == 0 )
mval += cur[s] == '+' ? 1 : -1 ;
     else
     {
int i ;
for ( i = 0; i < nr_vars && m != vars[ i ]; i++ ) ;
if ( i == nr_vars )
{
   vars[i] = m ;
   added[i] = 0 ;
   nr_vars++ ;
}
added[i] += cur[s] == '+' ? 1 : -1 ;
     }
  }
  if ( cur[s] == ']' && m == 0 && mval == -1 )
  {
     int i ;
     printf("%*.*s", depth, depth, "" ) ;
     for ( i = 0; i < nr_vars; i++ )
if ( added[i] == 1 )
   printf("p[%d] += p[%d] ; ", 
  move[mdepth] + vars[i], move[mdepth] );
else if ( added[i] == -1 )
   printf("p[%d] -= p[%d] ; ", 
  move[mdepth] + vars[i], move[mdepth] );
else if ( added[i] > 0 )
   printf("p[%d] += %d * p[%d] ; ", 
  move[mdepth] + vars[i], added[i], move[mdepth] );
else if ( added[i] < 0 )
   printf("p[%d] -= %d * p[%d] ; ",
  move[mdepth] + vars[i], -added[i], move[mdepth] );
     printf("p[%d] = 0 ;\n",  move[mdepth] );
  cur += s + 1;
  }
  else
  {
     printf("%*.*swhile( p[%d] ) {\n", depth, depth, "", move[mdepth] ) ;
     depth += 2;
     move[mdepth+1] = move[mdepth] ;
     mdepth++;
     cur++;
  }
       }
       break ;
       case ']':
       {
  int val = move[mdepth] - move[mdepth-1] ;
  if ( val == 1 )
     printf("%*.*sp++;\n", depth, depth, "") ; 
  else if ( val == -1 )
     printf("%*.*sp--;\n", depth, depth, "") ; 
  else if ( val > 0 )
     printf("%*.*sp += %d;\n", depth, depth, "", val) ; 
  else if ( val < 0 )
     printf("%*.*sp -= %d;\n", depth, depth, "", -val) ; 
  depth -= 2;
  mdepth-- ;
  printf("%*.*s}\n", depth, depth, "") ;
  cur++;
  break ;
       }
    }
 }
 printf( "}\n" ) ;
}
