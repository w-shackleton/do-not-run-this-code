<?php
include "connect.php";
mysql_connect($url,$user,$password);
@mysql_select_db($database) or die( "Unable to select database");

session_start();
$pass=$_SESSION['admpasswd'];
#echo "$uid $pass";
$usr = stripslashes($usr);
$usr = mysql_real_escape_string($usr);

$query="SELECT * FROM admin WHERE password='$pass'";
$result=mysql_query($query);

$num=mysql_numrows($result);
#echo " $num ";

?>
