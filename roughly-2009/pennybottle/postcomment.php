<?php
include "scripts/serverlogin.php";

if($num==1){
if($paused==1){
die();
}
$id=$_SESSION['uid'];
$comment=$_POST['message'];

$comment = stripslashes($comment);
$comment = mysql_real_escape_string($comment);

$query="INSERT INTO im VALUES ('','$id','$comment',NOW())";
mysql_query($query);

}
include "scripts/serverloginend.php";
?>
