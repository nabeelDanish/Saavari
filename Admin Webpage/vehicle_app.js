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
var spinner = document.getElementById("spinner")
var searchBar = document.getElementById("search_bar")
var searchButton = document.getElementById("search_btn")
console.log(user_id)

// Data for stored Tables
var data
let head = ["VEHICLE ID", "MAKE", "MODEL", "YEAR", "COLOR" ,"NUMBER PLATE", "STATUS", "VEHICLE TYPE", "ACCEPT", "REJECT"];

// Creating a Connection request
var connectionRequest = new XMLHttpRequest();

connectionRequest.open('GET', url + 'hello');
connectionRequest.onload = function () {
  // alert("Connection Established");
  progressText.innerHTML = "Connected!";
}
connectionRequest.send();

// -------------------------------------------------------------
//                    Loading Vehicle Data
// -------------------------------------------------------------
var vehicleDataLoad = new XMLHttpRequest();
vehicleDataLoad.open('POST', url + 'vehicleRequests');
vehicleDataLoad.setRequestHeader('Content-Type', 'application/json');

vehicleDataLoad.onload = function () {
  // alert("Data loaded!");
  // console.log(this.response);
  // Begin accessing JSON data here
  data = JSON.parse(this.response);

  if (vehicleDataLoad.status >= 200 && vehicleDataLoad.status < 400) {
    // console.log(data);
    let table = document.querySelector("table");
    progressText.innerHTML = "Data Loaded!";
    spinner.style.visibility = "hidden"
    searchBar.style.visibility = "visible"
    console.log(data);
    generateTableHead(table, head);
    generateTable(table, data);
  } else {
    console.log('error');
  }
}

vehicleDataLoad.send(JSON.stringify({"USER_ID":user_id}));


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

// Generating Table with Buttons
var acceptButtonIDs = [];
var rejectButtonIDs = [];

function generateTable(table, data) {

  // Adding Objects to table
  let i = 0;
  for (let element of data) {
    let row = table.insertRow();
    let cell = row.insertCell();

    // Adding Data
    var vehicleID = element['vehicleID'];
    var vehicleType = element['vehicleType']
    var make = vehicleType['make'];
    var model = vehicleType['model'];
    var year = vehicleType['year'];
    var color = element['color'];
    var numberPlate = element['numberPlate'];
    var status = element['status'];
    var regReqID = element['vehicleID'];

    // Displaying Data
    let text = document.createTextNode(vehicleID);
    cell.appendChild(text);

    cell = row.insertCell();
    text = document.createTextNode(make);
    cell.appendChild(text);

    cell = row.insertCell();
    text = document.createTextNode(model);
    cell.appendChild(text);

    cell = row.insertCell();
    text = document.createTextNode(year);
    cell.appendChild(text);

    cell = row.insertCell();
    text = document.createTextNode(color);
    cell.appendChild(text);

    cell = row.insertCell();
    text = document.createTextNode(numberPlate);
    cell.appendChild(text);

    cell = row.insertCell();
    text = document.createTextNode(status);
    cell.appendChild(text);

    // Setting Logic for Table entries
    cell = row.insertCell();
    var rideTypeInput = document.createElement("select");
    rideTypeInput.id = "rideTypeInput" + i;
    
    var options = ["BIKE", "SAAVARI MINI", "SAAVARI GO", "SAAVARI X"]; 

    for (var k = 0; k < options.length; k++) {
      var opt = options[k];
      var el = document.createElement("option");
      el.textContent = opt;
      el.value = opt;
      rideTypeInput.appendChild(el);
    }

    cell.appendChild(rideTypeInput);

    // Accept Button
    cell = row.insertCell();

    var buttonAccept = document.createElement("button");
    buttonAccept.id = "ACCPET" + i;
    acceptButtonIDs.push(buttonAccept.id);
    buttonAccept.value = "ACCEPT";
    buttonAccept.innerHTML = "Accept Request";

    buttonAccept.onclick = function() {
      progressText.innerHTML = "Sending Request ...";
      var i = acceptButtonIDs.indexOf(this.id);
      var r_id = parseInt(document.getElementById("rideTypeInput" + i).value + 1);
      repondToVehicleRequest(1, regReqID, r_id, 3);
    };

    cell.appendChild(buttonAccept);

    // Adding Reject Button with listener
    cell = row.insertCell();

    var buttonReject = document.createElement("button");
    buttonReject.id = "REJECT" + i;
    rejectButtonIDs.push(buttonReject.id);
    buttonReject.value = "REJECT";
    buttonReject.innerHTML = "Reject Request";

    buttonReject.onclick = function() {
      progressText.innerHTML = "Sending Request ...";
      var i = rejectButtonIDs.indexOf(this.id);
      var r_id = parseInt(document.getElementById("rideTypeInput" + i).value + 1);
      repondToVehicleRequest(1, regReqID, r_id, 2);
    };
    cell.appendChild(buttonReject);

    // Iterating
    ++i;

  } // End of for loop
}

// API Connection Methods
function repondToVehicleRequest(driverID, rideRequestID, rideType, status) {
  var sendVehicleRequest = new XMLHttpRequest();

  sendVehicleRequest.open('POST', url + 'respondToVehicleRequest');
  sendVehicleRequest.setRequestHeader('Content-Type', 'application/json');

  sendVehicleRequest.onload = function() {
    var data = JSON.parse(this.response);
    if (data.STATUS == 200) {
      // alert("Sent Successful!");
      progressText.innerHTML = "Request Sent!";
      location.reload();
    } else {
      progressText.innerHTML = "Request Failed!";
      console.error("failed!");
    }
  }
  var json = JSON.stringify({
    "DRIVER_ID" : driverID,
    "REGISTRATION_REQ_ID" : rideRequestID,
    "VEHICLE_TYPE_ID" : rideType,
    "STATUS" : status
  });
  console.log("JSON = " + json);
  sendVehicleRequest.send(json);
}

// Search Bar
searchButton.onclick = function() {
  let table = document.querySelector("table");
  table.innerHTML = ""
  generateTableHead(table, head);
  generateTable(table, data);
}
