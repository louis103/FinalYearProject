package com.lowa_softwares.stimasafe;

public class GeoJsonDataSchema {
    private String state;
    private String structural_type;
    private String datetime;
    private String latitude;
    private String longitude;
    private String photo_of_powerline;
    private String condition_of_transformer;
    private String photo_of_transformer;
    private String count_of_broken_cables;
    private String photo_of_powercables;
    private String accuracy;
    private String altitude;
    private Boolean has_broken_powercables;
    private Boolean has_transformer;

    public GeoJsonDataSchema(String state, String structural_type, String datetime, String latitude, String longitude, String photo_of_powerline, String condition_of_transformer, String photo_of_transformer, String count_of_broken_cables, String photo_of_powercables, String accuracy, String altitude, Boolean has_broken_powercables, Boolean has_transformer) {
        this.state = state;
        this.structural_type = structural_type;
        this.datetime = datetime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo_of_powerline = photo_of_powerline;
        this.condition_of_transformer = condition_of_transformer;
        this.photo_of_transformer = photo_of_transformer;
        this.count_of_broken_cables = count_of_broken_cables;
        this.photo_of_powercables = photo_of_powercables;
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.has_broken_powercables = has_broken_powercables;
        this.has_transformer = has_transformer;
    }
}
