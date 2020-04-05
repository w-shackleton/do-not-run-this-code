<?php
include "scripts/serverlogin.php";

if($num==1){
?>
<html>
<head>
<title>Pennies Admin - ARE YOU SURE?</title>
<link rel="stylesheet" href="style.css" type="text/css" />
</head>
<body>

Are you SURE you want to pay all debts?

<form action="cancel_debts.php" method="POST">
<input type="submit" value="YES">
<a href="user_manage.php">NO</a>
</form>
<?php
}
include "scripts/serverloginend.php";
?>
</body>
</html>