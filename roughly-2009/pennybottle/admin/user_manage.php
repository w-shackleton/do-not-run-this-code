<?php
include "scripts/serverlogin.php";

if($num==1){
$query="SELECT * FROM paused";
$result=mysql_query($query);
$paused=mysql_result($result,0,"paused");
$query="SELECT * FROM users";
$result=mysql_query($query);
$num=mysql_numrows($result);
$i=0;
$disabled = "";
$disabledtext = "";
if($paused == 0){
$disabled = " disabled";
$disabledtext = "Please pause the game to change these values.";
}
}
?><html>
<head>
<title>Pennies Admin - Users</title>
<link rel="stylesheet" href="style.css" type="text/css" />
<script type="text/javascript" charset="utf-8" src="js.js"></script>
<script src="sorttable.js"></script>
</head>
<body>

<table id="users" class="sortable">
	<tbody>
		<tr>
			<td>ID</td>
			<td>Username</td>
			<td>Bottles</td>
			<td>Money</td>
			<td>Wealth</td>
		</tr>
		<?php
		while ($i < $num) {
		?>
		<tr>
			<td>
				<?php
				$currentid = mysql_result($result,$i,"id");
				echo $currentid;
				?>
			</td>
			<td>
				<?php echo mysql_result($result,$i,"user"); ?>
			</td>
			<td>
				<?php echo mysql_result($result,$i,"bottle"); ?>
			</td>
			<td>
				<?php echo mysql_result($result,$i,"money"); ?>
			</td>
			<td>
				<?php echo mysql_result($result,$i,"money") + mysql_result(mysql_query("SELECT * FROM admin"),0,"bottleval") * mysql_result($result,$i,"bottle"); ?>
			</td>
			<td>
				<?php
				if(mysql_result(mysql_query("SELECT * FROM users WHERE id=$currentid"),0,disabled) == 0)
				{
				?>
				<a href="del_conf.php?id=<?php echo mysql_result($result,$i,"id"); ?>">Disable</a>
				<?php
				}
				else
				{
				?>
				<a href="del_conf.php?id=<?php echo mysql_result($result,$i,"id"); ?>">Enable</a>
				<?php
				}
				?>
			</td>
		</tr>
	<?php
	$i++;
	}
	?>
	</tbody>
	<tfoot>
		<tr>
			<td>
				
			</td>
			<td>
				TOTAL:
			</td>
			<td>
				<?php
					$row = mysql_fetch_assoc(mysql_query("SELECT SUM(bottle) AS sum FROM users"));
					echo $row['sum'];
				?>
			</td>
			<td>
				
				<?php
					$row = mysql_fetch_assoc(mysql_query("SELECT SUM(money) AS sum FROM users"));
					echo $row['sum'];
				?>
			</td>
			<td>
				
			</td>
		</tr>
		<form action="change_amts_conf.php" method="POST">
		<tr>
			<td>
				
			</td>
			<td>
				Change:
			</td>
			<td>
				<input type="input" name="bottle" size="4">
			</td>
			<td>
				<input type="input" name="money" size="4">
			</td>
			<td>
				<input type="submit" value="Change"<?php echo $disabled; ?>>
			</td>
		</tr>
		</form>
		<form action="add_amts_conf.php" method="POST">
		<tr>
			<td>
				
			</td>
			<td>
				Add wealth:
			</td>
			<td>
				<input type="input" name="bottle" size="4">
			</td>
			<td>
				<input type="input" name="money" size="4">
			</td>
			<td>
				<input type="submit" value="Add"<?php echo $disabled; ?>>
			</td>
		</tr>
		</form>
		<form action="add_usrs.php" method="POST">
		<tr>
			<td>
				
			</td>
			<td>
				<b>Add Users:</b>
			</td>
			<td>
				Amount:
			</td>
			<td>
				<input type="input" name="amt" size="4">
			</td>
			<td>
				<input type="submit" value="Go"<?php echo $disabled; ?>>
			</td>
		</tr>
		</form>
	</tfoot>
</table>
<?php echo $disabledtext; ?><br>
<a href="index.php">Go Back</a>
<?php include "scripts/serverloginend.php"; ?>
</body>
</html>