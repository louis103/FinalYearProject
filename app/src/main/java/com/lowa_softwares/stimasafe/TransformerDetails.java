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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputLayout;

public class TransformerDetails extends AppCompatActivity {
    private RadioButton yesRadioButton, noRadioButton;
    AppCompatButton btnBack;
    AppCompatButton nxtBack;
    LinearLayout toggleTransformerDetailsLayout;
    RadioGroup radioGroup;
    ImageView selectedImgTransformer;

    AppCompatButton btnChooseTakePhoto;
    TextView txtPhotoPath;

    String [] itemsCond = {"Healthy","Unhealthy","Loose"};

    AutoCompleteTextView autoCompleteTextViewCond;
    ArrayAdapter<String> adapterItemsCondition;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_transformer_screen);

        btnBack = findViewById(R.id.btnBack);
        nxtBack = findViewById(R.id.btnNext);
        toggleTransformerDetailsLayout = findViewById(R.id.toggleTransformerDetailsLayout);
        radioGroup = findViewById(R.id.toggleIfTransformer);
        btnChooseTakePhoto = findViewById(R.id.btnChoosePhoto);
        txtPhotoPath = findViewById(R.id.txtPhotoPath);
        selectedImgTransformer = findViewById(R.id.selectedImgTransformer);
        yesRadioButton = findViewById(R.id.yes);
        noRadioButton = findViewById(R.id.no);

        // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // To get the instance
        AppPreferences preferences = AppPreferences.getInstance(this);

        // check for state from shared preferences
        loadState();

        // Handle condition and photo,photo path
        autoCompleteTextViewCond = findViewById(R.id.auto_complete_txt_condition);
        // Populate dropdown menu
        adapterItemsCondition = new ArrayAdapter<String>(this, R.layout.list_item, itemsCond);
        autoCompleteTextViewCond.setAdapter(adapterItemsCondition);
        autoCompleteTextViewCond.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // save a drop down item to shared preferences
                preferences.saveDropdownValueTransformerCondition(i);
            }
        });

        // Load unsaved values from shared preferences
        loadTransformerUnsavedDetails();

        // Handle navigation
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TransformerDetails.this, RecordPowerlinePhoto.class));
            }
        });
        nxtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TransformerDetails.this, PowerCableScreen.class));
            }
        });

        // Handle radio group and layout visibility view
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //RadioButton rBtn = findViewById(i);
                switch (i){
                    case R.id.yes:
                        // Save state as visible
                        preferences.setLinearLayoutVisible(true);
                        toggleTransformerDetailsLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        // Save state as invisible
                        preferences.setLinearLayoutVisible(false);
                        removeDataIfAny();
                        toggleTransformerDetailsLayout.setVisibility(View.GONE);
                        break;
                }
            }
        });

        btnChooseTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(TransformerDetails.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    private void loadTransformerUnsavedDetails() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int savedPosition = preferences.getInt(Constants.PREF_DROP_TRANSFORMER_COND, 0);
        // Set the saved position
        autoCompleteTextViewCond.setText(adapterItemsCondition.getItem(savedPosition), false);
        // load the transformer photo path if there is
        String transformer_photo_key = "transformer_photo_path";
        // Check if the key has a value
        if (preferences.contains(transformer_photo_key)) {
            // Key has a value, set the TextView to that value
            String new_transformer_path = preferences.getString(transformer_photo_key, "transformer-photo-path");
            txtPhotoPath.setText(new_transformer_path);
            // Read the image file from the path
            Bitmap bitmap = BitmapFactory.decodeFile(new_transformer_path);

            if (bitmap != null) {
                // Set the Bitmap to the ImageView
                selectedImgTransformer.setImageBitmap(bitmap);
            } else {
                // Handle the case when decoding the image file fails
                selectedImgTransformer.setImageResource(R.drawable.pline); // Set a default image
            }
        } else {
            // Key doesn't have a value, set the TextView to default text
            txtPhotoPath.setText("No transformer photo has been selected!");
        }
    }

    private void loadState() {
        AppPreferences preferences = AppPreferences.getInstance(this);
        boolean isVisible = preferences.isLinearLayoutVisible();

        // Set radio button and linear layout state
        if (isVisible) {
            yesRadioButton.setChecked(true);
            toggleTransformerDetailsLayout.setVisibility(View.VISIBLE);
        } else {
            noRadioButton.setChecked(true);
            removeDataIfAny();
            toggleTransformerDetailsLayout.setVisibility(View.GONE);
        }
    }

    private void removeDataIfAny() {
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.clearTransformerDetailIfPreviouslyFilled();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        String filepath = uri.getPath();
        selectedImgTransformer.setImageURI(uri); // show the captured image here
        // save path of transformer
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveTransformerPhotoPath(filepath);
        // set the photo path to the textview
        txtPhotoPath.setText(filepath);
    }
}
