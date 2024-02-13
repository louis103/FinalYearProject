package com.lowa_softwares.stimasafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.github.dhaval2404.imagepicker.ImagePicker;

public class PowerCableScreen extends AppCompatActivity {
    AppCompatButton backBtn;
    AppCompatButton btnReviewDetails;

    LinearLayout toggleCableDetailsLayout;
    RadioGroup radioGroup;
    ImageView selectedImgCables;
    EditText etCountCables;

    AppCompatButton btnChooseTakePhoto;
    TextView txtPhotoPath;

    RadioButton yesRB,noRB;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_powercables_screen);

        backBtn = findViewById(R.id.btnBack);
        btnReviewDetails = findViewById(R.id.btnReviewDetails);

        toggleCableDetailsLayout = findViewById(R.id.toggleCableDetailsLayout);
        radioGroup = findViewById(R.id.toggleIfCables);
        selectedImgCables = findViewById(R.id.selectedImg);
        etCountCables = findViewById(R.id.etCountCables);
        btnChooseTakePhoto = findViewById(R.id.btnChoosePhoto);
        txtPhotoPath = findViewById(R.id.txtPhotoPath);

        // radio button IDs
        yesRB = findViewById(R.id.yes);
        noRB = findViewById(R.id.no);

        // To get the instance of global shared preferences
        AppPreferences preferences = AppPreferences.getInstance(this);

        // check linear layout if visible or not
        checkLinearLayout();

        // Load unsaved values from shared preferences
        loadUnsavedChanges();

        // get the text of edit text
        etCountCables.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Save the user-typed text to SharedPreferences after text changes
                saveNoofCables(editable.toString());
            }
        });
        // save the text when focus is gone
        etCountCables.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // Save the user-typed text to SharedPreferences when the EditText loses focus
                if (!b) {
                    saveNoofCables(etCountCables.getText().toString());
                }
            }
        });

        // navigation
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PowerCableScreen.this, TransformerDetails.class));
            }
        });
        btnReviewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PowerCableScreen.this, ReviewAndSubmitScreen.class));
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
                        preferences.setCableLinearLayoutVisible(true);
                        toggleCableDetailsLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        // Save state as invisible
                        preferences.setCableLinearLayoutVisible(false);
                        toggleCableDetailsLayout.setVisibility(View.GONE);
                        removeDataIfAny();
                        break;
                }
            }
        });
        btnChooseTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(PowerCableScreen.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    private void loadUnsavedChanges() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String edtCableCountKey = "faulty_cables_count";
        if (preferences.contains(edtCableCountKey)){
            String new_cable_count = preferences.getString(edtCableCountKey, "cable-count-value");
            etCountCables.setText(new_cable_count);
        }

        // load the faulty cables photo path if there is
        String cable_photo_key = "faulty_cables_path";
        // Check if the key has a value
        if (preferences.contains(cable_photo_key)) {
            // Key has a value, set the TextView to that value
            String new_cables_path = preferences.getString(cable_photo_key, "cable-photo-path");
            txtPhotoPath.setText(new_cables_path);
            // Read the image file from the path
            Bitmap bitmap = BitmapFactory.decodeFile(new_cables_path);

            if (bitmap != null) {
                // Set the Bitmap to the ImageView
                selectedImgCables.setImageBitmap(bitmap);
            } else {
                // Handle the case when decoding the image file fails
                selectedImgCables.setImageResource(R.drawable.pline); // Set a default image
            }
        } else {
            // Key doesn't have a value, set the TextView to default text
            txtPhotoPath.setText("No cables photo has been selected!");
        }
    }

    private void checkLinearLayout() {
        AppPreferences preferences = AppPreferences.getInstance(this);
        boolean isVisible = preferences.isCableLinearLayoutVisible();

        // Set radio button and linear layout state
        if (isVisible) {
            yesRB.setChecked(true);
            toggleCableDetailsLayout.setVisibility(View.VISIBLE);
        } else {
            noRB.setChecked(true);
            toggleCableDetailsLayout.setVisibility(View.GONE);
            removeDataIfAny();
        }
    }

    private void removeDataIfAny() {
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.clearCableDetailIfPreviouslyFilled();
    }

    private void saveNoofCables(String text) {
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveCablesCount(text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        String filepath = uri.getPath();
        selectedImgCables.setImageURI(uri); // show the captured image here

        // save path of transformer
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveCablesPhotoPath(filepath);

        txtPhotoPath.setText(filepath); // set the photo path to the textview
    }
}
