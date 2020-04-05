<?php
include "scripts/serverlogin.php";

if($num==1){
if($paused==1){
die("P");
}
$val=$_GET['val'];
$val = mysql_real_escape_string($val);
$query="UPDATE users SET bottleval='$val' WHERE id='$uid'";
mysql_query($query);


echo "D";
}

else{
echo "N";
}
include "scripts/serverloginend.php";
?>
