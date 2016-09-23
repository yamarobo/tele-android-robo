use strict;
use warnings;

open(my $fh , "../version.sh |");
while (my $line = readline $fh){
	if ($line =~ /([0-9]+).([0-9]+).([0-9]+)/){
		my $str = "$1_$2_$3";
	  	print $str;
	  	print "\n";
	  	my $files = "";
	  	foreach my $ch (split //, $str) {		  		
	  		if($ch =~ /[0-9]/){
	  			$files = $files." $ch.wav";
	  		}
	  		else{
	  			$files = $files." dot.wav";
	  		}
	  	}
	  	system("aplay".$files);
		last if (1);
	}
}
close $fh;

