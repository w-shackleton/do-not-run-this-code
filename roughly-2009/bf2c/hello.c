#include <stdio.h>

main()
{
 int a[10000] , *p, i ;
 for (i = 0; i < 10000 ; i++ )
    a[i] = 0 ;

 p = a ;

 p[1] += 9;
 p[0] += 8 * p[1] ; p[1] = 0 ;
 fputc( p[0], stdout ) ; fflush( stdout ) ;
 p[1] += 7;
 p[0] += 4 * p[1] ; p[1] = 0 ;
 p[0]++;
 fputc( p[0], stdout ) ; fflush( stdout ) ;
 p[0] += 7;
 fputc( p[0], stdout ) ; fflush( stdout ) ;
 fputc( p[0], stdout ) ; fflush( stdout ) ;
 p[0] += 3;
 fputc( p[0], stdout ) ; fflush( stdout ) ;
 p[3] += 8;
 p[2] += 4 * p[3] ; p[3] = 0 ;
 fputc( p[2], stdout ) ; fflush( stdout ) ;
 p[5] += 10;
 p[4] += 9 * p[5] ; p[5] = 0 ;
 p[4] -= 3;
 fputc( p[4], stdout ) ; fflush( stdout ) ;
 fputc( p[0], stdout ) ; fflush( stdout ) ;
 p[0] += 3;
 fputc( p[0], stdout ) ; fflush( stdout ) ;
 p[0] -= 6;
 fputc( p[0], stdout ) ; fflush( stdout ) ;
 p[0] -= 8;
 fputc( p[0], stdout ) ; fflush( stdout ) ;
 p[2]++;
 fputc( p[2], stdout ) ; fflush( stdout ) ;
}
