<?php
include "scripts/serverlogin.php";

if($num==1){
function RandomPassword() {
	$chars = "abcdefghijkmnopqrstuvwxyz023456789";
	srand((double)microtime()*1000000);
	$i = 0;
	$pass = '' ;
	while ($i <= 7) {
		$num = rand() % 33;
		$tmp = substr($chars, $num, 1);
		$pass = $pass . $tmp;
		$i++;
	}
	return $pass;
}
?>
<html>
<head>
<title>Add Users</title>
</head>
<body>
<form action="add_usrs_do.php" method="POST">
<input type="hidden" name="amt" value="<?php echo $_POST['amt']; ?>">
<table border="4">
	<tr>
		<td>
			User Name:
		</td>
		<td>
			Password:
		</td>
		<td>
			Bottles:
		</td>
		<td>
			Money:
		</td>
	</tr>
	<?php
	for($i=0;$i<$_POST['amt'];$i++)
	{
	?>
	<tr>
		<td>
			<input type="input" name="user-<?php echo $i; ?>" size="10">
		</td>
		<td>
			<input type="input" name="password-<?php echo $i; ?>" size="10" value="<?php echo RandomPassword(); ?>">
		</td>
		<td>
			
		</td>
		<td>
			
		</td>
	</tr>
	<?php
	}
	?>
	
	<tr>
		<td>
			
		</td>
		<td>
			
		</td>
		<td>
			<input type="input" name="bottles" size="10">
		</td>
		<td>
			<input type="input" name="money" size="10">
		</td>
	</tr>
</table>
<input type="submit" value="Add">
</form>
</body>
</html>
<?php
}
include "scripts/serverloginend.php";
?>
