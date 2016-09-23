use strict;
use warnings;
my $line;
my $fh;
my $timeout = 15;	#sec
my $findIP = 0;
while ($timeout > 0){
	my $findwlan = 0;
	open(my $fh , "ifconfig |");
	while (my $line = readline $fh){
		if($findwlan){
			if ($line =~ /([0-9]+).([0-9]+).([0-9]+).([0-9]+)/){
				my $str = "$1_$2_$3_$4_";
		  		print $str;
		  		print "\n";
		  		$findIP = 1;
		  	}
		}
		
		if($findIP == 1){
			last;
		}
		
		my $pos;
		$pos = index($line, "wlan");
		if($pos >= 0){
			$findwlan = 1;
		  		print "find wlan";
		  		print "\n";
		}
	}
	
	$timeout = $timeout - 1;
	close $fh;
	if($findIP == 1){
		 print "connect\n";
		last;
	}
	sleep(1);
}

if($findIP == 0){
	 print "timeout\n";
}


