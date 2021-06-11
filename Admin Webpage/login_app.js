// ----------------------------------------------------------
//                  ADMIN PAGE 
// ----------------------------------------------------------
// Nabeel Danish

// ---------------------------------------------------------
// Actual API Connection and Processing
// ---------------------------------------------------------


var url = sessionStorage.getItem("url")
var connectionError = document.getElementById("connection_error")
var url_field = document.getElementById("url_field")
btn_connect = document.getElementById("btn_connect")

if (url) {
	connectionError.innerText = "Connected!"
	connectionError.style.color = "green"
	url_field.style.visibility = "hidden"
	connect_msg = document.getElementById("connect_msg")
	connect_msg.innerText = "You are connected to SAVAPI and can now login!"
	btn_connect.style.visibility = "hidden"
}

// Setting Connect onCLick function
btn_connect.onclick = function() {
	var connect_loader = document.getElementById("connect_loader")
	connect_loader.style.visibility = "visible"

	// Input Field Checks
	url = url_field.value

	if (!url) {
		connectionError.innerText = "Error! Please enter a URL!"
		connect_loader.style.visibility = "hidden"
		return
	}
	if (!url.match("https:\/\/[0-9a-zA-Z]+.ngrok.io\/")) {
		connectionError.innerText = "Error! Not a valid SAVAPI URL!"
		connect_loader.style.visibility = "hidden"
		return
	}
	connectionError.innerText = "Connecting ..."
	connectionError.style.color = "green"

	// Creating a Connection request
	var connectionRequest = new XMLHttpRequest();
	connectionRequest.open('GET', url + 'hello');

	// Setting Connection onLoad
	connectionRequest.onload = function () {
		if (connectionRequest.status >= 200 && connectionRequest.status < 400) {
			connectionError.innerText = "Connected!"
			sessionStorage.setItem("url", url);
			url_field.style.visibility = "hidden"
			connect_msg = document.getElementById("connect_msg")
			connect_msg.innerText = "You are connected to SAVAPI and can now login!"
			btn_connect.style.visibility = "hidden"
			connect_loader.style.visibility = "hidden"
		} else {
			connectionError.innerText = "Error! Connection cannot be established!"
			connectionError.style.color = "red"
			connect_loader.style.visibility = "hidden"
		}
	}

	// Handling Network Errors
	connectionError.onerror = function() {
		connectionError.innerText = "Error! Connection cannot be established!"
		connectionError.style.color = "red"
		connect_loader.style.visibility = "hidden"
	}

	// Sending Request
	connectionRequest.send();
}

// ---------------------------------------------
// 	LOGIN FORM AND FUNCTIONS
// ---------------------------------------------
var login_loader = document.getElementById("login_loader")
function login(form) {

	// Getting Form Data
	var username = form.uname.value;
	var password = form.psw.value;
	var login_err = document.getElementById("login_err")
	login_loader.style.visibility = "visible"

	// Getting URL
	url = sessionStorage.getItem("url")
	console.log(url)
	if (!url || url == "") {
		login_err.innerText = "Error! You are not connected to SAVAPI!"
		login_loader.style.visibility = "hidden"
		return
	}

	// Username Checks
	if (!username || !username.match("(?:www.)?(?:[a-zA-Z0-9])+@([a-zA-Z].?)+(?:.com|.pk)")) {
		login_err.innerText = "Error! Please Enter a Valid Username!"
		login_loader.style.visibility = "hidden"
		return
	}
	
	// Password Checks
	if (!password) {
		login_err.innerText = "Error! Please Enter your Password!"
		login_loader.style.visibility = "hidden"
		return
	}

	// Calling Login
	var loginRequest = new XMLHttpRequest();
	loginRequest.open("POST", url + 'login_admin');
	loginRequest.setRequestHeader('Content-Type', 'application/json');

	loginRequest.onload = function() {
		var data = this.response;
		var user_id = data['userID'];
		var cookie = this.getResponseHeader("Set-Cookie")
		console.log(user_id)
		console.log(data);
		console.log(cookie)
		if (data != null) {
			sessionStorage.setItem("USER_ID", data['USER_ID']);
			window.location.replace("index.html");
			login_loader.style.visibility = "hidden"
		}
		login_loader.style.visibility = "hidden"
	}

	loginRequest.send(JSON.stringify({"username":username, "password":password}));
}
