package com.lowa_softwares.stimasafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data_upload.ErrorResponse;
import data_upload.GeoDataUploadApiService;
import data_upload.GeoJsonResponse;
import data_upload.GeoRetrofitClient;
import data_upload.ImageUploadApiService;
import data_upload.ImageUploadResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewAndSubmitScreen extends AppCompatActivity {

    AppCompatButton editBtn;
    AppCompatButton submitBtn;
    AppCompatButton btnClearAndRestartTheForm;

    ImageButton editPowerlineBtn, editTransformerBtn, editAdditionalBtn;

    String[] items_state = {"Leaning", "Broken", "Downed"};
    String[] items_structure_type = {"Wooden pole", "Concrete pole"};
    String[] itemsTransformerCondition = {"Healthy", "Unhealthy", "Loose"};

    TextView textViewState;
    TextView textViewPType;
    TextView textViewDateOfCollection;
    TextView textViewLatitude;
    TextView textViewLongitude;
    TextView textViewPowerlinePhoto;
    ImageView powerlineImageFinal;

    TextView textViewTransformerCondition;
    TextView textViewTransformerPhoto;
    ImageView powerlineTransformerImageFinal;

    TextView textViewNoOfBrokenCables;
    TextView textViewCablesPhotoPath;
    ImageView powerlineCablesImageFinal;

    // compile server url
    private static final String BASE_URL = "https://powerline-image-uploader-5d8b8ecfd15f.herokuapp.com"; // Replace with your server URL
    private static final int MAX_IMAGES = 3;

    // Initialize the three photo paths
    String powerlinePhotoPath;
    String transformerPhotoPath;
    String cablesPhotoPath;

    // Initialize variables to hold urls from cloudinary
    String transformerUrlFromCloudinary;
    String powerlineUrlFromCloudinary;
    String cablesUrlFromCloudinary;

    Boolean has_transformer;
    Boolean has_broken_powercables;

    RelativeLayout linearReviewandsubm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_and_submit_form);

        /* This activity is used to fetch collected data from shared preferences and submit to server
         * via a custom Django based API*/

        linearReviewandsubm = findViewById(R.id.linearReviewandsubm);

        editBtn = findViewById(R.id.btnEdit);
        submitBtn = findViewById(R.id.btnSubmitForm);
        btnClearAndRestartTheForm = findViewById(R.id.btnClearAndRestartTheForm);

        // handle id connections from different ui elements
        editPowerlineBtn = findViewById(R.id.editPowerline);
        editTransformerBtn = findViewById(R.id.editTransformer);
        editAdditionalBtn = findViewById(R.id.editAdditionalDetails);

        // IDs inside first cardviews
        textViewState = findViewById(R.id.textViewState);
        textViewPType = findViewById(R.id.textViewPType);
        textViewDateOfCollection = findViewById(R.id.textViewDateOfCollection);
        textViewLatitude = findViewById(R.id.textViewLat1);
        textViewLongitude = findViewById(R.id.textViewLong1);
        textViewPowerlinePhoto = findViewById(R.id.textViewPowerlinePhoto);
        powerlineImageFinal = findViewById(R.id.powerlineImageFinal);

        // second cardview
        textViewTransformerCondition = findViewById(R.id.textViewTransformerCondition);
        textViewTransformerPhoto = findViewById(R.id.textViewTransformerPhoto);
        powerlineTransformerImageFinal = findViewById(R.id.powerlineTransformerImageFinal);

        // third cardview
        textViewNoOfBrokenCables = findViewById(R.id.textViewNoOfBrokenCables);
        textViewCablesPhotoPath = findViewById(R.id.textViewCablesPhoto);
        powerlineCablesImageFinal = findViewById(R.id.powerlineCablesImageFinal);

        // load all the data in the shared preferences
        loadAllUnsavedData();

        editPowerlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReviewAndSubmitScreen.this, RecordFaultyPowerline.class));
            }
        });
        editTransformerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReviewAndSubmitScreen.this, TransformerDetails.class));
            }
        });
        editAdditionalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReviewAndSubmitScreen.this, PowerCableScreen.class));
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReviewAndSubmitScreen.this, PowerCableScreen.class));
            }
        });
        // btn to clear data from shared preferences and restart the whole process
        btnClearAndRestartTheForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Clear data that is in the shared Preferences and restart the whole form*/
                AppPreferences preferences = AppPreferences.getInstance(ReviewAndSubmitScreen.this);
                preferences.clearPreferences();
                /* navigate to select activities screen */
                startActivity(new Intent(ReviewAndSubmitScreen.this, SelectActivityNow.class));
            }
        });

        /* Take data from shared preferences, create a json object out of it, submit data
         * to the server via a custom REST API */
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Fetch all data from shared preferences*/
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ReviewAndSubmitScreen.this);
                /* start getting each key-value pair */
                String cableCount = preferences.getString(Constants.PREF_CABLE_COUNT, null);
                transformerPhotoPath = preferences.getString(Constants.PREF_TRANSFORMER_PHOTO_PATH, null);
                cablesPhotoPath = preferences.getString(Constants.PREF_CABLES_PHOTO_PATH, null);

                int powerlineState = Integer.parseInt(String.valueOf(preferences.getInt(Constants.PREF_POWERLINE_STATE, 0)));
                String state = items_state[powerlineState];
                int powerlineStrType = Integer.parseInt(String.valueOf(preferences.getInt(Constants.PREF_POWERLINE_TYPE, 0)));
                String strType = items_structure_type[powerlineStrType];
                int transformerCondition = Integer.parseInt(String.valueOf(preferences.getInt(Constants.PREF_DROP_TRANSFORMER_COND, 0)));
                String transformer_cond = itemsTransformerCondition[transformerCondition];

                String powerlineRecordDate = preferences.getString(Constants.PREF_POWERLINE_DATE, null);
                String powerlineLatitude = preferences.getString(Constants.PREF_POWERLINE_LATITUDE, null);
                String powerlineLongitude = preferences.getString(Constants.PREF_POWERLINE_LONGITUDE, null);
                String powerlineAccuracy = preferences.getString(Constants.PREF_POWERLINE_ACCURACY, "0");
                String powerlineAltitude = preferences.getString(Constants.PREF_POWERLINE_ALTITUDE, "0");
                powerlinePhotoPath = preferences.getString(Constants.PREF_POWERLINE_PHOTO_PATH, null);

                /* Perform the 3 image uploads to cloudinary, then upload the rest of jsonified data */
                // Dynamically populate the list of image paths based on your criteria
                List<String> imagePaths = getDynamicImagePaths();
                // Create Retrofit instance
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                // Create an instance of the ApiService
                ImageUploadApiService apiService = retrofit.create(ImageUploadApiService.class);

                // Prepare image files for upload
                List<MultipartBody.Part> imageParts = new ArrayList<>();

                // Loop through the images and create parts dynamically
                for (int i = 0; i < Math.min(imagePaths.size(), MAX_IMAGES); i++) {
                    File file = new File(imagePaths.get(i));
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
                    imageParts.add(imagePart);
                }

                // Make the API call
                Call<ImageUploadResponse> call = apiService.uploadImages(
                        imageParts.get(0),
                        imageParts.size() > 1 ? imageParts.get(1) : null,
                        imageParts.size() > 2 ? imageParts.get(2) : null
                );

                // make a POST request to server
                call.enqueue(new Callback<ImageUploadResponse>() {
                    @Override
                    public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                        if (response.isSuccessful()) {
                            List<String> uploadedUrls = response.body().getUrls();
                            // Handle the uploaded URLs as needed
                            powerlineUrlFromCloudinary = uploadedUrls.get(0);
                            // To get the global instance of shared preferences
                            AppPreferences preferences = AppPreferences.getInstance(ReviewAndSubmitScreen.this);
                            // Check if transformer layout is visible
                            if(preferences.isLinearLayoutVisible()){
                                transformerUrlFromCloudinary = uploadedUrls.get(1);
                                has_transformer = true;
                            }else{
                                transformerUrlFromCloudinary = "null";
                                has_transformer = false;
                            }
                            // check if cables layout is visible
                            if(preferences.isCableLinearLayoutVisible()){
                                cablesUrlFromCloudinary = uploadedUrls.get(2);
                                has_broken_powercables = true;
                            }else {
                                cablesUrlFromCloudinary = "null";
                                has_broken_powercables = false;
                            }
                            Gson gson = new Gson();
                            GeoJsonDataSchema geoJsonDataSchema = new GeoJsonDataSchema(
                                    state,
                                    strType,
                                    powerlineRecordDate,
                                    powerlineLatitude,
                                    powerlineLongitude,
                                    powerlineUrlFromCloudinary,
                                    transformer_cond,
                                    transformerUrlFromCloudinary,
                                    cableCount,
                                    cablesUrlFromCloudinary,
                                    powerlineAccuracy,
                                    powerlineAltitude,
                                    has_broken_powercables,
                                    has_transformer
                            );
                            showSnackbar("Image(s) uploaded successfully");
                            uploadPowerlineGeoData(geoJsonDataSchema);
                        } else {
                            showSnackbar("Image(s) upload failed! Data wont be uploaded also!!"+response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                        showSnackbar("Image upload failed: "+ t.getMessage());
                    }
                });

            }
        });
    }

    private void uploadPowerlineGeoData(GeoJsonDataSchema geoJsonDataSchema) {
        // Get the Retrofit API service
        GeoDataUploadApiService geoApiService = GeoRetrofitClient.getApiService();

        // Initialize a Gson object
        Gson gson = new Gson();
        String geoJsonDataString = gson.toJson(geoJsonDataSchema);

        // Create a RequestBody with the JSON string
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), geoJsonDataString);

        // Make the API call
        Call<GeoJsonResponse> call = geoApiService.uploadGeoJsonData(requestBody);
        call.enqueue(new Callback<GeoJsonResponse>() {
            @Override
            public void onResponse(Call<GeoJsonResponse> call, Response<GeoJsonResponse> response) {
                if (response.isSuccessful()) {
                    showSnackbar("Uploaded powerline data successfully!!!");
                    //* Clear share preferences and navigate user to Select activity after successful data upload */
                    AppPreferences preferences_ = AppPreferences.getInstance(ReviewAndSubmitScreen.this);
                    preferences_.clearPreferences();
                    startActivity(new Intent(ReviewAndSubmitScreen.this, SelectActivityNow.class));
                }else {
                    try {
                        ErrorResponse errorResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                        showSnackbar(errorResponse.getError().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        showSnackbar("Server error: cannot upload the data at the moment!!!" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<GeoJsonResponse> call, Throwable t) {
                showSnackbar("An error occurred, cannot upload the data at the moment." + t.getMessage());
            }
        });
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(linearReviewandsubm, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }



    // Implement logic to dynamically populate the list of image paths
    private List<String> getDynamicImagePaths() {
        List<String> dynamicPaths = new ArrayList<>();
        if (powerlinePhotoPath != null) {
            dynamicPaths.add(powerlinePhotoPath);
        }
        if(transformerPhotoPath != null){ // present
            dynamicPaths.add(transformerPhotoPath);
        }
        if(cablesPhotoPath != null){ //present
            dynamicPaths.add(cablesPhotoPath);
        }
        return dynamicPaths;
    }

    private void loadAllUnsavedData() {
        /* Load all the data */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        /* start getting each key-value pair */
        String cableCount = preferences.getString(Constants.PREF_CABLE_COUNT, null);
        String transformerPhotoPath = preferences.getString(Constants.PREF_TRANSFORMER_PHOTO_PATH, "transformer-photo-path");
        String cablesPhotoPath = preferences.getString(Constants.PREF_CABLES_PHOTO_PATH, "cables-photo-path");

        int powerlineState = Integer.parseInt(String.valueOf(preferences.getInt(Constants.PREF_POWERLINE_STATE, 0)));
        String state = items_state[powerlineState];
        int powerlineStrType = Integer.parseInt(String.valueOf(preferences.getInt(Constants.PREF_POWERLINE_TYPE, 0)));
        String strType = items_structure_type[powerlineStrType];
        int transformerCondition = Integer.parseInt(String.valueOf(preferences.getInt(Constants.PREF_DROP_TRANSFORMER_COND, 0)));
        String transformer_cond = itemsTransformerCondition[transformerCondition];

        String powerlineRecordDate = preferences.getString(Constants.PREF_POWERLINE_DATE, null);
        String powerlineLatitude = preferences.getString(Constants.PREF_POWERLINE_LATITUDE, null);
        String powerlineLongitude = preferences.getString(Constants.PREF_POWERLINE_LONGITUDE, null);
        String powerlineAccuracy = preferences.getString(Constants.PREF_POWERLINE_ACCURACY, "accuracy");
        String powerlineAltitude = preferences.getString(Constants.PREF_POWERLINE_ALTITUDE, "altitude");
        String powerlinePhotoPath = preferences.getString(Constants.PREF_POWERLINE_PHOTO_PATH, "powerline-photo-path");

        // now populate the id with the correct data and imageViews
        textViewState.setText(String.format("State of powerline: %s", state));
        textViewPType.setText(String.format("Structure type: %s", strType));
        textViewDateOfCollection.setText(String.format("Date of collection: %s", powerlineRecordDate));
        textViewLatitude.setText(String.format("Latitude: %s", powerlineLatitude));
        textViewLongitude.setText(String.format("Longitude: %s", powerlineLongitude));
        textViewPowerlinePhoto.setText(String.format("Powerline photo path: %s", powerlinePhotoPath));
        if (preferences.contains(Constants.PREF_POWERLINE_PHOTO_PATH)) {
            // Read the image file from the path
            Bitmap bitmap = BitmapFactory.decodeFile(powerlinePhotoPath);
            if (bitmap != null) {
                // Set the Bitmap to the ImageView
                powerlineImageFinal.setImageBitmap(bitmap);
            } else {
                // Handle the case when decoding the image file fails
                powerlineImageFinal.setImageResource(R.drawable.no_image); // Set a default image
            }
        } else {
            textViewPowerlinePhoto.setText(R.string.no_photo_path);
        }

        // populate transformer cardview
        textViewTransformerCondition.setText(String.format("Transformer condition: %s", transformer_cond));
        if (preferences.contains(Constants.PREF_TRANSFORMER_PHOTO_PATH)) {
            textViewTransformerPhoto.setText(String.format("Transformer photo path: %s", transformerPhotoPath));
            // Read the image file from the path
            Bitmap bitmap = BitmapFactory.decodeFile(transformerPhotoPath);
            if (bitmap != null) {
                // Set the Bitmap to the ImageView
                powerlineTransformerImageFinal.setImageBitmap(bitmap);
            } else {
                // Handle the case when decoding the image file fails
                powerlineTransformerImageFinal.setImageResource(R.drawable.no_image); // Set a default image
            }
        } else {
            textViewTransformerPhoto.setText(R.string.no_photo_path);
        }

        // populate cables cardview
        textViewNoOfBrokenCables.setText(String.format("No. of broken cables: %s", cableCount));
        if (preferences.contains(Constants.PREF_CABLES_PHOTO_PATH)) {
            textViewCablesPhotoPath.setText(String.format("Powerline cables photo path: %s", cablesPhotoPath));
            // Read the image file from the path
            Bitmap bitmap = BitmapFactory.decodeFile(cablesPhotoPath);
            if (bitmap != null) {
                // Set the Bitmap to the ImageView
                powerlineCablesImageFinal.setImageBitmap(bitmap);
            } else {
                // Handle the case when decoding the image file fails
                powerlineCablesImageFinal.setImageResource(R.drawable.no_image); // Set a default image
            }
        } else {
            textViewCablesPhotoPath.setText(R.string.no_photo_path);
        }
    }
}
