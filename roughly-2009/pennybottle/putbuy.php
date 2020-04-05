<?php
include "scripts/serverlogin.php";

if($num==1){
if($paused==1){
die("P");
}
$id=$_SESSION['uid'];
$quan=$_POST['quan'];
$val=$_POST['val'];

$val = stripslashes($val);
$val = mysql_real_escape_string($val);
$quan = stripslashes($quan);
$quan = mysql_real_escape_string($quan);

$query="SELECT * FROM users WHERE id='$uid'";
$result=mysql_query($query);
$money=mysql_result($result,0,"money");
$resmoney=mysql_result($result,0,"resmoney");
$avmoney=$money-$resmoney;
$totval=$quan * $val;
$newresmoney=$resmoney + $totval;
if($avmoney<$totval)
{
echo "T";
}
else
{
$query="INSERT INTO trades VALUES ('','" . $id . "','0','" . $quan . "','" . $val . "','0','1','',NOW())";
mysql_query($query);
$query="UPDATE users SET resmoney = '$newresmoney' WHERE id='$id'";
mysql_query($query);

echo "D";
}
}

else{
echo "N";
}
include "scripts/serverloginend.php";
?>
