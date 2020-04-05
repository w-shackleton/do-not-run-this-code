<?php
include "scripts/serverlogin.php";

if($num==1){
header("Content-Type: text/csv");
$result = mysql_query("SELECT * FROM borrows");
?>
Lend ID,Trade From,Trade To,Type,Initial Amt,Return Amt,Created Time,Completed Time,Status
<?php
for($i=0;$i<mysql_num_rows($result);$i++)
{
echo mysql_result($result,$i,id);
echo ",";
$fromid = mysql_result($result,$i,fromid);
echo mysql_result(mysql_query("SELECT * FROM users WHERE id='$fromid'"),0,user);
echo ",";
$compid = mysql_result($result,$i,compid);
if($compid != 0)
{
echo mysql_result(mysql_query("SELECT * FROM users WHERE id='$compid'"),0,user);
}
echo ",";
echo mysql_result($result,$i,type);
echo ",";
echo mysql_result($result,$i,amt);
echo ",";
echo mysql_result($result,$i,retamt);
echo ",";
echo mysql_result($result,$i,ts);
echo ",";
echo mysql_result($result,$i,compts);
echo ",";
echo mysql_result($result,$i,status);
echo "\n";
}
}
else
{
header('Location: home.php');
}
include "scripts/serverloginend.php";
?>
