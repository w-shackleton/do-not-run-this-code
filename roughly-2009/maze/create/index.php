<?php
session_start();
?>
<html>
<head>
<title>Create a Maze - 1 of 3 - Enter Details</title>
</head>
<body>

<form method="POST" action="new.php">
Creator Name:<input type="text" name="uname"><br>
Maze Name:<input type="text" name="tname"><br>
Size:<input type="text" name="x" size="2" value="6">,<input type="text" name="y" size="2" value="6">
<input type="submit" value="Go!" name="sub">
</form>

</body>
</html>
<?php
?>
