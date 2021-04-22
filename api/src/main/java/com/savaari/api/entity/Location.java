package com.savaari.api.entity;

public class Location
{
	// Main Attributes
	Double latitude;
	Double longitude;
	Long timestamp;
	
	// Main Constructors
	public Location() {
		super();
	}

	public Location(Double latitude, Double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public Location(Double latitude, Double longitude, Long timestamp) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.timestamp = timestamp;
	}
	
	// Getters and Setters
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
