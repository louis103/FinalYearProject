package data_upload;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImageUploadApiService {
    @Multipart
    @POST("/upload-multiple")
    Call<ImageUploadResponse> uploadImages(
            @Part MultipartBody.Part image1,
            @Part MultipartBody.Part image2,
            @Part MultipartBody.Part image3
    );
}
