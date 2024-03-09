package com.lowa_softwares.stimasafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import data_upload.ErrorResponse;
import data_upload.GeoRealTimeRetrofitClient;
import data_upload.IncidentDataUploadApiService;
import data_upload.IncidentImageResponse;
import data_upload.IncidentImageUploadApiService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IncidentPhotoAndReporting extends AppCompatActivity {

    ImageView imageGoBack;
    AppCompatButton btnChooseTakePhoto;
    TextView txtPhotoPath;
    ImageView selectedImg;
    AppCompatButton btnReportIncidentNow;

    String [] itemsIncType = {"Transformer Blow-Up","Electric Shock","Electrical driven Fire"};
    String [] itemsSevMeasure = {"High","Mild","Low"};

    // setup the base URL for uploding image
    private static final String BASE_URL = "https://powerline-image-uploader-5d8b8ecfd15f.herokuapp.com/";

    FrameLayout frameIncidentphoto;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_photo_realtime);

        imageGoBack = findViewById(R.id.imageGoBack);
        btnChooseTakePhoto = findViewById(R.id.btnChooseTakePhoto);
        txtPhotoPath = findViewById(R.id.txtPhotoPath);
        selectedImg = findViewById(R.id.selectedImg);
        btnReportIncidentNow = findViewById(R.id.btnReportIncidentNow);
        frameIncidentphoto = findViewById(R.id.frameIncidentphoto);

        // load unsaved data
        loadUnsavedDetails();

        imageGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IncidentPhotoAndReporting.this, RecordPowerIncident.class));
            }
        });
        btnChooseTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(IncidentPhotoAndReporting.this)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        btnReportIncidentNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Browse data from shared preferences */
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(IncidentPhotoAndReporting.this);

                int incidentType = preferences.getInt(Constants.PREF_INCIDENT_TYPE, 0);
                int severityLevel = preferences.getInt(Constants.PREF_INCIDENT_SEVERITY_LEVEL, 0);

                String incidentType1 = itemsIncType[incidentType];
                String severityLevel1 = itemsSevMeasure[severityLevel];

                // String dateOfIncident = preferences.getString(Constants.PREF_INCIDENT_DATE, "7/2/2024");
                // String timeOfIncident = preferences.getString(Constants.PREF_INCIDENT_TIME, "0.0 AM");
                String latitude = preferences.getString(Constants.PREF_INCIDENT_LATITUDE, "0.0");
                String longitude = preferences.getString(Constants.PREF_INCIDENT_LONGITUDE, "0.0");
                String IncidentImagePath = preferences.getString(Constants.PREF_INCIDENT_PHOTO_PATH, null);

                /* Upload the image to cloudinary first using Django custom API*/
                // Create Retrofit instance
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                // Create an instance of the ApiService
                IncidentImageUploadApiService IncApiService = retrofit.create(IncidentImageUploadApiService.class);

                MultipartBody.Part imagePart = prepareFilePart("image", IncidentImagePath);

                // Make the API call
                Call<IncidentImageResponse> call = IncApiService.uploadImage(imagePart);
                call.enqueue(new Callback<IncidentImageResponse>() {
                    @Override
                    public void onResponse(Call<IncidentImageResponse> call, Response<IncidentImageResponse> response) {
                        if (response.isSuccessful()) {
                            // Handle the imageUrl as needed
                            String imageUrl = response.body().getUrl();
                            // Handle the Body data
                            RealtimeGeoJsonSchema realtimeGeoJsonSchema = new RealtimeGeoJsonSchema(
                                    incidentType1,
                                    severityLevel1,
                                    latitude,
                                    longitude,
                                    imageUrl
                            );
                            showSnackbar("Image uploaded successfully");
                            uploadIncidentGeoData(realtimeGeoJsonSchema);
                        } else {
                            showSnackbar("Image upload failed: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<IncidentImageResponse> call, Throwable t) {
                        showSnackbar("Upload failed. Error: " + t.getMessage());
                    }
                });


            }
        });
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(frameIncidentphoto, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void uploadIncidentGeoData(RealtimeGeoJsonSchema realtimeGeoJsonSchema) {
        IncidentDataUploadApiService apiService = GeoRealTimeRetrofitClient.getApiService();

        // Initialize a Gson object
        Gson gson = new Gson();
        String geoJsonDataString = gson.toJson(realtimeGeoJsonSchema);

        // Create a RequestBody with the JSON string
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), geoJsonDataString);

        // Make the API call
        Call<RealtimeGeoJsonSchema> call = apiService.uploadGeoJsonData(requestBody);
        call.enqueue(new Callback<RealtimeGeoJsonSchema>() {
            @Override
            public void onResponse(Call<RealtimeGeoJsonSchema> call, Response<RealtimeGeoJsonSchema> response) {
                if (response.isSuccessful()) {
                    showSnackbar("Uploaded Incident data successfully!!!");
                    /* Clear the preferences and navigate to select activity screen */
                    AppPreferences preferences_ = AppPreferences.getInstance(IncidentPhotoAndReporting.this);
                    preferences_.clearIncidentPreferences();
                    startActivity(new Intent(IncidentPhotoAndReporting.this, SelectActivityNow.class));
                    finish();
                }else{
                    try {
                        ErrorResponse errorResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                        showSnackbar(errorResponse.getError().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        showSnackbar("Server error: cannot upload incident data at the moment!!!" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<RealtimeGeoJsonSchema> call, Throwable t) {
                showSnackbar("An error occurred, cannot upload the incident data at the moment!!!" + t.getMessage());
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, String filePath) {
        // Create a File object from the file path
        File file = new File(filePath);

        // Create a request body with the file content
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // Create a MultipartBody.Part from the request body
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void loadUnsavedDetails() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Check if there is a path already
        if (preferences.contains(Constants.PREF_INCIDENT_PHOTO_PATH)) {
            // Key has a value, set the TextView to that value
            String new_incident_photo_path = preferences.getString(Constants.PREF_INCIDENT_PHOTO_PATH, "incident-photo-path");
            txtPhotoPath.setText(new_incident_photo_path);

            // Read the image file from the path
            Bitmap bitmap = BitmapFactory.decodeFile(new_incident_photo_path);

            if (bitmap != null) {
                // Set the Bitmap to the ImageView
                selectedImg.setImageBitmap(bitmap);
            } else {
                // Handle the case when decoding the image file fails
                selectedImg.setImageResource(R.drawable.pline); // Set a default image
            }
        } else {
            // Key doesn't have a value, set the TextView to default text
            txtPhotoPath.setText("No incident photo has been recorded!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        String filepath = uri.getPath();
        selectedImg.setImageURI(uri); // show the captured image here

        // save path of transformer
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveIncidentPhotoPath(filepath);

        txtPhotoPath.setText(filepath); // set the photo path to the textview
    }
}
