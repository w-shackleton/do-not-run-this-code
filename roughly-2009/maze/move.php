<?php
session_start();
if(!isset($_SESSION['mL']))
{
	die("Maze Not Loaded");
}
$dir = $_GET['dir'];
$m = $_SESSION['maze'];

$meX = $_SESSION['mX'];
$meY = $_SESSION['mY'];
switch($dir)
{
	case "u":
		if($m[$meY][$meX] == 1 || $m[$meY][$meX] == 3)
		{
			header('Location: maze.php?e=c');
			die();
		}
		else
		{
			$meY--;
		}
		break;
	case "l":
		if($m[$meY][$meX] == 2 || $m[$meY][$meX] == 3)
		{
			header('Location: maze.php?e=c');
			die();
		}
		else
		{
			$meX--;
		}
		break;
	case "d":
		if(isset($m[$meY+1][$meX]))
		{
			if($m[$meY+1][$meX] == 1 || $m[$meY+1][$meX] == 3)
			{
				header('Location: maze.php?e=c');
				die();
			}
			else
			{
				$meY++;
			}
		}
		else
		{
			header('Location: maze.php?e=d');
			die();
		}
		break;
	case "r":
		if(isset($m[$meY][$meX+1]))
		{
			if($m[$meY][$meX+1] == 2 || $m[$meY][$meX+1] == 3)
			{
				header('Location: maze.php?e=c');
				die();
			}
			else
			{
				$meX++;
			}
		}
		else
		{
			header('Location: maze.php?e=d');
			die();
		}
		break;
}
$_SESSION['mX'] = $meX;
$_SESSION['mY'] = $meY;
header('Location: maze.php');
?>