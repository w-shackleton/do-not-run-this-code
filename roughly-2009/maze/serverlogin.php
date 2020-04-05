<?php
include "connect.php";
mysql_connect($url,$user,$password);
@mysql_select_db($database) or die( "Unable to select database");
?>
