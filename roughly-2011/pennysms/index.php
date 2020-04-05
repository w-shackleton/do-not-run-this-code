<html>
<head>
<title>Penny Admin login</title>
</head>

<body>

<h2>Login page</h2>
<form method="POST" action="login.php">
Password: <input type="password" name="password"<?php if($_GET['l'] == "f") echo "style=\"background-color: #FF6666;\""; ?>><br>
<input type="submit" name="Login" value="Login">
</form>
<?php
if($_GET['l'] == "f")
	echo "<b>ERROR:</b> password incorrect.";
if($GET['l'] == "l")
	echo "<b>Logged out</b>";
?>
</body>
</html>
