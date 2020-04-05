<?php
include 'serverlogin.php';
$x = stripslashes($_POST['x']);
$x = mysql_real_escape_string($x);
$y = stripslashes($_POST['y']);
$y = mysql_real_escape_string($y);
$f = $y . "," . $x . ";";
$x = $x;
$y = $y;

$ip=@$REMOTE_ADDR; 
for($j = 0;$j < $y;$j++)
{
	for($i = 0;$i < $x;$i++)
	{
		$num = "$i-$j";
		$m = "3";
		if(isset($_POST[$num]))
		{
			$m = $_POST[$num];
			if($i + 1 == $x)
			{
				$f =  $f . $m;
			}
			else
			{
				$f = $f . $m . ",";
			}
		}
	}
	
	if($j + 1 == $y)
	{
	}
	else
	{
		$f = $f . ";";
	}
}
$f = mysql_real_escape_string($f);
$uname = stripslashes($_POST['uname']);
$uname = mysql_real_escape_string($uname);
$tname = stripslashes($_POST['tname']);
$tname = mysql_real_escape_string($tname);

$ip = stripslashes($ip);
$ip = mysql_real_escape_string($ip);
$query = "INSERT INTO mazes VALUES ('','$f','$tname','$uname','$ip','','0','0','0','0')";
mysql_query($query);
$query = "SELECT id FROM mazes ORDER BY id DESC";
$result = mysql_query($query);
$id = mysql_result($result,0,id);
include 'serverloginend.php';
?>
Saved!<br>
Play <a href="../loadmaze.php?n=<?php echo $id; ?>">Here</a>