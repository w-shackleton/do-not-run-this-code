<?php 
include "include/header.php";
# ini_set('display_errors',1);
# error_reporting(E_ALL);

$dbneeded = false;
include "include/serverlogin.php";
?>

<div id="container">
	<h1>Pennybottle Web Admin</h1>
	<div id="lh">
		<h2>CSV Downloads</h2>

		<h3>Users</h3>
		<p>
			<form method="GET" action="dlcsv.php">
				<input type="hidden" name="what" value="users" size="10" />
				Final bottle worth:<input type="text" name="value" value="100" size="10" />
				<input type="submit" value="Download" />
			</form>
		</p>
		<h3>Trades</h3>
		<p>
			<a href="dlcsv.php?what=trades">Download Trades</a>
		</p>
		<h3>Worth guesses</h3>
		<p>
			<a href="dlcsv.php?what=quoteguesses">Download users penny worth guesses</a>
		</p>
	</div>
	<div id="footer">
		<p>
			<a href="login.php">Logout</a>
		</p>
	</div>
</div>

<?php
include "include/serverloginend.php";
include "include/footer.php";
?>
