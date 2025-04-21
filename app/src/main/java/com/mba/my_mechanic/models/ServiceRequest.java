package com.mba.my_mechanic.models;

public class ServiceRequest {
    private String id;
    private String garageId;
    private String mechanicId;
    private String problemDescription;
    private String status;
    private String userId;
    private String userLocation;
    private String userPhone;
    private String username;
    private String vehicleType;

    public ServiceRequest(String id, String garageId, String mechanicId, String problemDescription, String status,
                          String userId, String userLocation, String userPhone, String username, String vehicleType) {
        this.id = id;
        this.garageId = garageId;
        this.mechanicId = mechanicId;
        this.problemDescription = problemDescription;
        this.status = status;
        this.userId = userId;
        this.userLocation = userLocation;
        this.userPhone = userPhone;
        this.username = username;
        this.vehicleType = vehicleType;
    }

//    create a another construtor ServiceRequest(description, vehicle, location, phone)
    public ServiceRequest(String problemDescription, String vehicleType, String userLocation, String userPhone) {
        this.problemDescription = problemDescription;
        this.vehicleType = vehicleType;
        this.userLocation = userLocation;
        this.userPhone = userPhone;
    }

    // Add getters here
    public String getProblemDescription() { return problemDescription; }
    public String getStatus() { return status; }
    public String getUserLocation() { return userLocation; }
    public String getUserPhone() { return userPhone; }
    public String getUsername() { return username; }
    public String getVehicleType() { return vehicleType; }
}