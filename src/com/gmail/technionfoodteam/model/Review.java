package com.gmail.technionfoodteam.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Review {
	public static String JSON_ID = "id";
	public static String JSON_USERNAME = "username";
	public static String JSON_RANKING = "ranking";
	public static String JSON_DESCRIPTION = "description";
	private int id;
	private String username;
	private double ranking;
	private String description;
	public Review(){}
	public Review(String username, double ranking, String description){
		this.username = username;
		this.ranking = ranking;
		this.description = description;
	}
	public Review(int id, String username, double ranking, String description){
		this(username,ranking,description);
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public String getUsername() {
		return username;
	}
	public double getRanking() {
		return ranking;
	}
	public String getDescription() {
		return description;
	}
	public JSONObject toJSON() throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(JSON_ID, getId());
		obj.put(JSON_USERNAME, getUsername());
		obj.put(JSON_DESCRIPTION, getDescription());
		obj.put(JSON_RANKING, getRanking());
		return obj;
	}
	public static Review fromJSON(JSONObject obj) throws JSONException{
		Review review= new Review(obj.getInt(JSON_ID), obj.getString(JSON_USERNAME),
				obj.getDouble(JSON_RANKING), obj.getString(JSON_DESCRIPTION));
		return review;
	}
}
