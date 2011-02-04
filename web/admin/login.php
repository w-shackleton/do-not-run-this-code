<?php 
include "include/header.php";
# ini_set('display_errors',1);
# error_reporting(E_ALL);
session_start();
$_SESSION['admpasswd'] = ""; # Unset password
?>

<div id="container">
	<h1>Pennybottle Web Admin - Login</h1>
	<div id="lh">
		<h2>Login</h2>
		<?php # TODO: Implement some security here with javascript? ?>
		<form id="loginform" method="POST" action="logincheck.php">
			<p>
				Username: <input name="username" type="text" value="admin" size="10" />
			</p>
			<p>
				Password: <input name="passwd" type="password" value="" size="10" />
			</p>
			<?php if(isset($_REQUEST['e'])) echo "<p class=\"wrongpassword\">Wrong password</p>"; ?>
			<input class="submit" name="sub" type="submit" value="Login" />
		</form>
	</div>
</div>

<?php
include "include/footer.php";
?>
