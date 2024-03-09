package data_upload;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageUploadResponse {
    @SerializedName("urls")
    private List<String> urls;
    public List<String> getUrls() {
        return urls;
    }
}
