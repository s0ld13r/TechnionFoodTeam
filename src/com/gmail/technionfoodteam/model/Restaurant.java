package com.gmail.technionfoodteam.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Restaurant {
	public static String JSON_OBJECT_NAME = "restaurant";
	public static String JSON_ID = "id";
	public static String JSON_NAME = "name";
	public static String JSON_ADDRESS = "address";
	public static String JSON_LAT = "lat";
	public static String JSON_LNG = "lng";
	public static String JSON_RANKING = "ranking";
	public static String JSON_PICTURE = "picture";

	private int id;
	private String name;
	private String address;
	private double lng;
	private double lat;
	private double ranking;
	private String pathToLogo;
	public Restaurant(String name, String address, double lat, double lng, 
			double ranking, String pathToLog){
		this.name = name;
		this.address = address;
		this.lat = lat;
		this.lng = lng;
		this.ranking = ranking;
		this.pathToLogo = pathToLog;
	}
	public Restaurant(int id, String name, String address, double lat, double lng, 
			double ranking, String pathToLog){
		this(name,address,lat,lng,ranking,pathToLog);
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public double getLng() {
		return lng;
	}
	public double getLat() {
		return lat;
	}
	public double getRanking() {
		return ranking;
	}
	public String getPathToLogo() {
		return pathToLogo;
	}
	public JSONObject toJSON() throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(JSON_ID, getId());
		obj.put(JSON_NAME, getName());
		obj.put(JSON_ADDRESS, getAddress());
		obj.put(JSON_LAT, getLat());
		obj.put(JSON_LNG, getLng());
		obj.put(JSON_RANKING, getRanking());
		obj.put(JSON_PICTURE, getPathToLogo());
		return obj;
	}
	public static Restaurant fromJSON(JSONObject obj) throws JSONException{
		Restaurant restaurant = new Restaurant(obj.getInt(JSON_ID), obj.getString(JSON_NAME),obj.getString(JSON_ADDRESS),
				obj.getDouble(JSON_LAT),obj.getDouble(JSON_LNG), obj.getDouble(JSON_RANKING), obj.getString(JSON_PICTURE));
		return restaurant;
	}
}
