<?php
# $user="xzpyjvtr_PB";
# $password="tb68G^&zGbg&^(MN";
# $database="xzpyjvtr_PB";
# $url="localhost";
$url = "";
$database = "penny";
$user = "penny";
$password = ""; # Some defaults

$adminPassword = "";

$fh = fopen("../../../settings.txt", 'r');
while(($line = fgets($fh)) !== false) {
	$line = str_replace("\n", "", $line);
	$vars = explode(":", $line);
	switch($vars[0]) {
		case "mySqlAddress":
			$url = $vars[1];
			break;
		case "mySqlDatabase":
			$database = $vars[1];
		case "mySqlUsername":
			$user = $vars[1];
		case "mySqlPassword":
			$password = $vars[1];
		case "adminEncryptedPassword":
			$adminPassword = $vars[1];
	}
}

?>
