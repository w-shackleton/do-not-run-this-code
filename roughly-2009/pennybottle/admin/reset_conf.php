<?php
include "scripts/serverlogin.php";

if($num==1){
$delid = mysql_real_escape_string($_GET['id']);
?>
<html>
<head>
<title>Pennies Admin - ARE YOU SURE?</title>
<link rel="stylesheet" href="style.css" type="text/css" />
<script type="text/javascript" charset="utf-8" src="js.js"></script>
<script src="sorttable.js"></script>
</head>
<body>

Are you SURE you want to RESET THE GAME?!?
<form action="reset.php" method="POST">
<input type="submit" value="YES">
<a href="reset.php">NO</a>
</form>
<?php
}
include "scripts/serverloginend.php";
?>
</body>
</html>