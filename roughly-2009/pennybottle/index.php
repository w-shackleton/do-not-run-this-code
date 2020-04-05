<?php
include "scripts/serverlogin.php";

if($num==1){
include "include/header.php";
?>
<img src="paused.gif" id="pausedimg">
<img src="paused.gif" id="graphbgimg">
<img src="pausedicon.gif" id="pausedicon">
<img src="pausedicon.gif" id="pausedicon2">
<img src="tradegraph.png" id="graph" onclick="javascript:graphclose()">
<div id="metacontainer">
<div id="stat">
<div id="stat2"></div>
<br>
<a href="logout.php">Logout.</a>
<br>
<br>
<a href="javascript:graphopen()">
<img src="graph.png" border="0">View Graph</a>
<hr>
<form name="changeval">
Change your idea of the bottle value. This updates the leaderboard.<br><input type="text" size="2" name="val" style="font-family:arial;"onKeyPress="return dekCHANGE(event)">
<input type="button" value="Change" onclick="changevalfunc()">
</form>
<br>
<br>
</div>
<div id="im">
<div id="imbox">
</div>
<div id="imsend">
<form name="postacomment"><input type="text" name="text" size="30" style="font-family:arial;"onKeyPress="return dekIM(event)"><input type="button" value="Post" onclick="postcomment()"></form>
</div>
</div>
<?php
$query="SELECT * FROM htmlmessage";
$result=mysql_query($query);
echo str_replace("\\","",mysql_result($result,0,"message"));
?>
</div>
<div id="container">
	<div id="sellcon">
		<div id="sell">
			Offer to sell penny bottles:
			<?php include "include/sell.php"; ?>
		</div>
		<div id="buyqsstat">
			.
		</div>
		<div id="buyqs">
		</div>
	</div>
	<div id="buycon">
		<div id="buy">
			Bid for penny bottles:
			<?php include "include/buy.php"; ?>
		</div>
		<div id="sellqsstat">
			.
		</div>
		<div id="sellqs">
		</div>
	</div>
	<div id="comptrades">Recent Trades:</div>
	<?php
	if(mysql_result(mysql_query("SELECT * FROM enabled"),0,"e") == 1)
	{
	?>
	<div id="lendmcon">
		<div id="lendm">
			Offer to borrow money:
			<form name="lendm">
				Receive now (p): <input type="input" name="borrow" size="4" onKeyPress="return dekLENDM(event)"><br>
				Repay at End (p): <input type="input" name="ret" size="4" onKeyPress="return dekLENDM(event)"><br>
				<input type="button" value="Borrow" onclick="putlendm()">
				<input type="radio" name="thing" value="p">p
				<input type="radio" name="thing" value="b">bt.
				
				<div id="resulttextlendm">.</div>
			</form>
		</div>
		<div id="lendmstat">
			.
		</div>
		<div id="lendqs">
		</div>
	</div>
	<?php
	}
	?>
</div>
<div id="graphtext">
Graph shows 100 results. Line shows pence per bottle. Number shows amount of bottles
<a href="javascript:graphclose()">Close.</a>
</div>
<?php
}

else{
header('Location: home.php');
}
include "include/footer.php";
include "scripts/serverloginend.php";
?>
