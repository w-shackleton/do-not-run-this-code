<?php
include "scripts/serverlogin.php";

if($num==1){
$message = $_POST['message'];
$message = mysql_real_escape_string($message);
$query="UPDATE message SET message='$message'";
mysql_query($query);
}
include "scripts/serverloginend.php";
header('Location: index.php');
?>
