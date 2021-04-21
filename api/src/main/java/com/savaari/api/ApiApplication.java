package com.savaari.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

}
