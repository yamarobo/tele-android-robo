use strict;
use warnings;

my $findwlan = 0;
my $findeth = 0;
my $findip = 0;

open(my $fh , "ifconfig |");
while (my $line = readline $fh){
	if($findwlan){
		if ($line =~ /([0-9]+).([0-9]+).([0-9]+).([0-9]+)/){
			my $str = "$1_$2_$3_$4_";
		  	print $str;
		  	print "\n";
		  	my $files = "";
		  	foreach my $ch (split //, $str) {		  		
		  		if($ch =~ /[0-9]/){
		  			$files = $files." $ch.wav";
		  		}
		  		else{
				    system("aplay".$files);
				    sleep(1);
				    $files = "";
		  		}
		  	}
		  	$findip = 1;
			last if (1);
		}

		else{
			$findwlan = 0;
		}

	}
	if($findeth ){

		if ($line =~ /([0-9]+).([0-9]+).([0-9]+).([0-9]+)/){
			my $str = "$1_$2_$3_$4_";
		  	print $str;
		  	print "\n";
		  	my $files = "";
		  	foreach my $ch (split //, $str) {		  		
		  		if($ch =~ /[0-9]/){
		  			$files = $files." $ch.wav";
		  		}
		  		else{
				    system("aplay".$files);
				    sleep(1);
				    $files = "";
		  		}
		  	}
	  		    system("aplay eth.wav");
		  	$findip = 1;
			last if (1);
		}
		else{
			$findeth = 0;
		}

	}
	my $pos;
	$pos = index($line, "wlan");
	if($pos >= 0){
		$findwlan = 1;
	}	
	my $pos2;
	$pos2 = index($line, "eth");
	if($pos2 >= 0){
		$findeth = 1;
	}
	$pos2 = index($line, "enp");
	if($pos2 >= 0){
		$findeth = 1;
	}
}
close $fh;
if($findip == 0){
	   system("aplay ip_not_connected.wav");
}


