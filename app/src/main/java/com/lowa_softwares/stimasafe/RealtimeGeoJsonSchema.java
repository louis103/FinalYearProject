package com.lowa_softwares.stimasafe;

public class RealtimeGeoJsonSchema {
    private String incident_type;
    private String severity;
    private String latitude;
    private String longitude;
    private String photo_of_incident;

    public RealtimeGeoJsonSchema(String incident_type, String severity, String latitude, String longitude, String photo_of_incident) {
        this.incident_type = incident_type;
        this.severity = severity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo_of_incident = photo_of_incident;
    }
}
