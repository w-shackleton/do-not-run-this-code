<?php
include "connect.php";
mysql_connect($url,$user,$password);
@mysql_select_db($database) or die( "Unable to select database");

$pass=$_POST['pass'];

$pass2 = md5($pass);
$query="SELECT * FROM admin WHERE password='$pass2'";
$result=mysql_query($query);

$num=mysql_numrows($result);

if ($num == 1) {


session_start();
//echo htmlspecialchars(SID);
$_SESSION['admpasswd'] = $pass2;
mysql_close();
header('Location: index.php');
}
else{
echo"Failed Login";
mysql_close();
}

?>
