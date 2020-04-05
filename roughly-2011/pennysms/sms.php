<?php

if(@$_REQUEST['number'] == null)
{
	die("ERROR: number variable not declared");
}
if(@$_REQUEST['sms'] == null)
{
	die("ERROR: message variable not declared");
}
$instring = $_REQUEST['sms'];

$instring = ereg_replace(' to ', ' ', $instring);
$instring = trim(ereg_replace(' +', ' ', $instring));

$fh = fopen("stringdump.csv", 'a');
fwrite($fh, date("Y-m-d H-i-s") . "," . str_replace(" ", ",", $instring) . "\n");
fclose($fh);

include "connect.php";

$instring = mysql_real_escape_string(strtolower($instring));
$instring = str_replace(",", "", $instring);
if(strpos($instring,"penny") == 0)
{
	$instring = substr($instring, 6);
}

if(strrpos($instring, " ") == null) die("<h3>DIE: Couldn't get MAX number.</h3>");
$num2s = substr($instring, strrpos($instring, " "));
$num2 = (int)$num2s;
if($num2s != (string)$num2) die("<h3>DIE: Max number was a string!</h3>");

$instring = substr($instring, 0, strrpos($instring, " "));

if(strrpos($instring, " ") == null) die("<h3>DIE: Couldn't get MIN number.</h3>");
$num1s = substr($instring, strrpos($instring, " "));
$num1 = (int)$num1s;
if($num1s != (string)$num1) die("<h3>DIE: Min number was a string!</h3>");

$instring = substr($instring, 0, strrpos($instring, " "));
$num1 = abs($num1);
$num2 = abs($num2);

if($num1 > $num2)
{
	$temp = $num1;
	$num1 = $num2;
	$num2 = $temp;
}
$getSNum = mysql_query("SELECT id FROM sessions ORDER BY id DESC LIMIT 1");
$sessionNum = mysql_result($getSNum, 0);
$query = "INSERT INTO penny VALUES('',CURRENT_TIMESTAMP,'" . @$_REQUEST['number'] . "','" . $instring . "','" . $num1 . "','" . $num2 . "','" . $sessionNum . "')";

$result = mysql_query($query);

mysql_close();
?>
Data received.
