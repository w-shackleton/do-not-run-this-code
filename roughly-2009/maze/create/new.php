<?php
if($_POST['uname'] == "" && $_POST['tname'] == "")
{
header('Location: index.php');
die();
}
else
{
}
$creator = (string)$_POST['uname'];
$mazename = (string)$_POST['tname'];
$x = (int)$_POST['x'];
$y = (int)$_POST['y'];
if($x < 5)
{
$x = 5;
}
if($y < 5)
{
$y = 5;
}
if($x > 30)
{
$x = 30;
}
if($y > 30)
{
$y = 30;
}
?>
<html>
<head>
<title>Create a Maze - 2 of 3 - Create Maze</title>
<script type="text/javascript">
function changeIt(Ob)
{
	c = document.getElementById(Ob);
	p = document.location.href;
	d = p.substr(0,p.lastIndexOf('/')+1);
	
	x = parseInt(Ob.substr(0,Ob.indexOf('-')));
	y = parseInt(Ob.substr(Ob.indexOf('-')+1,Ob.length));
	switch(c.src)
	{
		case d+"img/0.PNG":
			c.src = d+"img/1.PNG";
			document.f[Ob].value = 1;
			break;
		case d+"img/1.PNG":
			c.src = d+"img/2.PNG";
			document.f[Ob].value = 2;
			if(y == 0)
			{
				c.src = d+"img/3.PNG";
				document.f[Ob].value = 3;
			}
			break;
		case d+"img/2.PNG":
			c.src = d+"img/3.PNG";
			document.f[Ob].value = 3;
			break;
		case d+"img/3.PNG":
			c.src = d+"img/0.PNG";
			document.f[Ob].value = 0;
			if(y == 0)
			{
				c.src = d+"img/1.PNG";
				document.f[Ob].value = 1;
			}
			if(x == 0)
			{
				c.src = d+"img/2.PNG";
				document.f[Ob].value = 2;
			}
			if(x == 0 && y == 0)
			{
				c.src = d+"img/3.PNG";
				document.f[Ob].value = 3;
			}
			break;
	}
}
</script>
<style type="text/css">
#p img{
margin:0px;
padding:0px;
}
</style>
</head>
<body>
<form method="POST" action="newSubmit.php" name="f">
<p id="t"><?php
for($j=0;$j<$y;$j++)
{
	for($i=0;$i<$x;$i++)
	{
		?><img src="img/3.PNG" onClick="changeIt('<?php echo $i . "-" . $j; ?>')" id="<?php echo $i . "-" . $j; ?>"><input type="hidden" name="<?php echo $i . "-" . $j; ?>" value="3"><?php
	}
?><img src="img/side.PNG"><br><?php
}
for($j=0;$j<$x;$j++)
{
?><img src="img/base.PNG"><?php
}
?><img src="img/corner.PNG"></p>
<input type="hidden" name="x" value="<?php echo $x; ?>">
<input type="hidden" name="y" value="<?php echo $y; ?>">
<input type="hidden" name="uname" value="<?php echo $creator; ?>">
<input type="hidden" name="tname" value="<?php echo $mazename; ?>">
<input type="submit" value="Submit">
</form>
</body>
</html>