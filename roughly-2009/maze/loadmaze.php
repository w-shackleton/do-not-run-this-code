<?php
session_start();
?>
Loading Maze...
<?php
include "serverlogin.php";
$n = 3;
if(isset($_GET['n'])) $n = $_GET['n'];
$result = mysql_query("SELECT * FROM mazes WHERE id='" . mysql_real_escape_string($n) . "'");
$maze = mysql_result($result,0,file);
$mazeId = mysql_result($result,0,id);
$m[] = array();
$p[] = array();
$p = explode(';',$maze);
$dims = explode(',',$p[0]);
/*echo $dims[0];
echo "<br>";
echo $dims[1];
echo "<br>";
echo "<code>";
*/
for($i = 1;$i < substr_count($maze,';') + 1;$i++)
{
$m[$i-1] = explode(',',$p[$i]);
}
$startX = 0;
$startY = 0;
$finX = $dims[0] - 1;
$finY = $dims[1] - 1;
$hasStart = false;
for($i = 0;$i<$dims[0];$i++)
{
	for($j = 0;$j<$dims[1];$j++)
	{
		switch($m[$i][$j])
		{
			case 4:
			case 5:
			case 6:
			case 7:
				if(!$hasStart)
				{
					$startX = $i;
					$startY = $j;
					$m[$i][$j] -= 4;
					$hasStart = true;
				}
				else
				{
					$finX = $i;
					$finY = $j;
					$m[$i][$j] -= 4;
				}
				break;
			default:
				break;
		}
	}
}
include "serverloginend.php";
?>
<br>
Maze Loaded, Shuffling...
<?php
function Invert($array)
{
	$newArray[] = array();
	for($i = 0;$i<count($array,0);$i++)
	{
		for($j = 0;$j<count($array[0],0);$j++)
		{
			$newArray[$j][$i] = $array[$i][$j];
		}
	}
	return $newArray;
}
$hShuffle[] = array();
$vShuffle[] = array();
for($i = 0;$i < $dims[0];$i++)
{
	$hShuffle[$i] = range(0,$dims[1]-1);
	shuffle($hShuffle[$i]);
}
for($i = 0;$i < $dims[1];$i++)
{
	$vShuffle[$i] = range(0,$dims[0]-1);
	shuffle($vShuffle[$i]);
}
$vShuffle = Invert($vShuffle);
?>
<br>Maze Shuffled, Saving...
<?php
$_SESSION['mazeid'] = $mazeId;
$_SESSION['maze'] = $m;

$_SESSION['dY'] = $dims[0];
$_SESSION['dX'] = $dims[1];

$_SESSION['hS'] = $hShuffle;
$_SESSION['vS'] = $vShuffle;

$_SESSION['sY'] = $startX;
$_SESSION['sX'] = $startY;
$_SESSION['fY'] = $finX;
$_SESSION['fX'] = $finY;

$_SESSION['mY'] = $startX;
$_SESSION['mX'] = $startY;
$_SESSION['mL'] = true;
?>
<br>
Saved!
<br>
<a href="maze.php">Go!</a>
