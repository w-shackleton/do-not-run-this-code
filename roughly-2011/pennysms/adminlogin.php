<?php
session_start();
if($_SESSION['pass'] != "<YES, THERE WAS A PASSWORD HERE>")
{
	header("Location: index.php?l=f");
	die("Incorrect Password");
}
?>
