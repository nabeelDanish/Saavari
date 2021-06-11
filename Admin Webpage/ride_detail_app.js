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
var spinner = document.getElementById("spinner")

// ---------------------------------------------------------
// Actual API Connection and Processing
// ---------------------------------------------------------

const url = sessionStorage.getItem("url");
const user_id = sessionStorage.getItem("USER_ID");
const RIDE_ID = sessionStorage.getItem("RIDE_ID");

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
var rideDataLoad = new XMLHttpRequest();
rideDataLoad.open('POST', url + 'getRideForAdmin');
rideDataLoad.setRequestHeader('Content-Type', 'application/json');

rideDataLoad.onload = function () {
  // Begin accessing JSON data here
  var data = JSON.parse(this.response);

  if (rideDataLoad.status >= 200 && rideDataLoad.status < 400) {
    let table = document.querySelector("table");
    progressText.innerHTML = "Data Loaded!";
    spinner.style.visibility = "hidden"
    console.log(data);
    generateTable(table, data);
  } else {
    console.log('error');
  }
}

// Calling API
rideDataLoad.send(JSON.stringify({"USER_ID":user_id, "rideId":RIDE_ID}));

// ---------------------------------------------------------
// Table Generation Code
// ---------------------------------------------------------
function generateTable(table, data) {
  // Attributes to display
  var attributes = ["RIDE ID"];

  // Adding Objects to table
  let row = table.insertRow();
  let cell = document.createElement("th");

  // Ride ID Row 
  cell.setAttribute("colspan", "2");
  cell.style.fontSize = "x-large";
  cell.style.paddingLeft = "230px";
  cell.style.paddingRight = "230px";
  let text = document.createTextNode("RIDE ID #" + RIDE_ID);
  cell.appendChild(text);
  row.appendChild(cell);

  // -----------------------------------------------------------
  // Rider First Row
  row = table.insertRow();
  cell = document.createElement("th");
  text = document.createTextNode("RIDER");
  cell.setAttribute("colspan", "2");
  cell.appendChild(text);
  row.appendChild(cell)

  // Rider Name
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("NAME");
  cell.appendChild(text);

  var riderName = data['rideParameters']['rider']['username'];
  cell = row.insertCell();
  text = document.createTextNode(riderName);
  cell.appendChild(text);

  // Rider Rating
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("RATING");
  cell.appendChild(text);

  cell = row.insertCell();
  var riderRating = data['rideParameters']['rider']['rating'];
  for (var k = 1; k <= 5; ++k) {
    var star = document.createElement("span")
    if (k <= riderRating) {
      star.setAttribute("class", "fa fa-star checked")
    } else {
      star.setAttribute("class", "fa fa-star")
    }
    cell.appendChild(star)
  }
  

  // -----------------------------------------------------------
  // Driver Row
  // -----------------------------------------------------------
  row = table.insertRow();
  cell = document.createElement("th");
  cell.setAttribute("colspan", "2");
  text = document.createTextNode("DRIVER");
  cell.appendChild(text);
  row.appendChild(cell);

  // Driver Name
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("NAME");
  cell.appendChild(text);

  var driverName = data['rideParameters']['driver']['username'];
  cell = row.insertCell();
  text = document.createTextNode(driverName);
  cell.appendChild(text);

  // Driver Rating
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("RATING");
  cell.appendChild(text);

  cell = row.insertCell();
  var driverRating = data['rideParameters']['driver']['rating'];
  for (var k = 1; k <= 5; ++k) {
    var star = document.createElement("span")
    if (k <= driverRating) {
      star.setAttribute("class", "fa fa-star checked")
    } else {
      star.setAttribute("class", "fa fa-star")
    }
    cell.appendChild(star)
  }

  // -----------------------------------------------------------
  // Vehicle Row
  // -----------------------------------------------------------
  row = table.insertRow();
  cell = document.createElement("th");
  cell.setAttribute("colspan", "2");
  text = document.createTextNode("VEHICLE");
  cell.appendChild(text);
  row.appendChild(cell);

  // Vehicle Name
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("DESCRIPTION");
  cell.appendChild(text);

  var vehicleColor = data['rideParameters']['driver']['activeVehicle']['color'];
  var vehicleMake = data['rideParameters']['driver']['activeVehicle']['vehicleType']['make'];
  var vehicleModel = data['rideParameters']['driver']['activeVehicle']['vehicleType']['model'];
  var vehicleYear = data['rideParameters']['driver']['activeVehicle']['vehicleType']['year'];
  cell = row.insertCell();
  text = document.createTextNode(vehicleColor + " " + vehicleMake + " " + vehicleModel + " " + vehicleYear);
  cell.appendChild(text);

  // Vehicle Number Plate
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("NUMBER PLATE");
  cell.appendChild(text);

  var vehicleNumberPlate = data['rideParameters']['driver']['activeVehicle']['numberPlate'];
  cell = row.insertCell();
  text = document.createTextNode(vehicleNumberPlate);
  cell.appendChild(text);

  // -----------------------------------------------------------
  // Ride Row
  // -----------------------------------------------------------
  row = table.insertRow();
  cell = document.createElement("th");
  cell.setAttribute("colspan", "2");
  text = document.createTextNode("RIDE");
  cell.appendChild(text);
  row.appendChild(cell);

  // Pickup Time
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("PICKUP TIME");
  cell.appendChild(text);

  var pickupTime = data['startTime'];
  var date = new Date(pickupTime)
  cell = row.insertCell();
  text = document.createTextNode(date);
  cell.appendChild(text);

  // Pickup Location
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("PICKUP LOCATION");
  cell.appendChild(text);

  var pickupLat = data['rideParameters']['pickupLocation']['latitude'];
  var pickupLong = data['rideParameters']['pickupLocation']['longitude'];
  cell = row.insertCell();
  text = document.createElement("div");
  text.setAttribute("id", "map_pickup");
  text.setAttribute("class", "map");
  cell.appendChild(text);

  // OpenLayers Maps API
  var map = new ol.Map({
        target: 'map_pickup',
        layers: [
          new ol.layer.Tile({
            source: new ol.source.OSM()
          })
        ],
        view: new ol.View({
          center: ol.proj.fromLonLat([pickupLong, pickupLat]),
          zoom: 15
        })
      });

  var layer = new ol.layer.Vector({
     source: new ol.source.Vector({
         features: [
             new ol.Feature({
                 geometry: new ol.geom.Point(ol.proj.fromLonLat([pickupLong, pickupLat]))
             })
         ]
     })
   });
   map.addLayer(layer);

  // Dropoff Time
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("DROPOFF TIME");
  cell.appendChild(text);

  var pickupTime = data['endTime'];
  var date = new Date(pickupTime)
  cell = row.insertCell();
  text = document.createTextNode(date);
  cell.appendChild(text);

   // Dropoff Location
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("DROPOFF LOCATION");
  cell.appendChild(text);

  var dropoffLat = data['rideParameters']['dropoffLocation']['latitude'];
  var dropoffLong = data['rideParameters']['dropoffLocation']['longitude'];
  cell = row.insertCell();
  text = document.createElement("div");
  text.setAttribute("id", "map_dropoff");
  text.setAttribute("class", "map");
  cell.appendChild(text);

  // OpenLayers Maps API
  map = new ol.Map({
        target: 'map_dropoff',
        layers: [
          new ol.layer.Tile({
            source: new ol.source.OSM()
          })
        ],
        view: new ol.View({
          center: ol.proj.fromLonLat([dropoffLong, dropoffLat]),
          zoom: 15
        })
      });

  layer = new ol.layer.Vector({
     source: new ol.source.Vector({
         features: [
             new ol.Feature({
                 geometry: new ol.geom.Point(ol.proj.fromLonLat([dropoffLong, dropoffLat]))
             })
         ]
     })
   });
   map.addLayer(layer);

  // -----------------------------------------------------------
  // Payment Row
  // -----------------------------------------------------------
  row = table.insertRow();
  cell = document.createElement("th");
  cell.setAttribute("colspan", "2");
  text = document.createTextNode("PAYMENT");
  cell.appendChild(text);
  row.appendChild(cell);

  // Ride Type
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("RIDE TYPE");
  cell.appendChild(text);

  var rideType = data['rideParameters']['rideType']['typeID'];
  var rideTypeString = ['BIKE', 'SAAVARI MINI', 'SAAVARI GO', 'SAAVARI X'];
  cell = row.insertCell();
  text = document.createTextNode(rideTypeString[rideType]);
  cell.appendChild(text);

  // Estimatd Fare
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("ESTIMATED FARE");
  cell.appendChild(text);

  var estimatedFare = data['estimatedFare'];
  cell = row.insertCell();
  text = document.createTextNode(estimatedFare);
  cell.appendChild(text);

  // Actual Fare
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("FARE");
  cell.appendChild(text);

  var fare = data['fare'];
  cell = row.insertCell();
  text = document.createTextNode(fare);
  cell.appendChild(text);

  // Amount Paid
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("AMOUNT PAID");
  cell.appendChild(text);

  var amountPaid = data['payment']['amountPaid'];
  cell = row.insertCell();
  text = document.createTextNode(amountPaid);
  cell.appendChild(text);

  // Change
  row = table.insertRow();
  cell = row.insertCell();
  text = document.createTextNode("CHANGE");
  cell.appendChild(text);

  var change = data['payment']['change'];
  cell = row.insertCell();
  text = document.createTextNode(change);
  cell.appendChild(text);
}
