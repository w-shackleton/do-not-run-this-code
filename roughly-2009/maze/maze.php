<?php
session_start();
if(!isset($_SESSION['mL']))
{
	die("Maze Not Loaded");
}
$tilesX = (int)$_SESSION['dX'];
$tilesY = (int)$_SESSION['dY'];
$squareSize = 20;
$osquareSize = $squareSize + $innerborder + $innerborder;
$border = 10;
$imgX = ($tilesX * $osquareSize) + ($border * 2);
$imgY = ($tilesY * $osquareSize) + ($border * 2);
?>
<br>
<img src="img/maze.png">
<br>
<code>
&nbsp;<a href="move.php?dir=u">U</a>
<br>
<a href="move.php?dir=l">L</a>&nbsp;<a href="move.php?dir=r">R</a>
<br>
&nbsp;<a href="move.php?dir=d">D</a>
<br>
</code>
<?php
if(isset($_GET['e']))
{
?>
<br>
!
<?php
}
else
{
?>
<br>
<?php
}
?>
<br>
<a href="index.php">Load new</a>