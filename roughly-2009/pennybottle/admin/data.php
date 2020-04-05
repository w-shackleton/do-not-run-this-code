<?php
include "scripts/serverlogin.php";

if($num==1){
?>
<html>
<head>
<title>Download Data</title>
</head>
<body>
<a href="tradedata.csv">Download Trades Data</a>
<br>
<i>Trade ID</i> is the ID tag of the trade.
<br>
<i>Money from</i> and <i>Money to</i> are the direction of the trade, but for the direction in which the <b>money</b> travelled, these need to be swapped round when <i>bid</i> = 1.
<br>
<i>Bottles</i> and <i>Money Per Bottle</i> Can be multiplied to give the total trade value.
<br>
<i>Sale Status</i> is 0 when incomplete, 1 when completed, and 2 when cancelled.
<br>
The <i>Timestamp</i> is the timestamp at which the trade was completed, 0 if it is incomplete.
<br>
<br>
<a href="userdata.csv">Download User Data</a>
<br>
User ID is the ID of the user.
<br>
Name is the user's login name.
<br>
Disabled is 0 if enabled, 1 if disabled.
<br>
Completed Trades is the amount of trades completed by that user.
<br>
Open Trades is the amount of trades still open from that user.
<br>
Cancelled Trades is the amount of trades that that user cancelled.
<br>
<br>
<a href="borrowdata.csv">Download Lend Data</a>
<br>
Docs needed
<br>
<br>
<a href="index.php">Back</a>
<?php
}
include "scripts/serverloginend.php";
?>
</body>
</html>