<?php

include "scripts/serverlogin.php";

if($num==1){
if($paused==1){
die("Paused");
}
$query="SELECT * FROM trades WHERE sold='1'";
$result=mysql_query($query);
$num=mysql_numrows($result);
$num=$num-31;
if($num<0)
{
$num=0;
}
$query="SELECT * FROM trades WHERE sold='1' ORDER BY id DESC LIMIT $num, 30";
$result=mysql_query($query);
$num=mysql_numrows($result);
$i=0;

$query4="SELECT bottleval,id FROM users WHERE id='$uid'";
$result4=mysql_query($query4);
$bottleval=mysql_result($result4,0,"bottleval");
?>
Last 30 Trades:
<table id="comptab" border="0">
<?php
while ($i < $num) {
$incme = false;
$incme = ((mysql_result($result,$i,"sellid") == $uid) || (mysql_result($result,$i,"buyid") == $uid));
?>
<tr id="tr-<?php echo mysql_result($result,$i,"id"); ?>" class="<?php if($incme){ echo "incme"; } ?>">
<td>
<?php
if(mysql_result($result,$i,"bid") == 1)
{
$data = mysql_result($result,$i,"buyid");
}
else
{
$data = mysql_result($result,$i,"sellid");
}
$result2 = mysql_query("SELECT * FROM users WHERE id='" . $data . "'");
echo mysql_result($result2,0,"user");
?>
</td>
<td>
->
</td>
<td>
<?php
if(mysql_result($result,$i,"bid") == 1)
{
$data = mysql_result($result,$i,"sellid");
}
else
{
$data = mysql_result($result,$i,"buyid");
}
$result2 = mysql_query("SELECT * FROM users WHERE id='" . $data . "'");
echo mysql_result($result2,0,"user");
?>
</td>
<td>
<?php
echo mysql_result($result,$i,"quan");
?>
</td>
<td>
@
</td>
<td>
<?php
echo mysql_result($result,$i,"val");
?>
</td>
<td>
=
</td>
<td>
<?php
echo mysql_result($result,$i,"quan") * mysql_result($result,$i,"val");
?>
</td>
<td>
p
</td>
</tr>
<?php
$i++;
}
?>
</table>


<?php
$query="SELECT user,bottle,money,(bottle*$bottleval)+money AS wealth FROM users ORDER BY wealth DESC";
$result=mysql_query($query);
$num=mysql_numrows($result);
$i=0;
?>
User Leaderboard:
<table id="usertab" border="0">
<?php
while ($i < $num) {
?>
<tr>
<td>
<?php
echo mysql_result($result,$i,"user");
?>
</td>
<td>
=
</td>
<td>
<?php
echo mysql_result($result,$i,"money");
?>
</td>
<td>
,
</td>
<td>
<?php
echo mysql_result($result,$i,"bottle");
?>
</td>
<td>
=
</td>
<td>
<?php
echo mysql_result($result,$i,"wealth");
?>
</td>
</tr>
<?php
$i++;
}
}
?>
</table>

<?php include "scripts/serverloginend.php"; ?>