<?php
include "scripts/serverlogin.php";

if($num==1){
?>
<html>
<head>
<title>Pennies Admin - ARE YOU SURE?</title>
<link rel="stylesheet" href="style.css" type="text/css" />
<script type="text/javascript" charset="utf-8" src="js.js"></script>
<script src="sorttable.js"></script>
</head>
<body>

Are you SURE you want to change all users to 
<?php
echo $_POST['bottle'];
?>
 bottles and 
<?php
echo $_POST['money'];
?>
 money?<br>
<form action="change_amts.php" method="POST">
<input type="hidden" value="<?php echo $_POST['bottle']; ?>" name="bottle">
<input type="hidden" value="<?php echo $_POST['money']; ?>" name="money">
<input type="submit" value="YES">
<a href="user_manage.php">NO</a>
</form>
<?php
}
include "scripts/serverloginend.php";
?>
</body>
</html>