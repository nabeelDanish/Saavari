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
  progressText.innerHTML = "Connected!";
}
connectionRequest.send();


// Main Response Button Send Function
var response_button = document.getElementById("submit_btn")
response_button.onclick = function() {
  // Getting Data
  var response_text = document.getElementById("response_text").value
  var response_category = document.getElementById("type").value
  var radio_btn_yes = document.getElementById("resolved")
  var radio_btn_no = document.getElementById("not_resolved")

  // Input Validation
  if (!response_text) {
    progressText.innerText = "Error! Please Enter a response message" 
    progressText.style.color = "red"
    return
  }
  if (!radio_btn_yes.checked && !radio_btn_no.checked) {
    progressText.innerText = "Error! Please specify whether the problem was solved!" 
    progressText.style.color = "red"
    return
  }
  responseCategory = 0
  if (radio_btn_yes.checked) {
    responseCategory = 200
  } else {
    responseCategory = 100
  }

  // HTTP Request Setup
  var respondRequest = new XMLHttpRequest()
  respondRequest.open('POST', url + 'respondToComplaint');
  respondRequest.setRequestHeader('Content-Type', 'application/json');

  // On Load Function
  respondRequest.onload = function () {
    var data = JSON.parse(this.response);
    if (data.STATUS == 200) {
      progressText.innerHTML = "Request Sent!";
      window.location.replace("problem.html");
    } else {
      progressText.innerHTML = "Request Failed!";
      console.error("failed!");
    }
  }

  // Sending Actual Request
  var json = JSON.stringify({
    "complaintId" : complaint_id,
    "responseCategory" : responseCategory,
    "responseMessage" : response_text
  });
  console.log("JSON = " + json);
  respondRequest.send(json);
}
