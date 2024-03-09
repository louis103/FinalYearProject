package data_upload;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GeoDataUploadApiService {
    @POST("powerlines/")
    Call<GeoJsonResponse> uploadGeoJsonData(@Body RequestBody geoJsonData);
}
