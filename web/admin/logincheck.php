<?php
session_start();
$pass = $_REQUEST['passwd'];
$salt = "FJDNnfjdjfduf789ud8ffkdnsfklDUOfn8dhfjkdnhs fdjsfhdsfuhdyf7d8hfjidH&y7fd8sy7fudnJFNsario78w4uy38hfnejknf4jn589234-4i9230iroempqjg8r9g07ur890gum890 h";

$hash = hash("sha1", $salt . $pass, true); # Password hashing to check
for($i = 0; $i < 100; $i++) {
	$hash = hash("sha1", $hash, true);
}
$_SESSION['admpasswd'] = base64_encode($hash); // Save password

echo base64_encode($hash);

header("Location: index.php");
?>
