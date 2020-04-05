<?php
session_start();
if(!isset($_SESSION['mL']))
{
	die("Maze Not Loaded");
}

//SESSION VARS
$tilesX = (int)$_SESSION['dX'];
$tilesY = (int)$_SESSION['dY'];

$startX = $_SESSION['sX'];
$startY = $_SESSION['sY'];
$finX = $_SESSION['fX'];
$finY = $_SESSION['fY'];

$meX = $_SESSION['mX'];
$meY = $_SESSION['mY'];

$hS = $_SESSION['hS'];
$vS = $_SESSION['vS'];

$maze = $_SESSION['maze'];
$mazeid = $_SESSION['mazeid'];
//END SESSION VARS

//CALC VARS
$border = 10;
$meborder = 5;
$innerborder = 2;
$squareSize = 20;
$osquareSize = $squareSize + $innerborder + $innerborder;
$imgX = ($tilesX * $osquareSize) + ($border * 2);
$imgY = ($tilesY * $osquareSize) + ($border * 2);
//END CALC VARS

//IMG CONSTRUCT
$im = imageCreate($imgX,$imgY);

$colBG = imageColorAllocate($im,0xFF,0xFF,0xAA);
$colBorder = imageColorAllocate($im,0x00,0x00,0x00);
$colBorderB = imageColorAllocate($im,0xDD,0xDD,0xFF);
$colStart = imageColorAllocate($im,0xFF,0x00,0x00);
$colFin = imageColorAllocate($im,0x00,0xFF,0x00);
$colMe = imageColorAllocate($im,0x00,0x00,0x00);
for($j = 0;$j < $tilesY;$j++)
{
	for($i = 0;$i < $tilesX;$i++)
	{
		$i2 = $hS[$j][$i];
		$j2 = $vS[$j][$i2];
		$l = false;
		$t = false;
		$r = false;
		$b = false;
		$x1 = $border + $osquareSize * $i2 + $innerborder;
		$y1 = $border + $osquareSize * $j2 + $innerborder;
		$x2 = $border + $osquareSize * $i2 + $innerborder + $squareSize;
		$y2 = $border + $osquareSize * $j2 + $innerborder + $squareSize;
		
		if($startX == $i && $startY == $j)
		{
			imageFilledRectangle($im,$x1,$y1,$x2,$y2,$colStart);
		}
		if($finX == $i && $finY == $j)
		{
			imageFilledRectangle($im,$x1,$y1,$x2,$y2,$colFin);
		}
		if($meX == $i && $meY == $j)
		{
			imageFilledRectangle($im,$x1 + $meborder,$y1 + $meborder,$x2 - $meborder,$y2 - $meborder,$colMe);
		}
		switch($maze[$j][$i])
		{
			case 0:
				break;
			case 1:
				$t = true;
				break;
			case 2:
				$l = true;
				break;
			case 3:
				$t = true;
				$l = true;
				break;
		}
		if(isset($maze[$j][$i+1]))
		{
			switch($maze[$j][$i+1])
			{
				case 2:
				case 3:
					$r = true;
					break;
			}
		}
		else
		{
			$r = true;
		}
		if(isset($maze[$j+1][$i]))
		{
			switch($maze[$j+1][$i])
			{
				case 1:
				case 3:
					$b = true;
					break;
			}
		}
		else
		{
			$b = true;
		}
		imageRectangle($im,$x1,$y1,$x2,$y2,$colBorderB);
		if($l)
		{
			imageLine($im,$x1,$y1,$x1,$y2,$colBorder);
		}
		if($t)
		{
			imageLine($im,$x1,$y1,$x2,$y1,$colBorder);
		}
		if($r)
		{
			imageLine($im,$x2,$y1,$x2,$y2,$colBorder);
		}
		if($b)
		{
			imageLine($im,$x1,$y2,$x2,$y2,$colBorder);
		}
	}
}
//END IMG CONSTRUCT

//IMG SEND
header('Content-type: image/png');
imagePNG($im);
imageDestroy($im); 
//END IMG SEND
?>