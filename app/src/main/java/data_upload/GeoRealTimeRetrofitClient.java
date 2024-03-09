package data_upload;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeoRealTimeRetrofitClient {
    private static final String BASE_URL = "https://powerline-monitoring-dashboard-ba1f4f6d707e.herokuapp.com/api/v1/";

    private static Retrofit retrofit;

    public static IncidentDataUploadApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(IncidentDataUploadApiService.class);
    }
}
