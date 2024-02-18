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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.github.dhaval2404.imagepicker.ImagePicker;

public class RecordPowerlinePhoto extends AppCompatActivity {

    AppCompatButton btnBack;
    AppCompatButton nxtBack;
    AppCompatButton btnChooseTakePhoto;
    ImageView selectedImage;
    TextView txtPhotoPath;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_powerline_photo_screen);

        btnBack = findViewById(R.id.btnBack);
        nxtBack = findViewById(R.id.btnNext);
        btnChooseTakePhoto = findViewById(R.id.btnChooseTakePhoto);
        selectedImage = findViewById(R.id.selectedImg);
        txtPhotoPath = findViewById(R.id.txtPhotoPath);

        // load the path and image if they are available in the shared preferences
        loadUnsavedData();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecordPowerlinePhoto.this, RecordFaultyPowerline.class));
            }
        });
        nxtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecordPowerlinePhoto.this, TransformerDetails.class));
            }
        });

        // Handle selecting/ taking a photo from android phone
        btnChooseTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(RecordPowerlinePhoto.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

    }

    private void loadUnsavedData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // load the powerline photo path if there is
        String powerline_photo_key = "powerline_photo_path";
        // Check if the key has a value
        if (preferences.contains(powerline_photo_key)){
            // Key has a value, set the TextView to that value
            String new_powerline_path = preferences.getString(powerline_photo_key, "powerline-photo-path");
            txtPhotoPath.setText(new_powerline_path);

            // Read the image file from the path
            Bitmap bitmap = BitmapFactory.decodeFile(new_powerline_path);
            if (bitmap != null) {
                // Set the Bitmap to the ImageView
                selectedImage.setImageBitmap(bitmap);
            } else {
                // Handle the case when decoding the image file fails
                selectedImage.setImageResource(R.drawable.pline); // Set a default image
            }
        }else{
            // Key doesn't have a value, set the TextView to default text
            txtPhotoPath.setText("No powerline photo has been selected!");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        String filepath = uri.getPath();
        selectedImage.setImageURI(uri); // show the captured image here
        // save path of transformer
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.savePowerlinePhotoPath(filepath);
        txtPhotoPath.setText(filepath); // set the photo path to the textview
    }
}
