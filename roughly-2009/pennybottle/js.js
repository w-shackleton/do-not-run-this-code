/*@cc_on @if (@_win32 && @_jscript_version >= 5) if (!window.XMLHttpRequest)
window.XMLHttpRequest = function() { return new ActiveXObject('Microsoft.XMLHTTP') }
@end @*/
function putsale() {
var url = "putsale.php";
var http = new XMLHttpRequest();
var params = "quan=" + document.sell.quan.value + "&val=" + document.sell.val.value;
//alert(params);
document.sell.quan.value = "";
document.sell.val.value = "";
http.open("POST", url, true);

http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
http.setRequestHeader("Content-length", params.length);
http.setRequestHeader("Connection", "close");

http.onreadystatechange = function() {//Call a function when the state changes.
	if(http.readyState == 4 && http.status == 200) {
		//alert(http.responseText);
		if(http.responseText == "D")
		{
			document.getElementById("resulttext").innerHTML = "Done.";
		}
		if(http.responseText == "T")
		{
			document.getElementById("resulttext").innerHTML = "Not enough money.";
		}
		if(http.responseText == "N")
		{
			document.getElementById("resulttext").innerHTML = "An error occurred.";
		}
		if(http.responseText == "P")
		{
			document.getElementById("resulttext").innerHTML = "GAME PAUSED.";
			pausegame();
		}
		setTimeout("document.getElementById(\"resulttext\").innerHTML = \".\"",900);
	}
}
http.send(params);
}
function putbuy() {
var url = "putbuy.php";
var http = new XMLHttpRequest();
var params = "quan=" + document.buy.quan.value + "&val=" + document.buy.val.value;
//alert(params);
document.buy.quan.value = "";
document.buy.val.value = "";
http.open("POST", url, true);

http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
http.setRequestHeader("Content-length", params.length);
http.setRequestHeader("Connection", "close");

http.onreadystatechange = function() {//Call a function when the state changes.
	if(http.readyState == 4 && http.status == 200) {
		//alert(http.responseText);
		if(http.responseText == "D")
		{
			document.getElementById("resulttextbuy").innerHTML = "Done.";
		}
		if(http.responseText == "T")
		{
			document.getElementById("resulttextbuy").innerHTML = "Not enough money.";
		}
		if(http.responseText == "N")
		{
			document.getElementById("resulttextbuy").innerHTML = "An error occurred.";
		}
		if(http.responseText == "P")
		{
			document.getElementById("resulttextbuy").innerHTML = "GAME PAUSED.";
			pausegame();
		}
		setTimeout("document.getElementById(\"resulttextbuy\").innerHTML = \".\"",900);
	}
}
http.send(params);
}

function putlendm() {
var url = "putlendm.php";
var http = new XMLHttpRequest();
var ch = 1;
if(document.lendm.thing[1].checked)
{
ch = 2;
}
var params = "borrow=" + document.lendm.borrow.value + "&return=" + document.lendm.ret.value + "&bottle=" + ch;
//alert(document.lendm.thing[1].checked);
document.lendm.borrow.value = "";
document.lendm.ret.value = "";
http.open("POST", url, true);

http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
http.setRequestHeader("Content-length", params.length);
http.setRequestHeader("Connection", "close");

http.onreadystatechange = function() {//Call a function when the state changes.
	if(http.readyState == 4 && http.status == 200) {
		//alert(http.responseText);
		if(http.responseText == "D")
		{
			document.getElementById("resulttextlendm").innerHTML = "Done.";
		}
		if(http.responseText == "T")
		{
			document.getElementById("resulttextlendm").innerHTML = "Not enough money.";
		}
		if(http.responseText == "N")
		{
			document.getElementById("resulttextlendm").innerHTML = "An error occurred.";
		}
		if(http.responseText == "P")
		{
			document.getElementById("resulttextlendm").innerHTML = "GAME PAUSED.";
			pausegame();
		}
		setTimeout("document.getElementById(\"resulttextlendm\").innerHTML = \".\"",900);
	}
}
http.send(params);
}

getinfo();
function getinfo() {
var url = "getinfo.php";
var http = new XMLHttpRequest();
http.open("GET", url, true);

http.onreadystatechange = function() {//Call a function when the state changes.
	if(http.readyState == 4 && http.status == 200) {
		document.getElementById("stat2").innerHTML = http.responseText;
		setTimeout("getinfo()",700);
	}
}
http.send(null);
}
getbuyqs();
function getbuyqs() {
var url = "getqs.php";
var http = new XMLHttpRequest();
http.open("GET", url, true);

http.onreadystatechange = function() {//Call a function when the state changes.
	if(http.readyState == 4 && http.status == 200) {
		
		var res = http.responseText;
		var index = res.indexOf("    TABLE SEPERATOR  ");
		var index2 = res.indexOf("  2 TABLE SEPERATOR  ");
		if(index != -1)
		{
			document.getElementById("buyqs").innerHTML = res.substring(0,index);
			document.getElementById("sellqs").innerHTML = res.substring(index + 22,index2);
			if(document.getElementById("lendqs") != null)
			{
				document.getElementById("lendqs").innerHTML = res.substring(index2 + 22);
			}
		}
		if(http.responseText=="Paused"){
			pausegame();
		}
		else{
			unpausegame();
		}
		setTimeout("getbuyqs()",300);
	}
}
http.send(null);
}

function buytrade(id) {
var url = "buysale.php";
var http = new XMLHttpRequest();
var params = "bid=" + id;
http.open("POST", url, true);

http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
http.setRequestHeader("Content-length", params.length);
http.setRequestHeader("Connection", "close");

http.onreadystatechange = function() {//Call a function when the state changes.
	if(http.readyState == 4 && http.status == 200) {
		//alert(http.responseText);
		if(http.responseText == "D")
		{
			document.getElementById("sellqsstat").innerHTML = "Done.";
		}
		if(http.responseText == "T")
		{
			document.getElementById("sellqsstat").innerHTML = "Not enough money.";
		}
		if(http.responseText == "N")
		{
			document.getElementById("sellqsstat").innerHTML = "An error occurred.";
		}
		if(http.responseText == "S")
		{
			document.getElementById("sellqsstat").innerHTML = "Trade no longer available.";
		}
		if(http.responseText == "P")
		{
			document.getElementById("sellqsstat").innerHTML = "GAME PAUSED.";
			pausegame();
		}
		setTimeout("document.getElementById(\"sellqsstat\").innerHTML = \".\"",900);
	}
}
document.getElementById("sellqsstat").innerHTML = "Clearing...";
http.send(params);
}
function buybuy(id) {
var url = "buybuy.php";
var http = new XMLHttpRequest();
var params = "bid=" + id;
http.open("POST", url, true);

http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
http.setRequestHeader("Content-length", params.length);
http.setRequestHeader("Connection", "close");

http.onreadystatechange = function() {//Call a function when the state changes.
	if(http.readyState == 4 && http.status == 200) {
		//alert(http.responseText);
		if(http.responseText == "D")
		{
			document.getElementById("buyqsstat").innerHTML = "Done.";
		}
		if(http.responseText == "T")
		{
			document.getElementById("buyqsstat").innerHTML = "Not enough money.";
		}
		if(http.responseText == "N")
		{
			document.getElementById("buyqsstat").innerHTML = "An error occurred.";
		}
		if(http.responseText == "S")
		{
			document.getElementById("buyqsstat").innerHTML = "Bid no longer available.";
		}
		if(http.responseText == "P")
		{
			document.getElementById("buyqsstat").innerHTML = "GAME PAUSED.";
			pausegame();
		}
		setTimeout("document.getElementById(\"buyqsstat\").innerHTML = \".\"",900);
	}
}
document.getElementById("buyqsstat").innerHTML = "Clearing...";
http.send(params);
}
function buyborrow(id) {
var url = "buylend.php";
var http = new XMLHttpRequest();
var params = "lid=" + id;
http.open("POST", url, true);

http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
http.setRequestHeader("Content-length", params.length);
http.setRequestHeader("Connection", "close");

http.onreadystatechange = function() {//Call a function when the state changes.
	if(http.readyState == 4 && http.status == 200) {
		//alert(http.responseText);
		if(http.responseText == "D")
		{
			document.getElementById("lendmstat").innerHTML = "Done.";
		}
		if(http.responseText == "T")
		{
			document.getElementById("lendmstat").innerHTML = "Not enough money.";
		}
		if(http.responseText == "N")
		{
			document.getElementById("lendmstat").innerHTML = "An error occurred.";
		}
		if(http.responseText == "S")
		{
			document.getElementById("lendmstat").innerHTML = "Bid no longer available.";
		}
		if(http.responseText == "P")
		{
			document.getElementById("lendmstat").innerHTML = "GAME PAUSED.";
			pausegame();
		}
		setTimeout("document.getElementById(\"lendmstat\").innerHTML = \".\"",900);
	}
}
document.getElementById("lendmstat").innerHTML = "Clearing...";
http.send(params);
}

function getrectrades() {
var url = "getrectrades.php";
var http = new XMLHttpRequest();
http.open("GET", url, true);

http.onreadystatechange = function() {//Call a function when the state changes.
	if(http.readyState == 4 && http.status == 200) {
		
		document.getElementById("comptrades").innerHTML = http.responseText + "<br>To change bottle value in leaderboard,see \"change your idea\"";
		if(http.responseText=="Paused"){
			pausegame();
			document.getElementById("comptrades").innerHTML += "<br><br><br><br><br><br><br><br><br><br><br>";
		}
		else{
			unpausegame();
		}
		setTimeout("getrectrades()",1000);
	}
}
http.send(null);
}
getrectrades();

var ispaused = 0;
function pausegame(){
	if(ispaused==0) {
		document.getElementById("pausedimg").style.visibility="visible";
		document.getElementById("pausedicon").style.visibility="visible";
		document.getElementById("pausedicon2").style.visibility="visible";
		ispaused=1;
	}
}
function unpausegame() {
	if(ispaused==1) {
		
		document.getElementById("pausedimg").style.visibility="hidden";
		document.getElementById("pausedicon").style.visibility="hidden";
		document.getElementById("pausedicon2").style.visibility="hidden";
		ispaused=0;
	}
}


function postcomment() {
var url = "postcomment.php";
var http = new XMLHttpRequest();
var params = "message=" + document.postacomment.text.value;
document.postacomment.text.value = "";
http.open("POST", url, true);

http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
http.setRequestHeader("Content-length", params.length);
http.setRequestHeader("Connection", "close");

http.onreadystatechange = function() {//Call a function when the state changes.
	if(http.readyState == 4 && http.status == 200) {
	}
}
http.send(params);
}

getcomment();
function getcomment() {
var url = "getcomment.php";
var http = new XMLHttpRequest();
http.open("GET", url, true);

http.onreadystatechange = function() {//Call a function when the state changes.
	if(http.readyState == 4 && http.status == 200) {
		
		document.getElementById("imbox").innerHTML = http.responseText;
		
		setTimeout("getcomment()",500);
	}
}
http.send(null);
}

function graphopen() {
document.getElementById("graph").style.visibility="visible";
document.getElementById("graphtext").style.visibility="visible";
document.getElementById("graph").src = "tradegraph.png?rand=" + Math.random();
document.getElementById("graphbgimg").style.visibility="visible";
}
function graphclose() {
document.getElementById("graph").style.visibility="hidden";
document.getElementById("graphtext").style.visibility="hidden";
document.getElementById("graphbgimg").style.visibility="hidden";
}


function dekIM(e) {
	var key;
	if(window.event)
	{
		key = window.event.keyCode;     //IE
	}
	else
	{
		key = e.which;     //firefox
	}
	if(key == 13)
	{
		postcomment();
		return false;
	}
	else
	{
		return true;
	}
}
function dekBUY(e) {
	var key;
	if(window.event)
	{
		key = window.event.keyCode;     //IE
	}
	else
	{
		key = e.which;     //firefox
	}
	if(key == 13)
	{
		putbuy();
		return false;
	}
	else
	{
		return true;
	}
}
function dekSELL(e) {
	var key;
	if(window.event)
	{
		key = window.event.keyCode;     //IE
	}
	else
	{
		key = e.which;     //firefox
	}
	if(key == 13)
	{
		putsale();
		return false;
	}
	else
	{
		return true;
	}
}
function dekCHANGE(e) {
	var key;
	if(window.event)
	{
		key = window.event.keyCode;     //IE
	}
	else
	{
		key = e.which;     //firefox
	}
	if(key == 13)
	{
		changevalfunc();
		return false;
	}
	else
	{
		return true;
	}
}
function dekLENDM(e) {
	var key;
	if(window.event)
	{
		key = window.event.keyCode;     //IE
	}
	else
	{
		key = e.which;     //firefox
	}
	if(key == 13)
	{
		putlendm();
		return false;
	}
	else
	{
		return true;
	}
}

function changevalfunc() {
var url = "changebottleval.php?val=" + document.changeval.val.value;
var http = new XMLHttpRequest();
http.open("GET", url, true);
http.send(null);
}

