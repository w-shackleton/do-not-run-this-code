#!/usr/bin/perl
$|=1;
$count = 0;
$pid = $$;
while (<>) {
        chomp $_;
        if ($_ =~ /(.*\.jpg)/i) {
                $url = $1;
                system("/usr/bin/wget", "-q", "-O","/var/www/images/$pid-$count.jpg", "$url");
                system("/usr/bin/mogrify", "-blur", "4","/var/www/images/$pid-$count.jpg");
                print "http://127.0.0.1/images/$pid-$count.jpg\n";
        }
        elsif ($_ =~ /(.*\.gif)/i) {
                $url = $1;
                system("/usr/bin/wget", "-q", "-O","/var/www/images/$pid-$count.gif", "$url");
                system("/usr/bin/mogrify", "-blur", "4","/var/www/images/$pid-$count.gif");
                print "http://127.0.0.1/images/$pid-$count.gif\n";

        }
        elsif ($_ =~ /(.*\.png)/i) {
                $url = $1;
                system("/usr/bin/wget", "-q", "-O","/var/www/images/$pid-$count.png", "$url");
                system("/usr/bin/mogrify", "-blur", "4","/var/www/images/$pid-$count.png");
                print "http://127.0.0.1/images/$pid-$count.png\n";

        }
        else {
                print "$_\n";;
        }
        $count++;
}
