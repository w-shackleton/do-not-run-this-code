<?php
include "scripts/serverlogin.php";

if($num==1){
$bottles = $_POST['bottle'];
$moneys = $_POST['money'];
$result = mysql_query("SELECT * FROM users");
$number = mysql_num_rows($result);
for($i = 0;$i < $number;$i++)
{
$oldm = mysql_result($result,$i,money);
$oldb = mysql_result($result,$i,bottle);
$newm = $oldm + $moneys;
$newb = $oldb + $bottles;
$currid=mysql_result($result,$i,id);
mysql_query("UPDATE users SET money='$newm' WHERE id='$currid'");
mysql_query("UPDATE users SET bottle='$newb' WHERE id='$currid'");
}
}
include "scripts/serverloginend.php";
header('Location: user_manage.php');
?>
