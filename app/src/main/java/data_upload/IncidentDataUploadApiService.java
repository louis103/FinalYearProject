package data_upload;

import com.lowa_softwares.stimasafe.RealtimeGeoJsonSchema;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IncidentDataUploadApiService {
    @POST("powerline-incidents/")
    Call<RealtimeGeoJsonSchema> uploadGeoJsonData(@Body RequestBody geoJsonData);
}
