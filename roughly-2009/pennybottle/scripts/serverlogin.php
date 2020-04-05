<?php
include "connect.php";
mysql_connect($url,$user,$password);
@mysql_select_db($database) or die( "Unable to select database");

session_start();
$uid=$_SESSION['uid'];
$pass=$_SESSION['passwd'];
#echo "$uid $pass";

$query="SELECT * FROM users WHERE id='$uid' and password='$pass' and disabled='0'";
$result=mysql_query($query);

$num=mysql_numrows($result);
$query="SELECT * FROM paused";
$result=mysql_query($query);
$paused=mysql_result($result,0,"paused");
#echo " $num ";

?>
