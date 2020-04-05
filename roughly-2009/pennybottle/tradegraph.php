<?php
error_reporting(0);
include "scripts/serverlogin.php";
function arrayTop($array) {
	$top = $array[0];
	for($i=0;$i<count($array);$i++)
	{
		if($array[$i] > $top)
		{
			$top = $array[$i];
		}
	}
	return $top;
}
function arrayBottom($array) {
	$bottom = $array[0];
	for($i=0;$i<count($array);$i++)
	{
		if($array[$i] < $bottom)
		{
			$bottom = $array[$i];
		}		
	}
	return $bottom;
}
if($num==1){
$query = "SELECT * FROM trades WHERE sold='1' ORDER BY id LIMIT 100";
$result = mysql_query($query);
$numrows = mysql_numrows($result);
$array = array();
$numarray = array();
for($i = 0;$i<$numrows;$i++)
{
$array[$i] = mysql_result($result,$i,val);
$numarray[$i] = mysql_result($result,$i,quan);
}
$h = $_GET['h'];
if($h == null)
{
$h = 300;
}
$w = $_GET['w'];
if($w == null)
{
$w = 600;
}
$border = 10;
$fontsize = 2;

$top = arrayTop($array);
$bottom = arrayBottom($array);
$range = $top - $bottom;
$innerh = $h - $border - $border;
$innerw = $w - $border - $border;
$horizdist = $innerw / count($array);
$imageFontWidth = imageFontWidth($fontsize);
$imageFontHeight = imageFontHeight($fontsize);

$halfway = round(($top + $bottom) / 2);
$quarterway = round(($halfway + $bottom) / 2);
$threequarterway = round(($top + $halfway) / 2);


$img = imageCreate($w,$h);

$background = imageColorAllocate($img,255,255,255);
$edge = imageColorAllocate($img,0,0,0);
$dataline = imageColorAllocate($img,0,255,0);
$datatext = imageColorAllocate($img,0,0,0);
$datalabels = imageColorAllocate($img,0xFF,0x99,0x33);

imageSetStyle($img,array($edge,$background));

//<EDGE>
imageLine($img,$border,$border,$border,$h-$border,$edge);
imageLine($img,$border,$h-$border,$w-$border,$h-$border,$edge);

imageLine($img,$border,$border,$w - $border,$border,IMG_COLOR_STYLED);
imageLine($img,$border,$h - ((0 - $bottom) / $range * $innerh) - $border,$w - $border,$h - ((0 - $bottom) / $range * $innerh) - $border,$edge);
//</EDGE>

//<TOP AND BOTTOM NUMBERS>
imageString($img,$fontsize,$border - ($imageFontWidth / 2 * strlen($bottom)),$innerh + $border - ($imageFontHeight / 2),$bottom,$datalabels);
imageString($img,$fontsize,$border - ($imageFontWidth / 2 * strlen($top)),$border - ($imageFontHeight / 2),$top,$datalabels);

//</TOP AND BOTTOM NUMBERS>

//<MIDDLE NUMBERS AND LINES>
$tempheight = $h - (($halfway - $bottom) / $range * $innerh) - $border;
imageString($img,$fontsize,$border - ($imageFontWidth / 2 * strlen($halfway)),$tempheight - ($imageFontHeight / 2),$halfway,$datalabels);
imageLine($img,$border,$tempheight,$w - $border,$tempheight,IMG_COLOR_STYLED);

$tempheight = $h - (($quarterway - $bottom) / $range * $innerh) - $border;
imageString($img,$fontsize,$border - ($imageFontWidth / 2 * strlen($quarterway)),$tempheight - ($imageFontHeight / 2),$quarterway,$datalabels);
imageLine($img,$border,$tempheight,$w - $border,$tempheight,IMG_COLOR_STYLED);

$tempheight = $h - (($threequarterway - $bottom) / $range * $innerh) - $border;
imageString($img,$fontsize,$border - ($imageFontWidth / 2 * strlen($threequarterway)),$tempheight - ($imageFontHeight / 2),$threequarterway,$datalabels);
imageLine($img,$border,$tempheight,$w - $border,$tempheight,IMG_COLOR_STYLED);
//</MIDDLE NUMBERS AND LINES>

//<LINE AND NUMBERS>
$coordx = array();
$coordy = array();
for($i=0;$i<count($array);$i++)
{
	$coordx[$i] = $i * $horizdist + $border;
	$temp = ($array[$i] - $bottom) / ($top - $bottom);
	$coordy[$i] = $h - ($temp * $innerh) - $border;
}
for($i=0;$i<count($array)-1;$i++){
	imageLine($img,$coordx[$i],$coordy[$i],$coordx[$i+1],$coordy[$i+1],$dataline);
}
for($i=0;$i<count($array);$i++){
	$xpos = $coordx[$i] - ($imageFontWidth / 2 * strlen($numarray[$i]));
	$ypos = $coordy[$i] - ($imageFontHeight / 2);
	imageString($img,$fontsize,$xpos,$ypos,$numarray[$i],$datatext);
}
//</LINE AND NUMBERS>

//<WRITE>
header("Content-Type: image/png");
header( 'Expires: Mon, 26 Jul 1997 05:00:00 GMT' );
header( 'Last-Modified: ' . gmdate( 'D, d M Y H:i:s' ) . ' GMT' );
header( 'Cache-Control: no-store, no-cache, must-revalidate' );
header( 'Cache-Control: post-check=0, pre-check=0', false );
header( 'Pragma: no-cache' );  

imagePNG($img);
imageDestroy($img); 
//</WRITE>

}

else{
header('Location: home.php');
}
include "scripts/serverloginend.php";
?>
