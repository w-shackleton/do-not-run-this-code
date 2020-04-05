<?php
include "scripts/serverlogin.php";

if($num==1){
header("Content-Type: text/csv");
$result = mysql_query("SELECT * FROM users");
?>
User ID,Name,Bottles,Reserved Bottles,Money,Reserved Money,Disabled,Completed Trades,Open Trades,Cancelled Trades
<?php
for($i=0;$i<mysql_num_rows($result);$i++)
{
$currid = mysql_result($result,$i,id);
$query2="SELECT * FROM trades WHERE sold='1' AND (buyid='$currid' OR sellid='$currid')";
$result2=mysql_query($query2);
$numcompleted=mysql_numrows($result2);
$query2="SELECT * FROM trades WHERE sold='0' AND (buyid='$currid' OR sellid='$currid')";
$result2=mysql_query($query2);
$numopen=mysql_numrows($result2);
$query2="SELECT * FROM trades WHERE sold='2' AND (buyid='$currid' OR sellid='$currid')";
$result2=mysql_query($query2);
$numcancelled=mysql_numrows($result2);
echo $currid;
echo ",";
echo mysql_result($result,$i,user);
echo ",";
echo mysql_result($result,$i,bottle);
echo ",";
echo mysql_result($result,$i,resbottle);
echo ",";
echo mysql_result($result,$i,money);
echo ",";
echo mysql_result($result,$i,resmoney);
echo ",";
echo mysql_result($result,$i,disabled);
echo ",";
echo $numcompleted;
echo ",";
echo $numopen;
echo ",";
echo $numcancelled;
echo "\n";
}
}
else
{
header('Location: home.php');
}
include "scripts/serverloginend.php";
?>
