// ----------------------------------------------------------
//                  ADMIN PAGE 
// ----------------------------------------------------------
// Nabeel Danish

// ---------------------------------------------------------
// Scripts for Navbar
// ---------------------------------------------------------
const navToggle = document.querySelector(".nav-toggle");
const links = document.querySelector(".links");
navToggle.addEventListener("click", function () {
	links.classList.toggle("show-links");
});


// ---------------------------------------------------------
// Actual API Connection and Processing
// ---------------------------------------------------------

var spinner = document.getElementById("spinner")
const url = sessionStorage.getItem("url");
const user_id = sessionStorage.getItem("USER_ID");
console.log(user_id)

// Creating a Connection request
var connectionRequest = new XMLHttpRequest();
var progress = document.getElementById("progress")

connectionRequest.open('GET', url + 'hello');
connectionRequest.onload = function () {
	spinner.style.visibility = "hidden"
	progress.innerText = "Connected!"
}
connectionRequest.send();
