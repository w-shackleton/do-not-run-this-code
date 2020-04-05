<?php
include "scripts/serverlogin.php";

if($num==1){
$val=$_POST['val'];
$query="UPDATE enabled SET e = '$val'";
mysql_query($query);
include "scripts/serverloginend.php";
header('Location: index.php');
}
include "scripts/serverloginend.php";
?>