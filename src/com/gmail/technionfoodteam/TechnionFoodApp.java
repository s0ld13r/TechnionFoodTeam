package com.gmail.technionfoodteam;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


public class TechnionFoodApp extends UILApplication {
	public static final String pathToServer = "http://ec2-54-72-218-202.eu-west-1.compute.amazonaws.com:8080/t/";
	//public static final String pathToServer = "http://10.0.2.2:8080/t/";
	public static final int READ_TIMEOUT = 5000;
	public static final int CONNECTION_TIMEOUT = 5000;
	public static final String JSON_ERROR = "error";
	private Location currentLocation;
	private LocationManager locationManager;
	public static void isJSONError(String jsonString) throws Exception{
		try {
			JSONObject obj = new JSONObject(jsonString);
			String res = obj.getString(TechnionFoodApp.JSON_ERROR);
			throw new Exception(res);
		} catch (JSONException e) {}
		
	}
	@Override
	public void onCreate() {
		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager)getSystemService(context);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);
		currentLocation = location;
		locationManager.requestLocationUpdates(provider, 60*1000, 50,
				locationListener);
		super.onCreate();
	}
	public Location getCurrentLocation(){
		if (currentLocation == null){
			Location location = new Location("Default location");
			location.setLatitude(32.777044);
			location.setLongitude(35.023148);
			return location;
		}
		return currentLocation;
	}
	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			currentLocation = (location);
		}
		public void onProviderDisabled(String provider){
		}
		public void onProviderEnabled(String provider){ 
			
		}
		public void onStatusChanged(String provider, int status,Bundle extras){
			
		}
	};
}
