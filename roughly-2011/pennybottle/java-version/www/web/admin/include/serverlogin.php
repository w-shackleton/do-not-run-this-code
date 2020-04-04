<?php
include "connect.php";
if(!isset($dbneeded)) { # Assume we do need it
	mysql_connect($url,$user,$password);
	@mysql_select_db($database) or die( "Unable to select database");
} else {
	if($dbneeded == true) {
		mysql_connect($url,$user,$password);
		@mysql_select_db($database) or die( "Unable to select database");
	}
}

session_start();

$loggedin = false;
if(!isset($_SESSION['admpasswd'])) { # Not yet logged in
	header("Location: login.php");
	include "footer.php";
	die();
} else {
	$pass=$_SESSION['admpasswd'];
	if($pass == $adminPassword)
		$loggedin = true;
	else {
		header("Location: login.php?e=p");
		include "footer.php";
		die();
	}
}
?>
