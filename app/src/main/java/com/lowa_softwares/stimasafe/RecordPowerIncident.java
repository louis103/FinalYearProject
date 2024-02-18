package com.lowa_softwares.stimasafe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

import pl.droidsonroids.gif.GifImageView;

public class RecordPowerIncident extends AppCompatActivity implements LocationListener {

    ImageView imageGoBack;
    ImageView btnNext;

    AppCompatButton btnSelectDate;
    AppCompatButton btnSelectTime;
    AppCompatButton btnGetPowerlineLoc;

    String [] itemsIncType = {"Transformer Blow-Up","Electric Shock","Electrical driven Fire"};
    String [] itemsSevMeasure = {"High","Mild","Low"};

    AutoCompleteTextView autoCompleteTextView1;
    AutoCompleteTextView autoCompleteTextView2;

    ArrayAdapter<String> adapterItemsIncType;
    ArrayAdapter<String> adapterItemsSeverity;

    int mYear,mMonth,mDay, mHour,mMinute;
    EditText edtxDate;
    EditText edtxTime;

    LocationManager locationManager;
    Dialog dialog;
    EditText editTxtLatitude1;
    EditText editTxtLongitude1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_power_incident);

        imageGoBack = findViewById(R.id.imageGoBack);
        btnNext = findViewById(R.id.imageGoForward);


        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnGetPowerlineLoc = findViewById(R.id.btnRecordLocationOfPowerline);

        edtxDate = findViewById(R.id.edtxDate);
        edtxTime = findViewById(R.id.edtxTime);

        editTxtLatitude1 = findViewById(R.id.etLatitude2);
        editTxtLongitude1 = findViewById(R.id.etLongitude2);

        // handling location finder dialog
        dialog = new Dialog(RecordPowerIncident.this);
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

        mHour = cal.get(Calendar.HOUR);
        mMinute = cal.get(Calendar.MINUTE);

        // Handle runtime permissions
        if (ContextCompat.checkSelfPermission(RecordPowerIncident.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RecordPowerIncident.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        // handle navigation
        imageGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecordPowerIncident.this, SelectActivityNow.class);
                startActivity(intent);
                finish();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecordPowerIncident.this, IncidentPhotoAndReporting.class));
            }
        });
        // To get the global instance of shared preferences
        AppPreferences preferences = AppPreferences.getInstance(this);

        //Handle two dropdowns
        autoCompleteTextView1 = findViewById(R.id.auto_complete_txt_incType);
        adapterItemsIncType = new ArrayAdapter<String>(this, R.layout.list_item, itemsIncType);
        autoCompleteTextView1.setAdapter(adapterItemsIncType);

        autoCompleteTextView2 = findViewById(R.id.auto_complete_txt_severity);
        adapterItemsSeverity = new ArrayAdapter<String>(this, R.layout.list_item, itemsSevMeasure);
        autoCompleteTextView2.setAdapter(adapterItemsSeverity);

        autoCompleteTextView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                preferences.saveDropdownValueIncidentType(i);
            }
        });
        autoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                preferences.saveDropdownValueSeverityLevel(i);
            }
        });

        // Load unsaved values from shared preferences
        loadUnsavedChanges();

        // Handle Date, time and location
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });
        btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker();
            }
        });
        btnGetPowerlineLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                getMyLocation();
            }
        });

        // save date and time when edit text changes
        edtxTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveIncidentTime(editable.toString());
            }
        });
        edtxTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    saveIncidentTime(edtxTime.getText().toString());
                }
            }
        });
        // for date
        edtxDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveIncidentDate(editable.toString());
            }
        });
        edtxDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    saveIncidentDate(edtxDate.getText().toString());
                }
            }
        });

        // handling coordinates changes through typing
        editTxtLatitude1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveLatInc(editable.toString());
            }
        });
        editTxtLatitude1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    saveLatInc(editTxtLatitude1.getText().toString());
                }
            }
        });

        editTxtLongitude1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveLongInc(editable.toString());
            }
        });
        editTxtLongitude1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    saveLongInc(editTxtLongitude1.getText().toString());
                }
            }
        });
    }

    private void saveLongInc(String longitude) {
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveLongInc(longitude);
    }

    private void saveLatInc(String latitude) {
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveLatInc(latitude);
    }

    private void loadUnsavedChanges() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int savedPositionIncType = preferences.getInt(Constants.PREF_INCIDENT_TYPE, 0);
        int savedPositionIncSevLev = preferences.getInt(Constants.PREF_INCIDENT_SEVERITY_LEVEL, 0);

        // set dropdowns to the retrieved values (for state and structural type)
        autoCompleteTextView1.setText(adapterItemsIncType.getItem(savedPositionIncType), false);
        autoCompleteTextView2.setText(adapterItemsSeverity.getItem(savedPositionIncSevLev), false);

        // date
        if (preferences.contains(Constants.PREF_INCIDENT_DATE)){
            // Key has a value date, set the date TextView to that value
            String new_date = preferences.getString(Constants.PREF_INCIDENT_DATE, "06/02/2024");
            edtxDate.setText(new_date);
        }
        // time
        if (preferences.contains(Constants.PREF_INCIDENT_TIME)){
            // Key has a value date, set the date TextView to that value
            String new_date = preferences.getString(Constants.PREF_INCIDENT_TIME, "0.0 AM");
            edtxTime.setText(new_date);
        }
        // latitude
        if (preferences.contains(Constants.PREF_INCIDENT_LATITUDE)){
            // Key has a value date, set the date TextView to that value
            String new_latitude = preferences.getString(Constants.PREF_INCIDENT_LATITUDE, "0.0");
            editTxtLatitude1.setText(new_latitude);
        }
        // longitude
        if (preferences.contains(Constants.PREF_INCIDENT_LONGITUDE)){
            // Key has a value date, set the date TextView to that value
            String new_longitude = preferences.getString(Constants.PREF_INCIDENT_LONGITUDE, "0.0");
            editTxtLongitude1.setText(new_longitude);
        }
    }

    @SuppressLint("MissingPermission")
    private void getMyLocation() {
        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, RecordPowerIncident.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String am_pm = (hour < 12) ? "AM" : "PM";

                //Showing the picked value in the textView
                String full_time = String.valueOf(hour) + ":" + String.valueOf(minute) + " " + am_pm;
                edtxTime.setText(full_time);
                // save time to shared preference
                saveIncidentTime(full_time);

            }
        }, mHour, mMinute, false);

        timePickerDialog.show();
    }

    private void saveIncidentTime(String full_time) {
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveIncidentTime(full_time);
    }

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month+=1;
                //Showing the picked value in the textView
                edtxDate.setText(String.format("%s/%s/%s", String.valueOf(day), String.valueOf(month), String.valueOf(year)));
                // save the date to shared preference
                saveIncidentDate(String.format("%s/%s/%s", String.valueOf(day), String.valueOf(month), String.valueOf(year)));

            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void saveIncidentDate(String full_date) {
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveIncidentDate(full_date);
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
            accuracyTextView.setText(String.format("Accuracy(m): %sm", accuracy));
            altitudeTextView.setText(String.format("Altitude(m): %sm", altitude));

            // change the success icon
            gifImageView.setImageResource(R.drawable.location_achieved);
            okButton.setEnabled(true); // enable the ok button

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    editTxtLatitude1.setText(lat);
                    editTxtLongitude1.setText(lon);

                    // save latitude and longitude to the
                    saveIncidentLocation(lat, lon);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Error Occurred: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveIncidentLocation(String lat, String lon) {
        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.saveIncidentLocation(lat, lon);
    }
}
