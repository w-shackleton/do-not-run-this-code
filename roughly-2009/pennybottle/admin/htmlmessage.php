<?php
include "scripts/serverlogin.php";

if($num==1){
$message = $_POST['message'];
$message = mysql_real_escape_string($message);
$message = str_replace("'","\"",$message);
$query="UPDATE htmlmessage SET message='$message'";
mysql_query($query);
}
include "scripts/serverloginend.php";
header('Location: index.php');
?>
