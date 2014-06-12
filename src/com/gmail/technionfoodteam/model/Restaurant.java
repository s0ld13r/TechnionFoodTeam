package com.gmail.technionfoodteam.model;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Restaurant implements Comparable<Restaurant>{
	public static final String JSON_OBJECT_NAME = "restaurant";
	public static final String JSON_ID = "id";
	public static final String JSON_NAME = "name";
	public static final String JSON_ADDRESS = "address";
	public static final String JSON_LAT = "lat";
	public static final String JSON_LNG = "lng";
	public static final String JSON_RANKING = "ranking";
	public static final String JSON_PICTURE = "picture";
	public static final String JSON_TELEPHONE = "telephone_number";
	public static final String JSON_OPENING_HOURS = "opening_hours";
	private int id;
	private String name;
	private String address;
	private double lng;
	private double lat;
	private double ranking;
	private String pathToLogo;
	private String telephoneNumber;
	private LinkedList<DayOpeningHours> openingHours;
	public Restaurant(String name, String address, double lat, double lng, 
			double ranking, String pathToLog, String tel){
		this.name = name;
		this.address = address;
		this.lat = lat;
		this.lng = lng;
		this.ranking = ranking;
		this.pathToLogo = pathToLog;
		this.telephoneNumber = tel;
	}
	public Restaurant(int id, String name, String address, double lat, double lng, 
			double ranking, String pathToLog, String tel){
		this(name,address,lat,lng,ranking,pathToLog, tel);
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
	public String getTelephoneNumber(){
		return telephoneNumber;
	}
	public LinkedList<DayOpeningHours> getOpeningHours(){
		return openingHours;
	}
	public void setOpeningHours(LinkedList<DayOpeningHours> openingHours){
		this.openingHours = openingHours;
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
		obj.put(JSON_TELEPHONE, getTelephoneNumber());
		if(openingHours != null){
			JSONArray arr = new JSONArray();
			for(DayOpeningHours doh : openingHours){
				arr.put(doh.toJSON());
			}
			obj.put(JSON_OPENING_HOURS, arr);
		}
		return obj;
	}
	public static Restaurant fromJSON(JSONObject obj) throws JSONException{
		Restaurant restaurant = new Restaurant(obj.getInt(JSON_ID), obj.getString(JSON_NAME),obj.getString(JSON_ADDRESS),
				obj.getDouble(JSON_LAT),obj.getDouble(JSON_LNG), obj.getDouble(JSON_RANKING), obj.getString(JSON_PICTURE), obj.getString(JSON_TELEPHONE));
		try{
			JSONArray arr = obj.getJSONArray(JSON_OPENING_HOURS);
			if(arr!=null){
				LinkedList<DayOpeningHours> lst = new LinkedList<DayOpeningHours>();
				for(int i=0;i<arr.length();i++){
					DayOpeningHours t = DayOpeningHours.fromJSON(arr.getJSONObject(i));
					if(t!= null){
						lst.addLast(t);
					}
				}
				restaurant.setOpeningHours(lst);
			}
		}catch(JSONException ex){}
		return restaurant;
	}
	@Override
	public int compareTo(Restaurant another) {
		if(this.ranking > another.ranking)
			return 1;
		if(this.ranking < another.ranking)
			return -1;
		return 0;
	}
	public String viewOpeningHours(){
		StringBuilder sb = new StringBuilder();
		for(DayOpeningHours doh : openingHours){
			sb.append(doh.getDay() + "\t\t\t" + doh.getStartTime().getHours() + ":" +doh.getStartTime().getMinutes()+ " - " + doh.getEndTime().getHours() + ":" +doh.getEndTime().getMinutes()+"\n");
		}
		return sb.toString();
	}
	
}
