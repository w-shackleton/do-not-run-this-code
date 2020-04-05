<html>
<head>
<title>Login</title>
</head>
<body>

<form action="checklogin.php" method="POST">
<input type="text" name="user"><br>
<input type="password" name="pass"><br>
<input type="submit" value="Login">
</form>
<?php

if($_GET['e'] == "f"){
echo "Login Failed";
}
?>
</body>
</html>