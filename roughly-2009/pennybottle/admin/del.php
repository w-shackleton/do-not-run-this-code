<?php
include "scripts/serverlogin.php";

if($num==1){
$delid = $_POST['user'];
if(mysql_result(mysql_query("SELECT * FROM users WHERE id=$delid"),0,disabled) == 1)
{
$query = "UPDATE users SET disabled='0' WHERE id='$delid'";
mysql_query($query);
}
else
{
$query = "UPDATE users SET disabled='1' WHERE id='$delid'";
mysql_query($query);
$query = "UPDATE users SET resbottle='0' WHERE id='$delid'";
mysql_query($query);
$query = "UPDATE users SET resmoney='0' WHERE id='$delid'";
mysql_query($query);
$query = "DELETE FROM trades WHERE sold='0' AND sellid='$delid'";
mysql_query($query);
}
}
include "scripts/serverloginend.php";
header('Location: user_manage.php');
?>
