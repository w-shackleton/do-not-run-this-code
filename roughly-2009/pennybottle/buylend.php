<?php
include "scripts/serverlogin.php";

if($num==1){
if($paused==1){
die("P");
}
$id=$_SESSION['uid'];
$lid=$_POST['lid'];

$lid = stripslashes($lid);
$lid = mysql_real_escape_string($lid);

if((mysql_result(mysql_query("SELECT * from borrows WHERE id='$lid'"),0,"status") == 1) || (mysql_result(mysql_query("SELECT * from borrows WHERE id='$lid'"),0,"status") == 2))
{
die ("S");
}

$query="SELECT * FROM borrows WHERE id='$lid'";
$result=mysql_query($query);
$lendamt=mysql_result($result,0,"amt");
$lendret=mysql_result($result,0,"retamt");
$seller=mysql_result($result,0,"fromid");
$type=mysql_result($result,0,"type");

if($type == "1")
{
$query="SELECT * FROM users WHERE id='$uid'";
$result=mysql_query($query);
$money=mysql_result($result,0,"money");
$resmoney=mysql_result($result,0,"resmoney");

$query="SELECT * FROM users WHERE id='$seller'";
$result=mysql_query($query);
$sellermoney=mysql_result($result,0,"money");

$newsellermoney=$sellermoney+$lendamt;
$avmoney=$money-$resmoney;
$newmoney=$money-$lendamt;

if($id==$seller)
{
$query="UPDATE borrows SET status = '2' WHERE id='$lid'";
mysql_query($query);
echo "D";
}
elseif($avmoney<$lendamt)
{
echo "T";
}
else
{
$query="UPDATE borrows SET status = '1' WHERE id='$lid'";
mysql_query($query);
$query="UPDATE borrows SET compid = '$id' WHERE id='$lid'";
mysql_query($query);


$query="UPDATE users SET money = '$newsellermoney' WHERE id='$seller'";
mysql_query($query);
$query="UPDATE users SET money = '$newmoney' WHERE id='$id'";
mysql_query($query);

echo "D";
}
}
else
{
$query="SELECT * FROM users WHERE id='$uid'";
$result=mysql_query($query);
$bottle=mysql_result($result,0,"bottle");
$resbottle=mysql_result($result,0,"resbottle");

$query="SELECT * FROM users WHERE id='$seller'";
$result=mysql_query($query);
$sellerbottle=mysql_result($result,0,"bottle");

$newsellerbottle=$sellerbottle+$lendamt;
$avbottle=$bottle-$resbottle;
$newbottle=$bottle-$lendamt;

if($id==$seller)
{
$query="UPDATE borrows SET status = '2' WHERE id='$lid'";
mysql_query($query);
echo "D";
}
elseif($avbottle<$lendamt)
{
echo "T";
}
else
{
$query="UPDATE borrows SET status = '1' WHERE id='$lid'";
mysql_query($query);
$query="UPDATE borrows SET compid = '$id' WHERE id='$lid'";
mysql_query($query);


$query="UPDATE users SET bottle = '$newsellerbottle' WHERE id='$seller'";
mysql_query($query);
$query="UPDATE users SET bottle = '$newbottle' WHERE id='$id'";
mysql_query($query);

echo "D";
}
}

}

else{
echo "N";
}
include "scripts/serverloginend.php";
?>
