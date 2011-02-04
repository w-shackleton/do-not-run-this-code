<?php

if(!isset($dbneeded)) {
	mysql_close();
	die();
}
if($dbneeded)
	mysql_close();
?>
