package data_upload;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IncidentImageUploadApiService {
    @Multipart
    @POST("/upload-single")
    Call<IncidentImageResponse> uploadImage(
            @Part MultipartBody.Part image
    );
}
