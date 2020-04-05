<?php
include "adminlogin.php";
include "connect.php";

$sname = mysql_real_escape_string($_POST['sname']);
mysql_query("INSERT INTO sessions VALUES('','" . $sname . "',CURRENT_TIMESTAMP)");

mysql_close();

@header("Location: admin.php");
?>
