<?php
include "scripts/serverlogin.php";

if($num==1){
$query = "DELETE FROM trades";
mysql_query($query);
$query = "DELETE FROM users";
mysql_query($query);
$query = "DELETE FROM im";
mysql_query($query);
mysql_query("UPDATE paused SET paused='1'");
mysql_query("UPDATE message SET message='Welcome'");
}
include "scripts/serverloginend.php";
header('Location: user_manage.php');
?>
