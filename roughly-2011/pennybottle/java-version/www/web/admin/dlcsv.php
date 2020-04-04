<?php 
ini_set('display_errors',1);
error_reporting(E_ALL);

$dbneeded = true;
include "include/serverlogin.php";

$what = $_REQUEST['what'];
switch($what) {
case "users":
	header("Content-type: application/octet-stream");
	header("Content-Disposition: attachment; filename=\"Pennybottle " . date("y m d") . " users.csv\"");
	$value = (int)$_REQUEST['value'];
	echo "Users\n";
	echo "User ID, Username, User friendly name, Pennies, Bottles, Worth (at $value ppb)\n";
	$result = mysql_query("SELECT id, username, friendlyname, pennies, bottles, (bottles * $value + pennies) AS value FROM users;");

	while($row = mysql_fetch_array($result)) {
		$id = $row['id'];
		$username = $row['username'];
		$friendlyname = $row['friendlyname'];
		$pennies = $row['pennies'];
		$bottles = $row['bottles'];
		$value = $row['value'];
		echo "$id,$username,$friendlyname,$pennies,$bottles,$value\n";
	}
	break;
case "trades":
	header("Content-type: application/octet-stream");
	header("Content-Disposition: attachment; filename=\"Pennybottle " . date("y m d") . " trades.csv\"");
	echo "Trades\n";
	echo "Trade ID, Status, Type, From UserID, From Username, To UserID, To Username, Pennies per bottle, Bottles, Total Pennies, Time created, Time accepted\n";
	$result = mysql_query("SELECT quotes.id AS quoteid, quotes.status, quotes.type, quotes.idfrom AS fromid, userfrom.friendlyname AS fromname, quotes.idto AS toid, userto.friendlyname AS toname, quotes.pennies AS qpennies, quotes.bottles AS qbottles, (CAST(quotes.pennies AS signed) * quotes.bottles) AS qvalue, timecreated, timeaccepted FROM quotes LEFT JOIN users AS userfrom ON idfrom=userfrom.id LEFT JOIN users AS userto ON idto=userto.id ORDER BY quotes.id;");

	while($row = mysql_fetch_array($result)) {
		$quoteid = $row['quoteid'];
		$status = $row['status'];
		$type = $row['type'];

		$fromid = $row['fromid'];
		$fromname = $row['fromname'];
		$toid = $row['toid'];
		$toname = $row['toname'];

		$qpennies = $row['qpennies'];
		$qbottles = $row['qbottles'];
		$qvalue = $row['qvalue'];
		$timecreated = $row['timecreated'];
		$timeaccepted = $row['timeaccepted'];

		echo "$quoteid,$status,$type,$fromid,$fromname,$toid,$toname,$qpennies,$qbottles,$qvalue,$timecreated,$timeaccepted\n";
	}
	break;
case "quoteguesses":
	header("Content-type: application/octet-stream");
	header("Content-Disposition: attachment; filename=\"Pennybottle " . date("y m d") . " worth guesses.csv\"");
	echo "Trades\n";
	echo "Guess ID, User ID, Username, Guess, Time\n";
	$result = mysql_query("SELECT g.id, g.userid, u.friendlyname, g.guess, g.time FROM worthguess AS g JOIN users AS u ON g.userid=u.id ORDER BY g.id;");

	while($row = mysql_fetch_array($result)) {
		$id = $row['id'];
		$userid = $row['userid'];
		$username = $row['friendlyname'];
		$guess = $row['guess'];
		$time = $row['time'];

		echo "$id,$userid,$username,$guess,$time\n";
	}
	break;
}

include "include/serverloginend.php";
?>
