package com.lowa_softwares.stimasafe;

public class GeoJsonDataSchema {
    private String powerlineState;
    private String powerlineStrType;
    private String powerlineRecordDate;
    private String powerlineLatitude;
    private String powerlineLongitude;
    private String powerlinePhotoPath;
    private String transformer_cond;
    private String transformerPhotoPath;
    private String cableCount;
    private String cablesPhotoPath;
    private String accuracy;
    private String altitude;

    public GeoJsonDataSchema(String powerlineState, String powerlineStrType, String powerlineRecordDate, String powerlineLatitude, String powerlineLongitude, String powerlinePhotoPath, String transformer_cond, String transformerPhotoPath, String cableCount, String cablesPhotoPath, String accuracy, String altitude) {
        this.powerlineState = powerlineState;
        this.powerlineStrType = powerlineStrType;
        this.powerlineRecordDate = powerlineRecordDate;
        this.powerlineLatitude = powerlineLatitude;
        this.powerlineLongitude = powerlineLongitude;
        this.powerlinePhotoPath = powerlinePhotoPath;
        this.transformer_cond = transformer_cond;
        this.transformerPhotoPath = transformerPhotoPath;
        this.cableCount = cableCount;
        this.cablesPhotoPath = cablesPhotoPath;
        this.accuracy = accuracy;
        this.altitude = altitude;
    }
}
