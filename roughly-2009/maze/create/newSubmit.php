<html>
<head>
<title>Create a Maze - 3 of 3 - Choose start / finish</title>
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
			c.src = d+"img/0-sel.PNG";
			document.f[Ob].value = 4;
			break;
		case d+"img/0-sel.PNG":
			c.src = d+"img/0.PNG";
			document.f[Ob].value = 0;
			break;
		case d+"img/1.PNG":
			c.src = d+"img/1-sel.PNG";
			document.f[Ob].value = 5;
			break;
		case d+"img/1-sel.PNG":
			c.src = d+"img/1.PNG";
			document.f[Ob].value = 1;
			break;
		case d+"img/2.PNG":
			c.src = d+"img/2-sel.PNG";
			document.f[Ob].value = 6;
			break;
		case d+"img/2-sel.PNG":
			c.src = d+"img/2.PNG";
			document.f[Ob].value = 2;
			break;
		case d+"img/3.PNG":
			c.src = d+"img/3-sel.PNG";
			document.f[Ob].value = 7;
			break;
		case d+"img/3-sel.PNG":
			c.src = d+"img/3.PNG";
			document.f[Ob].value = 3;
			break;
	}
}
</script>
</head>
<body>
<form method="POST" action="newSubmit-go.php" name="f">
<?php
$x = $_POST['x'];
$y = $_POST['y'];
for($j = 0;$j < $y;$j++)
{
	for($i = 0;$i < $x;$i++)
	{
		$num = "$i-$j";
		?><img src="img/<?php echo $_POST[$num]; ?>.PNG" id="<?php echo "$i-$j"; ?>" onClick="changeIt('<?php echo $i . "-" . $j; ?>')"><input type="hidden" name="<?php echo $i . "-" . $j; ?>" value="<?php echo $_POST[$num]; ?>"><?php
	}
	?><img src="img/side.PNG"><br><?php
}
for($j=0;$j<$x;$j++)
{
?><img src="img/base.PNG"><?php
}
?><img src="img/corner.PNG">
<input type="hidden" name="x" value="<?php echo $x; ?>">
<input type="hidden" name="y" value="<?php echo $y; ?>">
<input type="hidden" name="uname" value="<?php echo $_POST['uname']; ?>">
<input type="hidden" name="tname" value="<?php echo $_POST['tname']; ?>">
<br>
<input type="submit" value="Submit">
</form>
</body>
</html>