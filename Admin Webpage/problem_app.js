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
const progressText = document.getElementById("progress")
var spinner = document.getElementById("spinner")
var searchBar = document.getElementById("search_bar")
var searchButton = document.getElementById("search_btn")
let head = ["COMPLAINT ID", "SUBMITTED BY", "CATEGORY", "PROBLEM DESCRIPTION", "STATUS", "SUBMISSION DATE"];
var data

// ---------------------------------------------------------
// Actual API Connection and Processing
// ---------------------------------------------------------

const url = sessionStorage.getItem("url");
const user_id = sessionStorage.getItem("USER_ID");
console.log(user_id)

// Creating a Connection request
var connectionRequest = new XMLHttpRequest();

connectionRequest.open('GET', url + 'hello');
connectionRequest.onload = function () {
	progressText.innerHTML = "Connected!";
}
connectionRequest.send();

// --------------------------------------------
// 			Loading Problems data
// --------------------------------------------
var problemDataLoad = new XMLHttpRequest();
problemDataLoad.open('POST', url + 'fetchComplaints');
problemDataLoad.setRequestHeader('Content-Type', 'application/json');

problemDataLoad.onload = function () {
  // Begin accessing JSON data here
  data = JSON.parse(this.response);

  if (problemDataLoad.status >= 200 && problemDataLoad.status < 400) {
    let table = document.querySelector("table");
    progressText.innerHTML = "Data Loaded!";
    spinner.style.visibility = "hidden"
    searchBar.style.visibility = "visible"
    console.log(data);
    generateTableHead(table, head);
    generateTable(table, data, "", "");
  } else {
    console.log('error');
  }
}

// Calling API
problemDataLoad.send(JSON.stringify({"USER_ID":user_id}));

// ---------------------------------------------------------
// Table Generation Code
// ---------------------------------------------------------

// Generating Table Heads
function generateTableHead(table, data) {
  let thead = table.createTHead();
  let row = thead.insertRow();
  for (let key of data) {
    let th = document.createElement("th");
    let text = document.createTextNode(key);
    th.appendChild(text);
    row.appendChild(th);
  }
}

var detailButtonIDs = []
var storedRideIds = []
var storedComplaintIDS = []

function generateTable(table, data, field, query) {

  // Adding Objects to table
  let i = 0;
  for (let element of data) {

    // Adding Data
    var complaintId = element['complaintId'];
    var userId = element['userId'];
    var userType = element['userType'];
    var rideId = element['rideId'];
    var category = element['category'];
    var description = element['description'];
    var status = element['status'];
    var submissionTime = element['submissionTime'];
    var resolutionTime = element['resolutionTime'];
    var userTypeString;
    if (userType == 0) {
    	userTypeString = "RIDER";
    } else {
    	userTypeString = "DRIVER";
    }

    // Checking Search Data
    if (field == "COMPLAINT_ID" && complaintId != parseInt(query)) {
      continue
    }

    if (field == "CATEGORY" && category != parseInt(query)) {
      continue
    }

    if (field == "SUBMITTED_BY" && userTypeString != query) {
      continue
    }

    if (field == "STATUS" && status != parseInt(query)) {
      continue
    }

    // Creating First Row
    let row = table.insertRow();
    let cell = row.insertCell();

    // Storing Ride ID to be used later
    storedRideIds.push(rideId);

    // Displaying Data
    let text = document.createTextNode(complaintId);
    cell.appendChild(text);
    storedComplaintIDS.push(complaintId)

    cell = row.insertCell();
    text = document.createTextNode(userTypeString);
    cell.appendChild(text);

    cell = row.insertCell();
    text = document.createTextNode(category);
    cell.appendChild(text);

    cell = row.insertCell();
    text = document.createTextNode(description);
    cell.appendChild(text);

    cell = row.insertCell();
    text = document.createTextNode(status);
    cell.appendChild(text);

    cell = row.insertCell();
    var date = new Date(submissionTime);
    text = document.createTextNode(date);
    cell.appendChild(text);

    // Accept Button
    cell = row.insertCell();

    var buttonDetail = document.createElement("button");
    buttonDetail.id = "DETAIL" + i;
    detailButtonIDs.push(buttonDetail.id);
    buttonDetail.value = "DETAIL";
    buttonDetail.innerHTML = "DETAILS";

    buttonDetail.onclick = function() {
      progressText.innerHTML = "VIEWING DETAILS ...";
      var i = detailButtonIDs.indexOf(this.id);
      var r_id = storedRideIds[i]; 
      viewRideDetails(r_id);
    };

    cell.appendChild(buttonDetail);

    // Respond to Problem Button
    cell = row.insertCell();

    var buttonDetail = document.createElement("button");
    buttonDetail.id = "RESPOND" + i;
    detailButtonIDs.push(buttonDetail.id);
    buttonDetail.value = "RESPOND";
    buttonDetail.innerHTML = "RESPOND";

    buttonDetail.onclick = function() {
      progressText.innerHTML = "RESPONDING ...";
      var i = detailButtonIDs.indexOf(this.id);
      var r_id = storedComplaintIDS[i]; 
      respondToComplaint(r_id);
    };

    cell.appendChild(buttonDetail);
    // Iterating
    ++i;

  } // End of for loop
}

function viewRideDetails(rideID) {
	sessionStorage.setItem("RIDE_ID", rideID);
	window.location.replace("ride_detail.html");
}

function respondToComplaint(complaintId) {
	sessionStorage.setItem("COMPLAINT_ID", complaintId);
	window.location.replace("respond.html");
}

// Search Function
searchButton.onclick = function() {
  var searchText = document.getElementById("search_text")
  if (searchText.value == "") {
    return
  }
  var searchField = document.getElementById("search_option")

  // Generating the Table Again
  let table = document.querySelector("table");
  table.innerHTML = ""
  generateTableHead(table, head);
  generateTable(table, data, searchField.value, searchText.value);
}
