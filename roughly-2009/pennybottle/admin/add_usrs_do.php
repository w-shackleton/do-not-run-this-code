<?php
include "scripts/serverlogin.php";

if($num==1){
$tot = $_POST['amt'];
$m = $_POST['money'];
$b = $_POST['bottles'];
for($i=0;$i < $tot;$i++)
{
$name = $_POST["user-" . $i];
$pass = $_POST["password-" . $i];
$pass = md5($pass);
$query = "INSERT INTO users VALUES ('','$name','$pass','$b','','$m','','','')";
mysql_query($query);
}
}
include "scripts/serverloginend.php";
header('Location: user_manage.php');
?>
