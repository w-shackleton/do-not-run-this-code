<?php
include "adminlogin.php";

header('Content-type: text/csv');
header('Content-Disposition: attachment; filename="Penny raw output ' . date("Y-m-d H:i:s") . '.csv"');
$fp = fopen("stringdump.csv","r");
echo fread($fp,filesize("stringdump.csv"));
fclose($fp);
?>
