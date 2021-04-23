// ----------------------------------------------------------
//                  ADMIN PAGE 
// ----------------------------------------------------------
// Nabeel Danish

// ---------------------------------------------------------
// Actual API Connection and Processing
// ---------------------------------------------------------

var url = window.prompt("Copy the URL Link (make sure you include '/' at the end)");

// Creating a Connection request
var connectionRequest = new XMLHttpRequest();

connectionRequest.open('GET', url + 'hello');
connectionRequest.onload = function () {
	alert("Connection Established");
}
connectionRequest.send();

function login(form) {

	// Getting Form Data
	var username = form.uname.value;
	var password = form.psw.value;

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
			alert("Login Successful!");
			sessionStorage.setItem("url", url);
			sessionStorage.setItem("USER_ID", data['USER_ID']);
			window.location.replace("index.html");
		}
	}

	loginRequest.send(JSON.stringify({"username":username, "password":password}));
}