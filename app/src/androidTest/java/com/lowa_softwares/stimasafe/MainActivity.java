package com.lowa_softwares.stimasafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView text;
    private Button buttonTime;
    private Button buttonDate;
    String [] items = {"Material","Design","Components","Android","5.0 Lollipop"};

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    Button button_location;
    TextView showLocation;
    LocationManager locationManager;

    Button button_get_image;
    ImageView imageView;

    //dialog
    Dialog dialog;

    int mYear,mMonth,mDay;
    int mHour,mMinute;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.showText);
        buttonTime = findViewById(R.id.buttonTime);
        buttonDate = findViewById(R.id.buttonDate);

        showLocation = findViewById(R.id.text_location);
        button_location = findViewById(R.id.button_location);

        button_get_image = findViewById(R.id.button_get_image);
        imageView = findViewById(R.id.imageView);

        Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        mHour = cal.get(Calendar.HOUR);
        mMinute = cal.get(Calendar.MINUTE);

        //Runtime permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        buttonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker(); //Open time picker dialog
            }
        });

        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(); // Open date picker dialog
            }
        });

        //Handle dropdown
        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, items);

        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(MainActivity.this, "Item: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        // handling location finder dialog
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.location_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); // make sure the dialog cannot be closed by clicking outside
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.getWindow().getAttributes().horizontalMargin = 50;

        ImageButton okButton = dialog.findViewById(R.id.okButton);
        ImageButton cancelDialog = dialog.findViewById(R.id.cancelButton);



        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Saved coordinates!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Cancelled operation!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Add onclick listener to location button
        button_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Try to show the popup dialog for collecting coordinates
                dialog.show();
                // request location updates
                getMyLocation();
            }
        });

        // retrieve image
        button_get_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getMyLocation() {

        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
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
                text.setText(String.valueOf(year) + "." + String.valueOf(month) + "." + String.valueOf(day));

            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }


    private void openTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String am_pm = (hour < 12) ? "AM" : "PM";


                //Showing the picked value in the textView
                text.setText(String.valueOf(hour)+ ":"+String.valueOf(minute) +" " + am_pm);

            }
        }, mHour, mMinute, false);

        timePickerDialog.show();
    }

    // Handling location requests
    @Override
    public void onLocationChanged(@NonNull Location location) {
//        Toast.makeText(this, "" + location.getLatitude()+","+location.getLongitude()+","+location.getAccuracy()+","+location.getAltitude(), Toast.LENGTH_SHORT).show();
        try {
//            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            String address = addresses.get(0).getAddressLine(0);


            // showLocation.setText(location.getLatitude()+","+location.getLongitude()+","+location.getAccuracy()+","+location.getAltitude());
            // populate the dialog items
            String lat = String.valueOf(location.getLatitude());
            String lon = String.valueOf(location.getLongitude());
            String accuracy = String.valueOf(location.getAccuracy());
            String altitude = String.valueOf(location.getAltitude());
            String bearing = String.valueOf(location.getBearing());

            // set the dialog text view to the retrieved results
            // dialog content
            TextView latitudeTextView = dialog.findViewById(R.id.latitudeTextView);
            TextView longitudeTextView = dialog.findViewById(R.id.longitudeTextView);
            TextView accuracyTextView = dialog.findViewById(R.id.accuracyTextView);
            TextView altitudeTextView = dialog.findViewById(R.id.altitudeTextView);
//            TextView bearingTextView = dialog.findViewById(R.id.bearingTextView);

            GifImageView gifImageView = dialog.findViewById(R.id.animatedIcon);
//            ImageButton retryBtn = dialog.findViewById(R.id.retryButton);
//            retryBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(MainActivity.this, "Retry button enabled,now exiting", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                }
//            });

            latitudeTextView.setText("Latitude: " + lat);
            longitudeTextView.setText("Longitude: " + lon);
            accuracyTextView.setText("Accuracy: " + accuracy+"m");
            altitudeTextView.setText("Altitude: " + altitude + "m");
//            bearingTextView.setText("Bearing: "+ bearing + "°");

            // change the success icon
            gifImageView.setImageResource(R.drawable.location_achieved);
//            retryBtn.setEnabled(true);

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "An Error Occurred: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        String filepath = uri.getPath();
        imageView.setImageURI(uri);
        TextView file_path_txt = findViewById(R.id.file_path_txt);
        file_path_txt.setText(filepath);
    }
}