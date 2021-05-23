package com.savaari.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.savaari.api.database.DBHandlerFactory;

import java.util.ArrayList;

public class Driver extends User
{
	// Status flags values
	public static final int
			DV_DEFAULT = 0,
			DV_REQ_SENT = 1,
			DV_REQ_REJECTED= 2,
			DV_REQ_APPROVED = 3;

	// Main Attributes
	private static final String LOG_TAG = Driver.class.getSimpleName();
	private Boolean active;

	@JsonProperty("CNIC")
	private String CNIC;
	private String licenseNumber;
	private int status;
	private Vehicle activeVehicle;
	private ArrayList<Vehicle> vehicles;

	// Main Constructors
	public Driver() {

	}

	public Driver(Location currentLocation, Boolean active, Boolean takingRide) {
		super();
	}
	
	// Getters and Setters

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getCNIC() {
		return CNIC;
	}

	public void setCNIC(String CNIC) {
		this.CNIC = CNIC;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Vehicle getActiveVehicle() {
		return activeVehicle;
	}

	public void setActiveVehicle(Vehicle activeVehicle) {
		this.activeVehicle = activeVehicle;
	}

	public void setRegistrationDetails(String firstName, String lastName, String phoneNo, String CNIC, String licenseNumber) {
		setFirstName(firstName);
		setLastName(lastName);
		setPhoneNo(phoneNo);
		setCNIC(CNIC);
		setLicenseNumber(licenseNumber);
	}

	// Main Methods for System Interactions

	// Sign-UP

	// Login
	public void login()
	{
		setUserID(DBHandlerFactory.getInstance().createDBHandler().loginDriver(this));
	}

	// Fetch Data
	public boolean fetchData()
	{
		return DBHandlerFactory.getInstance().createDBHandler().fetchDriverData(this);
	}

	// Set Mark Active
	public boolean setMarkActive()
	{
		return DBHandlerFactory.getInstance().createDBHandler().markDriverActive(this);
	}

	public ArrayList<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(ArrayList<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	public boolean sendRegistrationRequest() {
		return DBHandlerFactory.getInstance().createDBHandler().sendRegistrationRequest(this);
	}

	public boolean sendVehicleRegistrationRequest(Vehicle vehicle) {
		vehicles.add(vehicle);

		for (Vehicle currentVehicleRequest : vehicles) {
			DBHandlerFactory.getInstance().createDBHandler().sendVehicleRegistrationRequest(this,
					currentVehicleRequest);
			currentVehicleRequest.setStatus(Vehicle.VH_REQ_SENT);
		}
		return true;
	}

	/* Location methods */

	// Saving Driver Location
	public boolean saveDriverLocation()
	{
		return DBHandlerFactory.getInstance().createDBHandler().saveDriverLocation(this);
	}

	// Getting Driver Location
	public void getDriverLocation()
	{
		setCurrentLocation(DBHandlerFactory.getInstance().createDBHandler().getDriverLocation(this));
	}

	/* End of section */

	public boolean selectActiveVehicle(Vehicle vehicle) {
		activeVehicle = vehicle;
		return DBHandlerFactory.getInstance().createDBHandler().setActiveVehicle(this);
	}
}
