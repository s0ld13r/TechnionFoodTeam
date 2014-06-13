package com.gmail.technionfoodteam.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Dish {
	public static String JSON_OBJECT_NAME = "dish";
	public static String JSON_ID = "id";
	public static String JSON_NAME = "name";
	public static String JSON_PRICE = "price";
	public static String JSON_DESCRIPTION = "description";
	public static String JSON_REST_ID = "restId";
	public static String JSON_DISH_TYPE = "dishType";
	public static String JSON_RANKING = "ranking";
	public static String JSON_PICTURE = "picture";
	public static String JSON_REST_NAME = "restName";
	public static String JSON_REST_LAT = "restLat";
	public static String JSON_REST_LNG = "restLng";
	private int id;
	private String name;
	private double price;
	private String description;
	private int restaurantId;
	private int dishType;
	private double ranking;
	private String photo;
	private String restaurantName;
	private double restLat;
	private double restLng;
	public Dish(String name, double price, String description, int restId, int dishType, double ranking, String photo){
		this.name = name;
		this.price = price;
		this.description = description;
		this.restaurantId = restId;
		this.dishType = dishType;
		this.ranking = ranking;
		this.photo = photo;
	}
	public Dish(int id, String name, double price, String description, int restId, int dishType, double ranking,  String photo, String restName){
		this(name, price, description, restId, dishType, ranking, photo);
		this.id = id;
		this.restaurantName = restName;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public double getPrice() {
		return price;
	}
	public String getDescription() {
		return description;
	}
	public int getRestaurantId() {
		return restaurantId;
	}
	public int getDishType() {
		return dishType;
	}
	public double getRanking(){
		return ranking;
	} 
	
	public String getPhoto() {
		return photo;
	}
	public String getRestaurantName(){
		return restaurantName;
	}
	public JSONObject toJSON() throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(JSON_ID, getId());
		obj.put(JSON_NAME, getName());
		obj.put(JSON_PRICE, getPrice());
		obj.put(JSON_DESCRIPTION, getDescription());
		obj.put(JSON_REST_ID, getRestaurantId());
		obj.put(JSON_DISH_TYPE, getDishType());
		obj.put(JSON_RANKING, getRanking());
		obj.put(JSON_PICTURE, getPhoto());
		obj.put(JSON_REST_NAME, getRestaurantName());
		obj.put(JSON_REST_LAT, getRestLat());
		obj.put(JSON_REST_LNG, getRestLng());
		return obj;
	}
	public static Dish fromJSON(JSONObject obj) throws JSONException{
		Dish dish = new Dish(obj.getInt(JSON_ID), obj.getString(JSON_NAME), 
				obj.getDouble(JSON_PRICE), obj.getString(JSON_DESCRIPTION),
				obj.getInt(JSON_REST_ID), obj.getInt(JSON_DISH_TYPE), obj.getDouble(JSON_RANKING),
				obj.getString(JSON_PICTURE), obj.getString(JSON_REST_NAME));
		try{
			dish.setRestLat(obj.getDouble(JSON_REST_LAT));
			dish.setRestLng(obj.getDouble(JSON_REST_LNG));
		}catch(JSONException ex){}
		return dish;
	}
	public double getRestLat() {
		return restLat;
	}
	public void setRestLat(double restLat) {
		this.restLat = restLat;
	}
	public double getRestLng() {
		return restLng;
	}
	public void setRestLng(double restLng) {
		this.restLng = restLng;
	}
}
