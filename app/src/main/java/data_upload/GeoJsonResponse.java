package data_upload;

import com.lowa_softwares.stimasafe.GeoJsonDataSchema;

public class GeoJsonResponse {
    // You may need to adjust these fields based on the actual response structure
    private String status;
    private GeoJsonDataSchema data;

    public String getStatus() {
        return status;
    }

    public GeoJsonDataSchema getData() {
        return data;
    }
}
