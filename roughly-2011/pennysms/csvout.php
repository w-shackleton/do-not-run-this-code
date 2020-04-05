<?php
include "adminlogin.php";
include "connect.php";
$querySString = "";
if(isset($_GET['session']))
{
$sessionNum = mysql_real_escape_string($_GET['session']);
if(mysql_num_rows(mysql_query("SELECT id FROM sessions WHERE id='" . $sessionNum . "' LIMIT 1")) == 0)
{
die("<h3>Error: No such session!</h3>");
}
else
{
$querySString = " WHERE session='" . mysql_real_escape_string($_GET['session']) . "'";
$sessionTitle = " " . mysql_result(mysql_query("SELECT name FROM sessions WHERE id='" . $sessionNum . "' LIMIT 1"), 0);
}
}
$query = "SELECT * FROM penny" . $querySString . " ORDER BY id ASC";
$res = mysql_query($query);
$query = "SELECT minval,name FROM penny" . $querySString . " ORDER BY minval DESC";
$res2 = mysql_query($query);
$query = "SELECT maxval,name FROM penny" . $querySString . " ORDER BY maxval ASC";
$res3 = mysql_query($query);
header('Content-type: text/csv');
header('Content-Disposition: attachment; filename="Penny game' . $sessionTitle . ' ' . date("Y-m-d H:i:s") . '.csv"');
?>Number,ID,Time,Name,Minval,Maxval<?php if($sessionTitle == ""){echo ",Session ID,Session Name";} ?>,,Volume,Bids (sorted mins),Asks (sorted maxes),Buyer,Seller
<?php
for($i = 0; $i < mysql_num_rows($res); $i++)
{
echo $i + 1 . ",";
echo mysql_result($res, $i, id) . ",";
echo mysql_result($res, $i, time) . ",";
//echo "'" . mysql_result($res, $i, number) . ",";
echo mysql_result($res, $i, name) . ",";
echo mysql_result($res, $i, minval) . ",";
echo mysql_result($res, $i, maxval) . ",";
if($sessionTitle == "")
{
echo mysql_result($res, $i, session) . ",";
echo mysql_result(mysql_query("SELECT name FROM sessions WHERE id='" . mysql_result($res, $i, session) . "'"),0) . ",,";
}
else echo ",";

echo $i + 1 . ",";
echo mysql_result($res2, $i, minval) . ",";
echo mysql_result($res3, $i, maxval) . ",";

echo mysql_result($res2, $i, name) . ",";
echo mysql_result($res3, $i, name) . ",";
?>

<?php
}
mysql_close();
?>
