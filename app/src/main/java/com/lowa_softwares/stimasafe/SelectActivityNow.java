package com.lowa_softwares.stimasafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class SelectActivityNow extends AppCompatActivity {

    AppCompatButton rcdFaultyPowerline;
    AppCompatButton rcdPowerlineIncident;
    TextView checkPrefStatus;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_activity_screen);

        rcdFaultyPowerline = findViewById(R.id.btnRecordFaultyPowerline);
        rcdPowerlineIncident = findViewById(R.id.btnRecordPowerlineIncident);
        checkPrefStatus = findViewById(R.id.checkPrefStatus);

        rcdFaultyPowerline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectActivityNow.this, RecordFaultyPowerline.class);
                startActivity(intent);
            }
        });
        rcdPowerlineIncident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(SelectActivityNow.this, RecordPowerIncident.class);
                startActivity(intent2);
            }
        });

        // check if there is unsaved changes in the data collection
        // Initialize AppPreferences
        AppPreferences appPreferences = AppPreferences.getInstance(this);
        // Check if data is available
        if (appPreferences.isDataAvailable()) {
            checkPrefStatus.setText("You have unsubmitted changes!");
            checkPrefStatus.setTextColor(getResources().getColor(R.color.red_A700));
        } else {
            checkPrefStatus.setText("You dont have unsubmitted changes!");
            checkPrefStatus.setTextColor(getResources().getColor(R.color.txtGreenColor));
        }
    }
}
