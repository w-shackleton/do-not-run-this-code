<?php
session_start();
if(!isset($_POST['password']))
{
	header("Location: index.php?l=n");
	exit();
}
if($_POST['password'] == "<YES, THERE WAS A PASSWORD HERE>")
{
	$_SESSION['pass'] = $_POST['password'];
	header("Location: admin.php");
	exit();
}
header("Location: index.php?l=f");
?>
