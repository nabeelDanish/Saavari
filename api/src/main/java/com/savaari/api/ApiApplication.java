package com.savaari.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.savaari.api.controllers.CRUDController;
import com.savaari.api.entity.Driver;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SpringBootApplication
@RestController
public class ApiApplication {

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

	// Logout Driver
	// TODO: Add layer that checks user is logged out in database
	@RequestMapping(value = "/logout_driver", method = RequestMethod.POST)
	public String logoutDriver(@RequestBody Map<String, String> allParams, HttpServletRequest request)
	{
		request.getSession().invalidate();
		return new JSONObject().put("STATUS_CODE", 200).toString();
	}
	/* End of section*/

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

}
