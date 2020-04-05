<?php
include "scripts/serverlogin.php";

if($num==1){
$query="SELECT * FROM paused";
$result=mysql_query($query);
$ispaused=mysql_result($result,0,"paused");
if($ispaused==1){
$query="UPDATE paused SET paused='0'";
mysql_query($query);
}
else{
$query="UPDATE paused SET paused='1'";
mysql_query($query);
}
}
header('Location: index.php');
include "scripts/serverloginend.php";
?>
