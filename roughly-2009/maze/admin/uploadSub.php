<?php
include "serverlogin.php";
if($_FILES['ufile']['size'] >  0)
{
$fileName = $_FILES['ufile']['name'];
$tmpName = $_FILES['ufile']['tmp_name'];
$fileSize = $_FILES['ufile']['size'];
$fileType = $_FILES['ufile']['type'];

$fp = fopen($tmpName, 'r');
$content = fread($fp, filesize($tmpName));
$content = addslashes($content);

if(!get_magic_quotes_gpc())
{
    $fileName = addslashes($fileName);
}
mysql_query("INSERT INTO mazes VALUES ('','$fileType','$fileSize','$content')");
}
else
{
die("Upload Error");
}
include "serverloginend.php";
?>