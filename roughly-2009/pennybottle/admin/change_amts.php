<?php
include "scripts/serverlogin.php";

if($num==1){
$bottle = $_POST['bottle'];
$money = $_POST['money'];
$query = "UPDATE users SET bottle = '$bottle'";
mysql_query($query);
$query = "UPDATE users SET money = '$money'";
mysql_query($query);
header('Location: user_manage.php');
}
include "scripts/serverloginend.php";
?>
