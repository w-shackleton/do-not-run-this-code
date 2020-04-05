<?php
include "scripts/serverlogin.php";

if($num==1){
header("Content-Type: text/csv");
$result = mysql_query("SELECT * FROM trades");
?>
Trade ID,Money From,Money To,Bottles,Money Per Bottle,Sale Status,Bid,Timestamp,Initial Timestamp
<?php
for($i=0;$i<mysql_num_rows($result);$i++)
{
echo mysql_result($result,$i,id);
echo ",";
$sellid = mysql_result($result,$i,sellid);
echo mysql_result(mysql_query("SELECT * FROM users WHERE id='$sellid'"),0,user);
echo ",";
$buyid = mysql_result($result,$i,buyid);
if($buyid != 0)
{
echo mysql_result(mysql_query("SELECT * FROM users WHERE id='$buyid'"),0,user);
}
echo ",";
echo mysql_result($result,$i,quan);
echo ",";
echo mysql_result($result,$i,val);
echo ",";
echo mysql_result($result,$i,sold);
echo ",";
echo mysql_result($result,$i,bid);
echo ",";
$ts = mysql_result($result,$i,ts);
if($ts == "0000-00-00 00:00:00")
{
$ts = "0";
}
echo $ts;
echo ",";
$tsinit = mysql_result($result,$i,tsinit);
if($tsinit == "0000-00-00 00:00:00")
{
$tsinit = "0";
}
echo $tsinit;
echo ",\n";
}
}
else
{
header('Location: home.php');
}
include "scripts/serverloginend.php";
?>
