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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.Gson;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_and_submit_form);

        /* This activity is used to fetch collected data from shared preferences and submit to server
         * via a custom Django based API*/

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
                String powerlineAccuracy = preferences.getString(Constants.PREF_POWERLINE_ACCURACY, "0");
                String powerlineAltitude = preferences.getString(Constants.PREF_POWERLINE_ALTITUDE, "0");
                String powerlinePhotoPath = preferences.getString(Constants.PREF_POWERLINE_PHOTO_PATH, "powerline-photo-path");

                /* Perform the 3 image uploads to cloudinary, then upload the rest of jsonified data */

                /* Create JSON using Google's Gson library */
                Gson gson = new Gson();
                GeoJsonDataSchema geoJsonDataSchema = new GeoJsonDataSchema(
                        state,
                        strType,
                        powerlineRecordDate,
                        powerlineLatitude,
                        powerlineLongitude,
                        powerlinePhotoPath,
                        transformer_cond,
                        transformerPhotoPath,
                        cableCount,
                        cablesPhotoPath,
                        powerlineAccuracy,
                        powerlineAltitude
                );
                /* Generate JSON from the data*/
                String jsonToSubmit = gson.toJson(geoJsonDataSchema);
                Log.d("POWERLINE FINAL DATA TO SUBMIT", jsonToSubmit);
                /* Create a Retrofit API object for submitting data */

                /* Create a non-dismissive popup and upload data to server */

                /* Clear share preferences and navigate user to Select activity after successful data upload */
                AppPreferences preferences_ = AppPreferences.getInstance(ReviewAndSubmitScreen.this);
                preferences_.clearPreferences();
                startActivity(new Intent(ReviewAndSubmitScreen.this, SelectActivityNow.class));
            }
        });
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
