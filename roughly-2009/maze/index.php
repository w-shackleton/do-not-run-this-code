<?php
include 'serverlogin.php';

$result = mysql_query("SELECT * FROM mazes");
?>
<h1>Jigmaze</h1>
<br>
<a href="create">Create new Maze</a> (fairly self-explainatory).
<br>
<br>
<table>
<?php
for($i = 0;$i < mysql_numrows($result);$i++)
{
?>
<tr>
<td><a href="loadmaze.php?n=<?php echo mysql_result($result,$i,id); ?>"><?php echo mysql_result($result,$i,id); ?></a></td>
<td><?php echo mysql_result($result,$i,name); ?></td>
<td><?php echo mysql_result($result,$i,user); ?></td>
<!--
<td><?php echo mysql_result($result,$i,rating); ?></td>
<td><?php echo mysql_result($result,$i,hard); ?></td>
-->
</tr>
<?php
}
?>
</table>
<?php
include 'serverloginend.php';
?>