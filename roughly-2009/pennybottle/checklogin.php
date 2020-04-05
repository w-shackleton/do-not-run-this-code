<?php
include "connect.php";
mysql_connect($url,$user,$password);
@mysql_select_db($database) or die( "Unable to select database");

$usr=$_POST['user'];
$pass=$_POST['pass'];

$usr = stripslashes($usr);
$usr = mysql_real_escape_string($usr);

$pass2 = md5($pass);
$query="SELECT * FROM users WHERE user='$usr' and password='$pass2'";
$result=mysql_query($query);

$num=mysql_numrows($result);

if ($num == 1) {

$id=mysql_result($result,0,"id");

session_start();
//echo htmlspecialchars(SID);
$_SESSION['uid'] = $id;
$_SESSION['passwd'] = $pass2;
mysql_close();
header('Location: index.php');
}
else{
header('Location: home.php?e=f');
mysql_close();
}

?>
