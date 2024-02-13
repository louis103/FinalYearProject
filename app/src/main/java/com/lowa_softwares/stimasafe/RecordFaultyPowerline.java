package com.lowa_softwares.stimasafe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.Calendar;

import pl.droidsonroids.gif.GifImageView;

public class RecordFaultyPowerline extends AppCompatActivity implements LocationListener {

    AppCompatButton backBtn;
    AppCompatButton nxtBtn;
    AppCompatButton btnRecordLocationOfPowerline;

    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteTextView autoCompleteTextViewStrType;
    ArrayAdapter<String> adapterItems;
    ArrayAdapter<String> adapterStrTypeItems;

    String [] items = {"Leaning","Broken","Downed"};
    String [] items_str_type = {"Wooden pole","Concrete pole"};

    AppCompatButton btnSelectDate;
    EditText selectDate1;
    int mYear,mMonth,mDay;

    LocationManager locationManager;
    Dialog dialog;
    EditText editTxtLatitude1;
    EditText editTxtLongitude1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_faulty_screen);

        backBtn = findViewById(R.id.btnBack);
        nxtBtn = findViewById(R.id.btnNext);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        selectDate1 = findViewById(R.id.selectDate1);
        btnRecordLocationOfPowerline = findViewById(R.id.btnRecordLocationOfPowerline);

        editTxtLatitude1 = findViewById(R.id.editTxtLatitude1);
        editTxtLongitude1 = findViewById(R.id.editTxtLongitude1);

        // handling location finder dialog
        dialog = new Dialog(RecordFaultyPowerline.this);
        dialog.setContentView(R.layout.location_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); // make sure the dialog cannot be closed by clicking outside
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.getWindow().getAttributes().horizontalMargin = 50;

        Button cancelDialog = dialog.findViewById(R.id.cancelButton);
        // handle popup onclicks
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        //Runtime permissions
        if (ContextCompat.checkSelfPermission(RecordFaultyPowerline.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RecordFaultyPowerline.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        // handle navigation
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecordFaultyPowerline.this, SelectActivityNow.class));
            }
        });
        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecordFaultyPowerline.this, RecordPowerlinePhoto.class));
            }
        });

        // To get the global instance of shared preferences
        AppPreferences preferences = AppPreferences.getInstance(this);

        //Handle dropdown 1
        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, items);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                preferences.saveDropdownValuePowerlineState(i);
            }
        });

        // handle dropdown 2
        autoCompleteTextViewStrType = findViewById(R.id.auto_complete_txt_str_type);
        adapterStrTypeItems = new ArrayAdapter<String>(this, R.layout.list_item, items_str_type);
        autoCompleteTextViewStrType.setAdapter(adapterStrTypeItems);
        autoCompleteTextViewStrType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                preferences.saveDropdownValuePowerlineStrType(i);
            }
        });

        // Load unsaved values from shared preferences
        loadUnsavedChanges();

        // function for data picker
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });
        // handle functionality for when date is selected
        selectDate1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveDate(editable.toString());
            }
        });
        selectDate1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    saveDate(selectDate1.getText().toString());
                }
            }
        });

        // handle saving typed coordinates
        editTxtLatitude1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveLat(editable.toString());
            }
        });
        editTxtLatitude1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    saveLat(editTxtLatitude1.getText().toString());
                }
            }
        });

        // longitude changed
        editTxtLongitude1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveLong(editable.toString());
            }
        });
        editTxtLongitude1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    saveLong(editTxtLongitude1.getText().toString());
                }
            }
        });


        // handle location acquisition
        btnRecordLocationOfPowerline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                getMyLocation();
            }
        });
    }

    private void saveLat(String latitude) {
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveLat(latitude);
    }
    private void saveLong(String longitude) {
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveLong(longitude);
    }

    private void loadUnsavedChanges() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int savedPositionState = preferences.getInt(Constants.PREF_POWERLINE_STATE, 0);
        int savedPositionType = preferences.getInt(Constants.PREF_POWERLINE_TYPE, 0);

        // set dropdowns to the retrieved values (for state and structural type)
        autoCompleteTextView.setText(adapterItems.getItem(savedPositionState), false);
        autoCompleteTextViewStrType.setText(adapterStrTypeItems.getItem(savedPositionType), false);

        // handle date value
        if (preferences.contains(Constants.PREF_POWERLINE_DATE)){
            // Key has a value date, set the date TextView to that value
            String new_date = preferences.getString(Constants.PREF_POWERLINE_DATE, "06/02/2024");
            selectDate1.setText(new_date);
        }

        // handle saved coordinates
        // for latitude
        if (preferences.contains(Constants.PREF_POWERLINE_LATITUDE)){
            // Key has a value date, set the date TextView to that value
            String new_latitude = preferences.getString(Constants.PREF_POWERLINE_LATITUDE, "0.0");
            editTxtLatitude1.setText(new_latitude);
        }
        // for longitude
        if (preferences.contains(Constants.PREF_POWERLINE_LONGITUDE)){
            // Key has a value date, set the date TextView to that value
            String new_longitude = preferences.getString(Constants.PREF_POWERLINE_LONGITUDE, "0.0");
            editTxtLongitude1.setText(new_longitude);
        }
    }

    private void saveDate(String date) {
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveSelectedDate(date);
    }

    @SuppressLint("MissingPermission")
    private void getMyLocation() {
        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, RecordFaultyPowerline.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month+=1;
                //Showing the picked value in the textView
                selectDate1.setText(String.format("%s/%s/%s", String.valueOf(day), String.valueOf(month), String.valueOf(year)));

            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            String lat = String.valueOf(location.getLatitude());
            String lon = String.valueOf(location.getLongitude());
            String accuracy = String.valueOf(location.getAccuracy());
            String altitude = String.valueOf(location.getAltitude());

            TextView latitudeTextView = dialog.findViewById(R.id.latitudeTextView);
            TextView longitudeTextView = dialog.findViewById(R.id.longitudeTextView);
            TextView accuracyTextView = dialog.findViewById(R.id.accuracyTextView);
            TextView altitudeTextView = dialog.findViewById(R.id.altitudeTextView);

            Button okButton = dialog.findViewById(R.id.okButton);

            GifImageView gifImageView = dialog.findViewById(R.id.animatedIcon);

            latitudeTextView.setText(String.format("Latitude: %s", lat));
            longitudeTextView.setText(String.format("Longitude: %s", lon));
            accuracyTextView.setText(String.format("Accuracy: %sm", accuracy));
            altitudeTextView.setText(String.format("Altitude: %sm", altitude));

            // change the success icon
            gifImageView.setImageResource(R.drawable.location_achieved);
            okButton.setEnabled(true); // enable the ok button

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    editTxtLatitude1.setText(lat);
                    editTxtLongitude1.setText(lon);
                    // function to save the location data to shared preferences
                    saveLocationData(lat, lon, accuracy, altitude);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Error Occurred: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLocationData(String lat, String lon, String accuracy, String altitude) {
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveLocationData(lat, lon, accuracy, altitude);
    }

}
