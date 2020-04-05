<?php

include "scripts/serverlogin.php";

if($num==1){
echo "logged in as ";
$query="SELECT * FROM users WHERE id='$uid'";
$result=mysql_query($query);
echo mysql_result($result,0,"user");
echo "<br>Bottles: ";
echo mysql_result($result,0,"bottle");
echo " | Reserved: ";
echo mysql_result($result,0,"resbottle");
echo "<br>Money: ";
echo mysql_result($result,0,"money");
echo " pence. | Reserved: ";
echo mysql_result($result,0,"resmoney");
echo " pence.<br><br>Message: <i>";
$query="SELECT * FROM message";
$result=mysql_query($query);
echo mysql_result($result,0,"message");
echo "</i>";
}
else
{

echo "Account Disabled.";
}
include "scripts/serverloginend.php";
?>
