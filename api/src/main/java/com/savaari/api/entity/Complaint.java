package com.savaari.api.entity;

public class Complaint {
    private int complaintId;
    private int userId;
    private int userType;
    private int rideId;
    private int category;
    private String description;
    private int status;
    private long submissionTime;
    private long resolutionTime;

    public Complaint(int complaintId, int userId, int userType, int rideId, int category,
                     String description, int status, long submissionTime, long resolutionTime) {
        this.complaintId = complaintId;
        this.userId = userId;
        this.userType = userType;
        this.rideId = rideId;
        this.category = category;
        this.description = description;
        this.status = status;
        this.submissionTime = submissionTime;
        this.resolutionTime = resolutionTime;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(long submissionTime) {
        this.submissionTime = submissionTime;
    }

    public long getResolutionTime() {
        return resolutionTime;
    }

    public void setResolutionTime(long resolutionTime) {
        this.resolutionTime = resolutionTime;
    }
}
