<?php
include "adminlogin.php";
include "connect.php";
mysql_query("DELETE FROM penny");
mysql_query("DELETE FROM sessions");
mysql_close();
header("Location: admin.php");
?>
