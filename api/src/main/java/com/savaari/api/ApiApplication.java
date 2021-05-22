package com.savaari.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.savaari.api.controllers.AdminSystem;
import com.savaari.api.controllers.CRUDController;
import com.savaari.api.controllers.LocationController;
import com.savaari.api.entity.*;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

@SpringBootApplication
@RestController
public class ApiApplication {

	private static LocationController locationController;
	private static ObjectMapper objectMapper;

	/* MAIN METHOD */
	public static void main(String[] args)
	{
		objectMapper = new ObjectMapper();
		objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

		SpringApplication.run(ApiApplication.class, args);
	}

	// Test API Status
	@CrossOrigin(origins = "*")
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	/*
	 * Checks if matchmakingController stored as session attribute
	 * If not, create and store
	 */
	private <T> T getAttributeObject(HttpServletRequest request, Class<T> valueType, String className) {
		String msgs = (String) request.getSession().getAttribute(className);
		T object;

		try {
			// No such session attribute is stored
			if (msgs == null) {
				// Initialize controller and attribute list
				object = (T) Class.forName(className).getDeclaredConstructor().newInstance();

				// Save to session attributes
				msgs = (objectMapper.writeValueAsString(object));
				request.getSession().setAttribute(className, msgs);
			}
			else {
				// Deserialize into controller object
				object = objectMapper.readValue(msgs, valueType);
			}

			return object;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private <T> boolean storeObjectAsAttribute(HttpServletRequest request, String className, T object) {

		if (object == null) {
			return false;
		}

		@SuppressWarnings("unchecked")
		String msgs = (String) request.getSession().getAttribute(className);

		try {
			// No such session attribute is stored
			if (msgs == null) {
				// Initialize controller and attribute list
				object = (T) Class.forName(className).getDeclaredConstructor().newInstance();
				msgs = "";
			}

			// Save to session attributes
			msgs = (objectMapper.writeValueAsString(object));
			request.getSession().setAttribute(className, msgs);

			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void deleteAttribute(HttpServletRequest request, String className) {
		request.getSession().removeAttribute(className);
	}


	/* Add new user methods */
	@RequestMapping(value = "/add_rider", method = RequestMethod.POST)
	public String addRider(@RequestBody Map<String, String> allParams)
	{
		System.out.println(allParams.toString());
		String username = allParams.get("username");
		String email_address = allParams.get("email_address");
		String password = allParams.get("password");

		JSONObject result = new JSONObject();

		if (new CRUDController().addRider(username, email_address, password)) {
			result.put("STATUS_CODE", 200);
		}
		else {
			result.put("STATUS_CODE", 404);
		}
		return result.toString();
	}

	// Sign-up for Driver
	@RequestMapping(value = "/add_driver", method = RequestMethod.POST)
	public String addDriver(@RequestBody Map<String, Object> allParams)
	{
		System.out.println(allParams.toString());

		String username = (String) allParams.get("username");
		String email_address = (String) allParams.get("email_address");
		String password = (String) allParams.get("password");
		JSONObject result = new JSONObject();
		if (new CRUDController().addDriver(username, email_address, password)) {
			result.put("STATUS_CODE", 200);
		}
		else {
			result.put("STATUS_CODE", 404);
		}

		return result.toString();
	}
	/* End of section */

	/* Authenticate user methods */

	/* Authenticate user methods */
	@RequestMapping(value = "/login_rider", method = RequestMethod.POST)
	public String loginRider(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		Rider rider = new Rider();
		rider.setEmailAddress(allParams.get("username"));
		rider.setPassword(allParams.get("password"));

		CRUDController crudController = getAttributeObject(request, CRUDController.class, CRUDController.class.getName());
		if (crudController == null) { crudController = new CRUDController(); }

		Integer userID = crudController.loginRider(rider);

		// Package response
		JSONObject result = new JSONObject();

		if (userID == null) {
			result.put("STATUS_CODE", 404);
			result.put("USER_ID", -1);
		}
		else {
			result.put("STATUS_CODE", 200);
			result.put("USER_ID", userID);

			if (request.getSession(false) == null) {
				request.getSession(true);
			}

			storeObjectAsAttribute(request, CRUDController.class.getName(), crudController);
		}

		return result.toString();
	}

	// Persist Login for Rider
	@RequestMapping(value = "/persistRiderLogin", method = RequestMethod.POST)
	public String persistRiderLogin(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		if (request.getSession(false) == null) {
			request.getSession(true);

			Rider rider = new Rider();
			rider.setUserID(Integer.parseInt(allParams.get("USER_ID")));

			CRUDController crudController = getAttributeObject(request, CRUDController.class, CRUDController.class.getName());
			if (crudController == null) { crudController = new CRUDController(); }

			crudController.persistRiderLogin(rider);
			storeObjectAsAttribute(request, CRUDController.class.getName(), crudController);
			return new JSONObject().put("STATUS_CODE", 200).toString();
		}

		return new JSONObject().put("STATUS_CODE", 200).toString();
	}

	// Login for Driver
	@RequestMapping(value = "/login_driver", method = RequestMethod.POST)
	public String loginDriver(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		CRUDController crudController = getAttributeObject(request, CRUDController.class, CRUDController.class.getName());
		if (crudController == null) { crudController = new CRUDController(); }

		Driver driver = new Driver();
		driver.setEmailAddress(allParams.get("username"));
		driver.setPassword(allParams.get("password"));

		Integer userID = crudController.loginDriver(driver);

		// Package response
		JSONObject result = new JSONObject();

		if (userID == null) {
			result.put("STATUS_CODE", 404);
			result.put("USER_ID", -1);
		}
		else {
			result.put("STATUS_CODE", 200);
			result.put("USER_ID", userID);

			if (request.getSession(false) == null) {
				request.getSession(true);
			}

			storeObjectAsAttribute(request, CRUDController.class.getName(), crudController);
		}

		return result.toString();
	}

	// Persist Driver Login Call
	@RequestMapping(value = "/persistDriverLogin", method = RequestMethod.POST)
	public String persistDriverLogin(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		JSONObject result = new JSONObject();
		Driver driver = new Driver();
		driver.setUserID(Integer.parseInt(allParams.get("USER_ID")));

		if (request.getSession(false) == null) {
			request.getSession(true);

			CRUDController crudController = getAttributeObject(request, CRUDController.class, CRUDController.class.getName());

			if (crudController != null) {
				crudController.persistDriverLogin(driver);
				storeObjectAsAttribute(request, CRUDController.class.getName(), crudController);
				result.put("STATUS", 200);
				result.put("USER_ID", driver.getUserID());
			}
			else {
				result.put("STATUS_CODE", 404);
				result.put("USER_ID", Driver.DEFAULT_ID);
			}

			return result.toString();
		}
		else {
			result.put("STATUS_CODE", 200);
			result.put("USER_ID", driver.getUserID());
		}

		return result.toString();
	}

	// Logout Rider
	// TODO: Add layer that checks user is logged out in database
	@RequestMapping(value = "/logout_rider", method = RequestMethod.POST)
	public String logoutRider(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		request.getSession().invalidate();
		return new JSONObject().put("STATUS_CODE", 200).toString();
	}

	// Logout Driver
	// TODO: Add layer that checks user is logged out in database
	@RequestMapping(value = "/logout_driver", method = RequestMethod.POST)
	public String logoutDriver(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		request.getSession().invalidate();
		return new JSONObject().put("STATUS_CODE", 200).toString();
	}
	/* End of section*/

	@RequestMapping(value = "/rider_data", method = RequestMethod.POST)
	public String riderData(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		if (request.getSession(false) == null) {
			System.out.println("Driver data: Invalid session");
			return null;
		}

		CRUDController crudController = getAttributeObject(request, CRUDController.class, CRUDController.class.getName());
		if (crudController == null) { return null; }

		String result = null;
		Rider rider = crudController.riderData();
		if (rider != null) {
			try {
				result = objectMapper.writeValueAsString(rider);
			}
			catch (JsonProcessingException e) {
				e.printStackTrace();
				return null;
			}
		}

		storeObjectAsAttribute(request, CRUDController.class.getName(), crudController);
		return result;
	}

	// Fetching Driver Data
	@RequestMapping(value = "/driver_data", method = RequestMethod.POST)
	public String driverData(@RequestBody Map<String, String> allParams, HttpServletRequest request) {
		if (request.getSession(false) == null) {
			System.out.println("Driver data: Invalid session");
			return null;
		}

		CRUDController crudController = getAttributeObject(request, CRUDController.class, CRUDController.class.getName());
		if (crudController == null) { return null; }

		String result = null;
		Driver driver = crudController.driverData();
		if (driver != null) {
			try {
				result = objectMapper.writeValueAsString(driver);
			}
			catch (JsonProcessingException e) {
				e.printStackTrace();
				return null;
			}
		}
		storeObjectAsAttribute(request, CRUDController.class.getName(), crudController);
		return result;
	}


	/*
	* -------------------------------------
	*  ADMIN SYSTEM REQUESTS
	* -------------------------------------
	*/

	@CrossOrigin(origins = "*")
	@PostMapping("/add_admin")
	public String addAdmin(@RequestBody String allParams, HttpServletRequest request) {

		JSONObject result = new JSONObject();

		try {
			Administrator administrator = objectMapper.readValue(allParams, Administrator.class);

			if (new AdminSystem().addAdmin(administrator)) {
				result.put("STATUS_CODE", 200);
			}
			else {
				result.put("STATUS_CODE", 404);
			}
			return result.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
			result.put("STATUS_CODE", 404);
			return result.toString();
		}
	}

	@CrossOrigin(origins = "*")
	@PostMapping("/login_admin")
	public String loginAdmin(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		AdminSystem adminSystem = getAttributeObject(request, AdminSystem.class, AdminSystem.class.getName());
		if (adminSystem == null) { return null; }

		Administrator admin = new Administrator();
		admin.setEmailAddress(allParams.get("username"));
		admin.setPassword(allParams.get("password"));

		// Package response
		JSONObject result = new JSONObject();

		if (adminSystem.loginAdmin(admin)) {
			return null;
		}
		else {
			if (request.getSession(false) == null) {
				request.getSession(true);
			}

			try {
				storeObjectAsAttribute(request, AdminSystem.class.getName(), adminSystem);
				return objectMapper.writeValueAsString(admin);
			}
			catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		return null;
	}


	/*
	* -------------------------------------------
	*  DRIVER & VEHICLE REGISTRATION REQUESTS
	* -------------------------------------------
	* */

	/* Get Driver & Vehicle Requests */
	@CrossOrigin(origins = "*")
	@PostMapping("/driverRequests")
	public String driverRequests(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{

		AdminSystem adminSystem = getAttributeObject(request, AdminSystem.class, AdminSystem.class.getName());
		if (adminSystem == null) { return null; }

		try {
			ArrayList<Driver> driverRequests = adminSystem.getDriverRequests();
			if (driverRequests != null) {
				return objectMapper.writeValueAsString(driverRequests);
			}

			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@CrossOrigin(origins = "*")
	@PostMapping("/vehicleRequests")
	public String vehicleRequests(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{

		AdminSystem adminSystem = getAttributeObject(request, AdminSystem.class, AdminSystem.class.getName());
		if (adminSystem == null) { return null; }

		try {
			ArrayList<Vehicle> vehicleRequests = adminSystem.getVehicleRequests();
			if (vehicleRequests != null) {
				return objectMapper.writeValueAsString(vehicleRequests);
			}

			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/* Send Registration Request methods */

	@RequestMapping(value = "/registerDriver", method = RequestMethod.POST)
	public String registerDriver(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		if (request.getSession(false) == null) {
			return null;
		}

		CRUDController crudController = getAttributeObject(request, CRUDController.class, CRUDController.class.getName());

		Driver driver = new Driver();
		driver.setUserID(Integer.parseInt(allParams.get("USER_ID")));
		driver.setFirstName(allParams.get("FIRST_NAME"));
		driver.setLastName(allParams.get("LAST_NAME"));
		driver.setPhoneNo(allParams.get("PHONE_NO"));
		driver.setCNIC(allParams.get("CNIC"));
		driver.setLicenseNumber(allParams.get("LICENSE_NUMBER"));

		boolean status = crudController.registerDriver(driver);

		// Package response
		JSONObject result = new JSONObject();
		if (status) {
			result.put("STATUS", 200);
		} else {
			result.put("STATUS", 400);
		}

		storeObjectAsAttribute(request, CRUDController.class.getName(), crudController);

		return result.toString();
	}

	/*
	 * Param: STATUS - previous status
	 * If STATUS = Vehicle.VH_REJECTED, must have old registrationRequestID
	 * ELIF STATUS = VH_DEFAULT, send new request
	 * */
	@RequestMapping(value = "/sendVehicleRequest", method = RequestMethod.POST)
	public String sendVehicleRegistrationRequest(@RequestBody Map<String, String> allParams, HttpServletRequest request) {

		if (request.getSession(false) == null) {
			return null;
		}

		CRUDController crudController = getAttributeObject(request, CRUDController.class, CRUDController.class.getName());

		if (crudController == null) {
			return new JSONObject().put("STATUS", 404).toString();
		}

		Vehicle vehicle = new Vehicle();
		if (allParams.containsKey("REGISTRATION_REQ_ID")) {
			System.out.println("Setting reg req id: " + Integer.parseInt(allParams.get("REGISTRATION_REQ_ID")));
			vehicle.setVehicleID(Integer.parseInt(allParams.get("REGISTRATION_REQ_ID")));
		}
		else {
			System.out.println("Default reg req id");
			vehicle.setVehicleID(Vehicle.DEFAULT_ID);
		}
		vehicle.setMake(allParams.get("MAKE"));
		vehicle.setModel(allParams.get("MODEL"));
		vehicle.setYear(allParams.get("YEAR"));
		vehicle.setNumberPlate(allParams.get("NUMBER_PLATE"));
		vehicle.setColor(allParams.get("COLOR"));
		vehicle.setStatus(Integer.parseInt(allParams.get("STATUS")));

		JSONObject result = new JSONObject();
		boolean requestSent = crudController.sendVehicleRegistrationRequest(vehicle);

		storeObjectAsAttribute(request, CRUDController.class.getName(), crudController);
		result.put("STATUS", ((requestSent)?200:404));
		return result.toString();
	}

	/* Respond to Registration Requests */

	/**
	 *
	 * @param allParams STATUS - new status (VH_REQUEST_REJECTED or VH_REQUEST_ACCEPTED), VEHICLE_TYPE_ID,
	 *                     DRIVER_ID & REGISTRATION_REQUEST_ID
	 * @param request Http Request object
	 * @return JSONObject string. STATUS key maps to 200 if successful, 404 if not
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/respondToVehicleRequest", method = RequestMethod.POST)
	public String respondToVehicleRequest(@RequestBody Map<String, String> allParams, HttpServletRequest request) {

		AdminSystem adminSystem = getAttributeObject(request, AdminSystem.class, AdminSystem.class.getName());
		if (adminSystem == null) { return null; }

		Vehicle vehicleRequest = new Vehicle();
		vehicleRequest.setVehicleID(Integer.parseInt(allParams.get("REGISTRATION_REQ_ID")));
		vehicleRequest.setVehicleTypeID(Integer.parseInt(allParams.get("VEHICLE_TYPE_ID")));
		vehicleRequest.setStatus(Integer.parseInt(allParams.get("STATUS")));

		JSONObject result = new JSONObject();
		boolean responseSent = adminSystem.respondToVehicleRegistrationRequest(vehicleRequest);
		result.put("STATUS", ((responseSent)?200:404));
		return result.toString();
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/respondToDriverRequest", method = RequestMethod.POST)
	public String respondToDriverRequest(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{

		AdminSystem adminSystem = getAttributeObject(request, AdminSystem.class, AdminSystem.class.getName());
		if (adminSystem == null) { return null; }

		Driver driver = new Driver();
		driver.setUserID(Integer.parseInt(allParams.get("DRIVER_ID")));
		driver.setStatus(Integer.parseInt(allParams.get("STATUS")));

		Administrator administrator = getAttributeObject(request, Administrator.class, Administrator.class.getName());

		boolean status = adminSystem.respondToDriverRegistrationRequest(driver);

		// Packaging Response
		JSONObject jsonObject = new JSONObject();
		if (status) {
			jsonObject.put("STATUS", 200);
		} else {
			jsonObject.put("STATUS", 404);
		}
		return jsonObject.toString();
	}


	/*
	 * -------------------------------------------
	 *  DRIVER & RIDER LOCATION REQUESTS
	 * -------------------------------------------
	 * */

	@RequestMapping(value = "/saveDriverLocation", method = RequestMethod.POST)
	public String saveDriverLocation(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		if (request.getSession(false) == null) {
			return null;
		}

		Driver driver = new Driver();
		driver.setUserID(Integer.parseInt(allParams.get("USER_ID")));
		driver.setCurrentLocation(new Location(Double.valueOf(allParams.get("LATITUDE")), Double.valueOf(allParams.get("LONGITUDE")),
				Long.parseLong(allParams.get("TIMESTAMP"))));

		boolean locationSaved = locationController.saveDriverLocation(driver);

		JSONObject result = new JSONObject();
		result.put("STATUS", ((locationSaved)? 200 : 404));
		return result.toString();
	}

	@RequestMapping(value = "/saveRiderLocation", method = RequestMethod.POST)
	public String saveRiderLocation(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		if (request.getSession(false) == null) {
			return null;
		}

		Rider rider = new Rider();
		rider.setUserID(Integer.parseInt(allParams.get("USER_ID")));
		rider.setCurrentLocation(new Location(Double.valueOf(allParams.get("LATITUDE")), Double.valueOf(allParams.get("LONGITUDE")),
				Long.parseLong(allParams.get("TIMESTAMP"))));

		boolean locationSaved = locationController.saveRiderLocation(rider);

		JSONObject result = new JSONObject();
		result.put("STATUS", ((locationSaved)? 200 : 404));
		return result.toString();
	}

	@RequestMapping(value = "/getDriverLocations", method = RequestMethod.POST)
	public String getDriverLocations(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		if (request.getSession(false) == null) {
			return null;
		}

		try {
			ArrayList<Location> locations = locationController.getDriverLocations();

			if (locations == null) {
				return null;
			} else {
				return objectMapper.writeValueAsString(locations);
			}
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/getRiderLocations", method = RequestMethod.POST)
	@ResponseBody
	public String getRiderLocations(HttpServletRequest request)
	{
		if (request.getSession(false) == null) {
			return null;
		}

		try {
			ArrayList<Location> locations = locationController.getRiderLocations();

			if (locations == null) {
				return null;
			} else {
				return objectMapper.writeValueAsString(locations);
			}
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/getDriverLocation", method = RequestMethod.POST)
	public String getDriverLocation(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		if (request.getSession(false) == null) {
			return null;
		}

		Driver driver = new Driver();
		driver.setUserID(Integer.parseInt(allParams.get("USER_ID")));

		locationController.getDriverLocation(driver);

		JSONObject result = new JSONObject();

		if (driver.getCurrentLocation() == null) {
			result.put("STATUS_CODE", 404);
		}
		else {
			result.put("STATUS_CODE", 200);
			result.put("LATITUDE", driver.getCurrentLocation().getLatitude());
			result.put("LONGITUDE", driver.getCurrentLocation().getLongitude());
		}
		return result.toString();
	}

	@RequestMapping(value = "/getRiderLocation", method = RequestMethod.POST)
	public String getRiderLocation(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		if (request.getSession(false) == null) {
			return null;
		}

		Rider rider = new Rider();
		rider.setUserID(Integer.parseInt(allParams.get("USER_ID")));

		locationController.getRiderLocation(rider);

		JSONObject result = new JSONObject();

		if (rider.getCurrentLocation() == null) {
			result.put("STATUS_CODE", 404);
		}
		else {
			result.put("STATUS_CODE", 200);
			result.put("LATITUDE", rider.getCurrentLocation().getLatitude());
			result.put("LONGITUDE", rider.getCurrentLocation().getLongitude());
		}
		return result.toString();
	}

	/* End of section */

}
