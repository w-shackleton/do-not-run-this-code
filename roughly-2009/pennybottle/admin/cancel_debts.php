<?php
include "scripts/serverlogin.php";

if($num==1){

mysql_query("UPDATE borrows SET status='2' WHERE status='0'");

include "scripts/serverloginend.php";
header('Location: index.php');
}
include "scripts/serverloginend.php";
?>