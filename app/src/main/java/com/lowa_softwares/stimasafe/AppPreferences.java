package com.lowa_softwares.stimasafe;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferences {
    private static AppPreferences instance;
    private SharedPreferences sharedPreferences;

    private AppPreferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static synchronized AppPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreferences(context.getApplicationContext());
        }
        return instance;
    }
    // check if linear layout is visible
    public boolean isLinearLayoutVisible() {
        return sharedPreferences.getBoolean(Constants.PREF_KEY_LINEAR_LAYOUT_VISIBLE, false);
    }
    public boolean isCableLinearLayoutVisible() {
        return sharedPreferences.getBoolean(Constants.PREF_CABLE_KEY_LINEAR_LAYOUT_VISIBLE, false);
    }
    // change the linear layout visibility
    public void setLinearLayoutVisible(boolean isVisible) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.PREF_KEY_LINEAR_LAYOUT_VISIBLE, isVisible);
        editor.apply();
    }

    public void setCableLinearLayoutVisible(boolean isVisible) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.PREF_CABLE_KEY_LINEAR_LAYOUT_VISIBLE, isVisible);
        editor.apply();
    }

    // function to save the position of selected item in the transformer dropdown
    public void saveDropdownValueTransformerCondition(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.PREF_DROP_TRANSFORMER_COND, position);
        editor.apply();
    }
    // save dropdown for transformer state
    public void saveDropdownValuePowerlineState(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.PREF_POWERLINE_STATE, position);
        editor.apply();
    }
    // save dropdown for transformer type
    public void saveDropdownValuePowerlineStrType(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.PREF_POWERLINE_TYPE, position);
        editor.apply();
    }

    // check if there are un-submitted changes in the prefs
    public boolean isDataAvailable() {
        return sharedPreferences.getAll().size() > 0;
    }

    // save transformer path
    public void saveTransformerPhotoPath(String path){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_TRANSFORMER_PHOTO_PATH, path);
        editor.apply();
    }

    // save faulty cables photo path
    public void saveCablesPhotoPath(String path){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_CABLES_PHOTO_PATH, path);
        editor.apply();
    }
    // save faulty powerline photo path
    public void savePowerlinePhotoPath(String path){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_POWERLINE_PHOTO_PATH, path);
        editor.apply();
    }

    // function to clear out shared preferences
    public void clearPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void saveCablesCount(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_CABLE_COUNT, text);
        editor.apply();
    }

    public void saveSelectedDate(String date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_POWERLINE_DATE, date);
        editor.apply();
    }

    public void saveLocationData(String lat, String lon, String accuracy, String altitude) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_POWERLINE_LATITUDE, lat);
        editor.putString(Constants.PREF_POWERLINE_LONGITUDE, lon);
        editor.putString(Constants.PREF_POWERLINE_ACCURACY, accuracy);
        editor.putString(Constants.PREF_POWERLINE_ALTITUDE, altitude);
        editor.apply();
    }

    public void saveLat(String latitude) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_POWERLINE_LATITUDE, latitude);
        editor.apply();
    }

    public void saveLong(String longitude) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_POWERLINE_LONGITUDE, longitude);
        editor.apply();
    }

    public void saveDropdownValueIncidentType(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.PREF_INCIDENT_TYPE, position);
        editor.apply();
    }

    public void saveDropdownValueSeverityLevel(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.PREF_INCIDENT_SEVERITY_LEVEL, position);
        editor.apply();
    }

    public void saveIncidentTime(String full_time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_INCIDENT_TIME, full_time);
        editor.apply();
    }

    public void saveIncidentDate(String full_date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_INCIDENT_DATE, full_date);
        editor.apply();
    }

    public void saveIncidentLocation(String lat, String lon) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_INCIDENT_LATITUDE, lat);
        editor.putString(Constants.PREF_INCIDENT_LONGITUDE, lon);
        editor.apply();
    }

    public void saveLongInc(String longitude) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_INCIDENT_LONGITUDE, longitude);
        editor.apply();
    }

    public void saveLatInc(String latitude) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_INCIDENT_LATITUDE, latitude);
        editor.apply();
    }

    public void saveIncidentPhotoPath(String filepath) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_INCIDENT_PHOTO_PATH, filepath);
        editor.apply();
    }

    public void clearIncidentPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(Constants.PREF_INCIDENT_TYPE);
        editor.remove(Constants.PREF_INCIDENT_SEVERITY_LEVEL);
        editor.remove(Constants.PREF_INCIDENT_DATE);
        editor.remove(Constants.PREF_INCIDENT_TIME);
        editor.remove(Constants.PREF_INCIDENT_LATITUDE);
        editor.remove(Constants.PREF_INCIDENT_LONGITUDE);
        editor.remove(Constants.PREF_INCIDENT_PHOTO_PATH);

        editor.apply();
    }

    public void clearTransformerDetailIfPreviouslyFilled() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.contains(Constants.PREF_DROP_TRANSFORMER_COND)){
            editor.remove(Constants.PREF_DROP_TRANSFORMER_COND);
        }
        if(sharedPreferences.contains(Constants.PREF_TRANSFORMER_PHOTO_PATH)){
            editor.remove(Constants.PREF_TRANSFORMER_PHOTO_PATH);
        }
        editor.apply();
    }

    public void clearCableDetailIfPreviouslyFilled() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.contains(Constants.PREF_CABLE_COUNT)){
            editor.remove(Constants.PREF_CABLE_COUNT);
        }
        if(sharedPreferences.contains(Constants.PREF_CABLES_PHOTO_PATH)){
            editor.remove(Constants.PREF_CABLES_PHOTO_PATH);
        }
        editor.apply();
    }
}
