package com.lowa_softwares.stimasafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;

public class IncidentPhotoAndReporting extends AppCompatActivity {

    ImageView imageGoBack;
    AppCompatButton btnChooseTakePhoto;
    TextView txtPhotoPath;
    ImageView selectedImg;
    AppCompatButton btnReportIncidentNow;

    String [] itemsIncType = {"Transformer Blow-Up","Electric Shock","Electrical driven Fire"};
    String [] itemsSevMeasure = {"High","Mild","Low"};


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

                String dateOfIncident = preferences.getString(Constants.PREF_INCIDENT_DATE, "7/2/2024");
                String timeOfIncident = preferences.getString(Constants.PREF_INCIDENT_TIME, "0.0 AM");
                String latitude = preferences.getString(Constants.PREF_INCIDENT_LATITUDE, "0.0");
                String longitude = preferences.getString(Constants.PREF_INCIDENT_LONGITUDE, "0.0");
                String image_url = preferences.getString(Constants.PREF_INCIDENT_PHOTO_PATH, "no path");

                /* Upload the image to cloudinary first using Django custom API*/

                /* Initialize Gson class for json serialization*/
                Gson gson = new Gson();
                RealtimeGeoJsonSchema realtimeGeoJsonSchema = new RealtimeGeoJsonSchema(
                        incidentType1,
                        severityLevel1,
                        dateOfIncident,
                        timeOfIncident,
                        latitude,
                        longitude,
                        image_url
                );
                /* Generate JSON from the data*/
                String jsonToSubmit = gson.toJson(realtimeGeoJsonSchema);
                Log.d("INCIDENT FINAL DATA TO SUBMIT", jsonToSubmit);
                /* Perform a real-time api call to submit data to database */

                /* Clear the preferences and navigate to select activity screen */
                AppPreferences preferences_ = AppPreferences.getInstance(IncidentPhotoAndReporting.this);
                preferences_.clearIncidentPreferences();
                startActivity(new Intent(IncidentPhotoAndReporting.this, SelectActivityNow.class));
                finish();
            }
        });
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
