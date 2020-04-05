<?php
include "scripts/serverlogin.php";

if($num==1){
if($paused==1){
die("P");
}
$id=$_SESSION['uid'];
$bid=$_POST['bid'];

$bid = stripslashes($bid);
$bid = mysql_real_escape_string($bid);

if((mysql_result(mysql_query("SELECT * from trades WHERE id='$bid'"),0,"sold") == 1) || (mysql_result(mysql_query("SELECT * from trades WHERE id='$bid'"),0,"sold") == 2))
{
die ("S");
}

$query="SELECT * FROM users WHERE id='$uid'";
$result=mysql_query($query);
$bottles=mysql_result($result,0,"bottle");
$money=mysql_result($result,0,"money");
$resmoney=mysql_result($result,0,"resmoney");

$query="SELECT * FROM trades WHERE id='$bid'";
$result=mysql_query($query);
$bottlecost=mysql_result($result,0,"val");
$bottlequan=mysql_result($result,0,"quan");
$seller=mysql_result($result,0,"sellid");

$query="SELECT * FROM users WHERE id='$seller'";
$result=mysql_query($query);
$sellerbottles=mysql_result($result,0,"bottle");
$sellerresbottles=mysql_result($result,0,"resbottle");
$sellermoney=mysql_result($result,0,"money");

$totbottlecost=$bottlecost*$bottlequan;

$newsellermoney=$sellermoney+$totbottlecost;
$newmoney=$money-$totbottlecost;

$newsellerbottles=$sellerbottles-$bottlequan;
$newsellerresbottles=$sellerresbottles-$bottlequan;

$newbottles=$bottles+$bottlequan;

/*echo "\nmy bottles " . $bottles;
echo "\nmy money " . $money;
echo "\ntrade cost " . $bottlecost;
echo "\ntrade quan " . $bottlequan;
echo "\nseller " . $seller;
echo "\nseller bottles " . $sellerbottles;
echo "\nseller res bottles " . $sellerresbottles;
echo "\nseller money " . $sellermoney;
echo "\ntotal bottle cost " . $totbottlecost;
echo "\nnew seller money " . $newsellermoney;
echo "\nnew money " . $newmoney;
echo "\nnew seller bottles " . $newsellerbottles;
echo "\nnew seller res bottles " . $newsellerresbottles;
echo "\nnew bottles " . $newbottles;
echo "\nbottle ID " . $bid;*/
if($id==$seller)
{

$query="UPDATE trades SET sold = '2' WHERE id='$bid'";
mysql_query($query);

$query="UPDATE users SET resbottle = '$newsellerresbottles' WHERE id='$seller'";
mysql_query($query);

$query="UPDATE trades SET buyid = '$id' WHERE id='$bid'";
mysql_query($query);

echo "D";
}
else
{
if($money-$resmoney<$totbottlecost)
{
echo "T";
}
else
{
$query="UPDATE trades SET sold = '1' WHERE id='$bid'";
mysql_query($query);
$query="UPDATE trades SET buyid = '$id' WHERE id='$bid'";
mysql_query($query);

$query="UPDATE users SET money = '$newsellermoney' WHERE id='$seller'";
mysql_query($query);
$query="UPDATE users SET money = '$newmoney' WHERE id='$id'";
mysql_query($query);

$query="UPDATE users SET bottle = '$newsellerbottles' WHERE id='$seller'";
mysql_query($query);
$query="UPDATE users SET resbottle = '$newsellerresbottles' WHERE id='$seller'";
mysql_query($query);

$query="UPDATE users SET bottle = '$newbottles' WHERE id='$id'";
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
