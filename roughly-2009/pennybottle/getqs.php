<?php

include "scripts/serverlogin.php";

if($num==1){
if($paused==1){
die("Paused");
}
$query="SELECT * FROM trades WHERE sold='0' and bid='1' ORDER BY val DESC";
$result=mysql_query($query);
$num=mysql_numrows($result);
$i=0;
?>
<table id="buytab" border="0">
<?php
while ($i < $num) {
?>
<tr id="tr-<?php echo mysql_result($result,$i,"id"); ?>">
<td onclick="buybuy(<?php echo mysql_result($result,$i,"id"); ?>)">
<?php
$data = mysql_result($result,$i,"sellid");
$result2 = mysql_query("SELECT * FROM users WHERE id='" . $data . "'");
echo mysql_result($result2,0,"user");
?>
</td>
<td onclick="buybuy(<?php echo mysql_result($result,$i,"id"); ?>)">
Buying
</td>
<td onclick="buybuy(<?php echo mysql_result($result,$i,"id"); ?>)">
<?php
echo mysql_result($result,$i,"quan");
?>
</td>
<td onclick="buybuy(<?php echo mysql_result($result,$i,"id"); ?>)">
@
</td>
<td onclick="buybuy(<?php echo mysql_result($result,$i,"id"); ?>)">
<?php
echo mysql_result($result,$i,"val");
?>
</td>
<td onclick="buybuy(<?php echo mysql_result($result,$i,"id"); ?>)">
=
</td>
<td onclick="buybuy(<?php echo mysql_result($result,$i,"id"); ?>)">
<?php
echo mysql_result($result,$i,"quan") * mysql_result($result,$i,"val");
?>
</td>
<td onclick="buybuy(<?php echo mysql_result($result,$i,"id"); ?>)">
p
</td>
</tr>
<?php
$i++;
}
?>
</table>
    TABLE SEPERATOR   
<?php
$query="SELECT * FROM trades WHERE sold='0' and bid='0' ORDER BY val";
$result=mysql_query($query);
$num=mysql_numrows($result);
$i=0;
?>
<table id="selltab" border="0">
<?php
while ($i < $num) {
?>
<tr id="tr-<?php echo mysql_result($result,$i,"id"); ?>">
<td onclick="buytrade(<?php echo mysql_result($result,$i,"id"); ?>)">
<?php
$data = mysql_result($result,$i,"sellid");
$result2 = mysql_query("SELECT * FROM users WHERE id='" . $data . "'");
echo mysql_result($result2,0,"user");
?>
</td>
<td onclick="buytrade(<?php echo mysql_result($result,$i,"id"); ?>)">
Selling
</td>
<td onclick="buytrade(<?php echo mysql_result($result,$i,"id"); ?>)">
<?php
echo mysql_result($result,$i,"quan");
?>
</td>
<td onclick="buytrade(<?php echo mysql_result($result,$i,"id"); ?>)">
@
</td>
<td onclick="buytrade(<?php echo mysql_result($result,$i,"id"); ?>)">
<?php
echo mysql_result($result,$i,"val");
?>
</td>
<td onclick="buytrade(<?php echo mysql_result($result,$i,"id"); ?>)">
=
</td>
<td onclick="buytrade(<?php echo mysql_result($result,$i,"id"); ?>)">
<?php
echo mysql_result($result,$i,"quan") * mysql_result($result,$i,"val");
?>
</td>
<td onclick="buytrade(<?php echo mysql_result($result,$i,"id"); ?>)">
p
</td>
</tr>
<?php
$i++;
}
?>
</table>
  2 TABLE SEPERATOR   
<?php
$query="SELECT id,fromid,type,amt,retamt,status,(retamt/amt)-1 AS intrest FROM borrows WHERE status='0' ORDER BY intrest DESC";
$result=mysql_query($query);
$num=mysql_numrows($result);
$i=0;
?>
<table id="lendmtab" border="0">
<?php
while ($i < $num) {
?>
<tr id="tr-<?php echo mysql_result($result,$i,"id"); ?>">
<td onclick="buyborrow(<?php echo mysql_result($result,$i,"id"); ?>)">
<?php
$data = mysql_result($result,$i,"fromid");
$result2 = mysql_query("SELECT * FROM users WHERE id='" . $data . "'");
echo mysql_result($result2,0,"user");
?>
</td>
<td onclick="buyborrow(<?php echo mysql_result($result,$i,"id"); ?>)">
Wants
</td>
<td onclick="buyborrow(<?php echo mysql_result($result,$i,"id"); ?>)">
<?php
echo mysql_result($result,$i,"amt");
if(mysql_result($result,$i,"type") == 1)
{
echo "p";
}
else
{
echo "bt.";
}
?>
</td>
<td onclick="buyborrow(<?php echo mysql_result($result,$i,"id"); ?>)">
->
</td>
<td onclick="buyborrow(<?php echo mysql_result($result,$i,"id"); ?>)">
<?php
echo mysql_result($result,$i,"retamt");
if(mysql_result($result,$i,"type") == 1)
{
echo "p";
}
else
{
echo "bt.";
}
?>
</td>
</tr>
<?php
$i++;
}
}
include "scripts/serverloginend.php";
?>
