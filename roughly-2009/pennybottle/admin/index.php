<?php
include "scripts/serverlogin.php";

if($num==1){
$query="SELECT * FROM paused";
$result=mysql_query($query);
$ispaused=mysql_result($result,0,"paused");
$disabled = " disabled";
$pausegame = "pause game to repay. ";

?>
<html>
<head>
<title>Pennies Admin</title>
<link rel="stylesheet" href="style.css" type="text/css" />
<script type="text/javascript" charset="utf-8" src="js.js">

</script>
</head>
<body>
<?php
if($ispaused==1){
$disabled = "";
$pausegame = "";
?>
Game is paused<br>
<a href="pause.php">Unpause</a><br>
<?php
}
else{
?>
Game is running<br>
<a href="pause.php">Pause</a><br>
<?php
}
?>
<a href="user_manage.php">Manage Users</a><br>
<form action="change_val.php" method="POST">
Change Value | Current: 
<?php
echo mysql_result(mysql_query("SELECT * FROM admin"),0,"bottleval");
?><br>
Value: <input type="input" name="val" size="4"><br>
<input type="submit" value="Change">
</form>
<hr>
<form action="change_enabled.php" method="POST">
Enabled panels:<input type="input" name="val" value="<?php echo mysql_result(mysql_query("SELECT * FROM enabled"),0,"e"); ?>">
(0 is default,1 is + lending panel)
<br>
<input type="submit" value="Change">
</form>
<hr>
<form action="message.php" method="POST">
Change Message | Current: 
<?php
echo mysql_result(mysql_query("SELECT * FROM message"),0,"message");
?><br>
New Message: <input type="input" name="message" size="20"><br>
<input type="submit" value="Change Message">
</form>
<hr>
<form action="htmlmessage.php" method="POST">
Change HTML Message<br>
Message (in html): <input type="input" name="message" size="60" value='<?php echo str_replace("\\","",mysql_result(mysql_query("SELECT * FROM htmlmessage"),0,"message")); ?>'><br>
<input type="submit" value="Change HTML Message"> The message does not update automatically. This has to be set for the game, and will only update if the page is refreshed.<br>DO NOT go past 8 lines.
<br>
Use <code>&lt;img src="1p.gif"&gt;</code> to get the 1p image.<br>
Use <code>&lt;img src="1p.gif" style="float:left;"&gt;</code> to get the text wrapped around the image.<br>
For a line break, use <code>&lt;br&gt;</code>.
</form>
<hr>
<a href="data.php">Download Data</a>
<br><br>

<form action="cancel_debts_conf.php" method="POST">
<input type="submit" value="Pay all debts"<?php echo $disabled; ?>><?php echo $pausegame ?>(End of Game)
</form>

<br><br>
<form action="reset_conf.php" method="POST">
Reset the Game: (You may want to export to CSV first)
<input type="submit" value="Reset">
</form>
<hr>
<a href="logout.php">Logout</a>
<?php
}

else{
header('Location: home.php');
}
include "scripts/serverloginend.php";
?>
</body>
</html>