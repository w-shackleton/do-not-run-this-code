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

Are you SURE you want to 
<?php
if(mysql_result(mysql_query("SELECT * FROM users WHERE id=$delid"),0,disabled) == 1)
{
echo "enable";
}
else
{
echo "disable";
}
?>
 the user id
<?php
echo $_GET['id'];
?>
, 
<?php
echo mysql_result(mysql_query("SELECT * FROM users WHERE id=$delid"),0,user);
?>?
<form action="del.php" method="POST">
<input type="hidden" value="<?php echo $_GET['id']; ?>" name="user">
<input type="submit" value="YES">
<a href="user_manage.php">NO</a>
</form>
<?php
}
include "scripts/serverloginend.php";
?>
</body>
</html>