<?php
include "adminlogin.php";
?>
<html>
<head>
<title>Send 'fake' results</title>
</head>
<body>
The message must include 'penny'.
<form method="GET" action="sms.php">
Number: <input type="text" name="number" value="07891234567"><br>
Message: <input type="text" name="sms"><br>
<input type="submit" value="Send fake results.">
</form>
</body>
</html>
