package com.lowa_softwares.stimasafe;

public class RealtimeGeoJsonSchema {
    private String incidentType;
    private String severityLevel;
    private String dateOfIncident;
    private String timeOfIncident;
    private String latitude;
    private String longitude;
    private String image_url;

    public RealtimeGeoJsonSchema(String incidentType, String severityLevel, String dateOfIncident, String timeOfIncident, String latitude, String longitude, String image_url) {
        this.incidentType = incidentType;
        this.severityLevel = severityLevel;
        this.dateOfIncident = dateOfIncident;
        this.timeOfIncident = timeOfIncident;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image_url = image_url;
    }
}
