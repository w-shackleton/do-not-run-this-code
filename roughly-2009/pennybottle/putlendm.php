<?php
include "scripts/serverlogin.php";

if($num==1){
if($paused==1){
die("P");
}
$id=$_SESSION['uid'];
$borrow=$_POST['borrow'];
$return=$_POST['return'];
$isbottle=$_POST['bottle'];

$borrow = stripslashes($borrow);
$borrow = mysql_real_escape_string($borrow);
$return = stripslashes($return);
$return = mysql_real_escape_string($return);
$bottle = stripslashes($bottle);
$bottle = mysql_real_escape_string($bottle);
$query="INSERT INTO borrows VALUES ('','" . $id . "','0','" . $isbottle . "','" . $borrow . "','" . $return . "',NOW(),'0','0')";
mysql_query($query);
echo "D";
}

else{
echo "N";
}
include "scripts/serverloginend.php";
?>
