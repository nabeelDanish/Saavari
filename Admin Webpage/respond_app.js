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

const progressText = document.getElementById("progress");

// ---------------------------------------------------------
// Actual API Connection and Processing
// ---------------------------------------------------------

const url = sessionStorage.getItem("url");
const user_id = sessionStorage.getItem("USER_ID");
const complaint_id = sessionStorage.getItem("COMPLAINT_ID")
console.log(user_id)

// Creating a Connection request
var connectionRequest = new XMLHttpRequest();

connectionRequest.open('GET', url + 'hello');
connectionRequest.onload = function () {
  // alert("Connection Established");
  progressText.innerHTML = "Connected!";
}
connectionRequest.send();

var response_button = document.getElementById("submit_btn")
response_button.onclick = function() {
  var response_text = document.getElementById("response_text").value
  var response_category = document.getElementById("type").value

  // Sending HTTP Request
  var respondRequest = new XMLHttpRequest()
  respondRequest.open('POST', url + 'respondToComplaint');
  respondRequest.setRequestHeader('Content-Type', 'application/json');

  respondRequest.onload = function () {
    var data = JSON.parse(this.response);
    if (data.STATUS == 200) {
      progressText.innerHTML = "Request Sent!";
      location.reload();
    } else {
      progressText.innerHTML = "Request Failed!";
      console.error("failed!");
    }
  }

  var json = JSON.stringify({
    "complaintId" : complaint_id,
    "responseCategory" : response_category,
    "responseMessage" : response_text
  });
  console.log("JSON = " + json);
  respondRequest.send(json);
}