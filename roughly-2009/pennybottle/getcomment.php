<?php

include "scripts/serverlogin.php";

if($num==1){
if($paused==1){
die("Paused");
}
$query="SELECT * FROM im ORDER BY ts DESC LIMIT 10";
$result=mysql_query($query);
$num=mysql_numrows($result);
$i=0;
?>
<table id="messagetab" border="0">
<?php
while ($i < $num) {
?>
<tr>
<td>
<?php
$currid = mysql_result($result,$i,userid);
echo mysql_result(mysql_query("SELECT * FROM users WHERE id='$currid'"),0,user);
echo ": ";
echo mysql_result($result,$i,message);
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