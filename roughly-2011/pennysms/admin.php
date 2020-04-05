<?php
include "adminlogin.php";
include "connect.php";

$sortby = "id";
$desc="ASC";
if($_GET['sort'] == "a") { $sortby = "id"; }
if($_GET['sort'] == "b") { $sortby = "name"; }
if($_GET['sort'] == "c") { $sortby = "minval"; }
if($_GET['sort'] == "d") { $sortby = "maxval"; }
if($_GET['desc'] == "d") { $desc = "DESC"; }

$getSNum = mysql_query("SELECT id FROM sessions ORDER BY id DESC LIMIT 1");
if(mysql_num_rows($getSNum) == 0)
{
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head profile="http://gmpg.org/xfn/11">
<title>Penny Admin page</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="style.css" type="text/css" />
</head>
<body>
<div id="infotxt">
<b>Error:</b> No sessions found. Create a new one:
</div>
<form method="POST" action="addsession.php">
Add new session:<input type="text" name="sname" maxlength="50" /><input type="submit" value="Add" />
</form>
</body>
</html>
<?php
mysql_close();
exit();
}
$sessionNum = mysql_result($getSNum, 0);
$sessionString = "";
if(isset($_GET['session']))
{
$sessionNum = mysql_real_escape_string($_GET['session']);
$sessionString = "session=" . $sessionNum . "&";
}

$query = "SELECT * FROM penny WHERE session='" . $sessionNum . "' ORDER BY " . $sortby . " " . $desc;
$res = mysql_query($query);
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head profile="http://gmpg.org/xfn/11">
<title>Penny Admin page</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<?php

if($_GET['refresh'] == "y")
	$_SESSION['autoref'] = "y";
else if($_GET['refresh'] == "n")
	$_SESSION['autoref'] = "n";

$autorefresh = "OFF";
$autorefreshLink = "y";
if($_SESSION['autoref'] == "y")
{
	echo "<meta http-equiv=\"refresh\" content=\"5\" />";
	$autorefresh = "<b>ON</b>";
	$autorefreshLink = "n";
}
?>

<link rel="stylesheet" href="style.css" type="text/css" />
</head>
<body>
<div id="infotxt">
<h1>Text '<b>penny</b>', followed by your <b>name</b>, followed by your <b>minimum</b> and <b>maximum</b> in <b>whole numbers of pennies</b> (not £s and decimal points) to <b>0779 788 5050</b></h1>
<b>Example:</b>"penny Jack 1 1000000'
</div>
<a href="">Refresh</a> | <a href="admin.php?refresh=<?php echo $autorefreshLink; ?>">Auto-refresh <?php echo $autorefresh; ?></a>
<table class="tab">
<tr>
<th colspan="4" style="border-bottom: 1px grey solid;"><?php
$sTitleQ = mysql_query("SELECT name FROM sessions WHERE id='" . $sessionNum . "'");
if(mysql_num_rows($sTitleQ) == 0)
{
echo "Error: Session not found! (select one from list)";
}
else
{
echo "Session: " . mysql_result($sTitleQ, 0);
}
?></th>
</tr>
<tr>
<th><a href="admin.php?<?php echo $sessionString; ?>sort=a<?php if($desc == 'ASC' && $_GET['sort'] == 'a') echo '&desc=d'; ?>">Time
<?php if($_GET['sort'] == "a") { if($desc == "DESC") { echo "<img src=\"down.gif\" />"; } else { echo "<img src=\"up.gif\" />"; } } ?></a></th>
<th><a href="admin.php?<?php echo $sessionString; ?>sort=b<?php if($desc == 'ASC' && $_GET['sort'] == 'b') echo '&desc=d'; ?>">Name
<?php if($_GET['sort'] == "b") { if($desc == "DESC") { echo "<img src=\"down.gif\" />"; } else { echo "<img src=\"up.gif\" />"; } } ?></a></th>
<th><a href="admin.php?<?php echo $sessionString; ?>sort=c<?php if($desc == 'ASC' && $_GET['sort'] == 'c') echo '&desc=d'; ?>">Min
<?php if($_GET['sort'] == "c") { if($desc == "DESC") { echo "<img src=\"down.gif\" />"; } else { echo "<img src=\"up.gif\" />"; } } ?></a></th>
<th><a href="admin.php?<?php echo $sessionString; ?>sort=d<?php if($desc == 'ASC' && $_GET['sort'] == 'd') echo '&desc=d'; ?>">Max
<?php if($_GET['sort'] == "d") { if($desc == "DESC") { echo "<img src=\"down.gif\" />"; } else { echo "<img src=\"up.gif\" />"; } } ?></a></th>
</tr>
<?php
for($i = 0; $i < mysql_num_rows($res); $i++)
{
?><tr<?php if($i % 2 == 1) echo " class=\"alt\""; ?>>
<td><?php echo substr(mysql_result($res, $i, time), 11); ?></td>
<td><?php echo mysql_result($res, $i, name); ?></td>
<td><?php echo mysql_result($res, $i, minval); ?></td>
<td><?php echo mysql_result($res, $i, maxval); ?></td>
</tr><?php
}
?>
</table>
<div class="foot">
<b>Download CSV:</b><br />
<a href="csvout.php">All</a> / <a href="csvout.php?session=<?php echo $sessionNum; ?>">Current session</a> | <a href="stringdump.php">Raw File</a><br /><br />
<a href="sendsms.php">Send fake SMS</a><br />
<hr>
<a href="logout.php">Logout</a>
</div>
<div id="session">
<b>Sessions:</b><br />
<form method="POST" action="addsession.php">
Add new session:<input type="text" name="sname" maxlength="50" /><input type="submit" value="Add" />
</form><hr />
<form method="GET" action="admin.php">
<select name="session">
<?php
$sResult = mysql_query("SELECT id,name FROM sessions ORDER BY id");
for($i = 0; $i < mysql_num_rows($sResult); $i++)
{
?>
<option value="<?php echo mysql_result($sResult, $i, id); ?>"<?php if($i + 1 == mysql_num_rows($sResult)) echo " selected=\"selected\""; ?>><?php echo mysql_result($sResult, $i, name); ?></option>
<?php
}
?>
</select>
<input type="submit" value="Change" />
<a href="admin.php">(Current)</a>
</form>
</div>
<a href="clearcheck.php" id="cleard">Clear data</a>
</body>
</html>
<?php
mysql_close();
?>
