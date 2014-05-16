package com.gmail.technionfoodteam.model;

import org.json.JSONException;
import org.json.JSONObject;

public class RestaurantReview extends Review {
	public static String JSON_OBJECT_NAME = "restaurant_review";
	public static String JSON_REST_ID = "rest_id";
	private int restaurantId;
	public RestaurantReview(int dishId, String username, double ranking, String description){
		super(username,ranking,description);
		this.restaurantId = dishId;
	}
	public RestaurantReview(int reviewId,int dishId, String username, double ranking, String description){
		super(reviewId,username,ranking,description);
		this.restaurantId = dishId;
	}
	public int getRestaurantId(){
		return restaurantId;
	}
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject obj = super.toJSON();
		obj.put(JSON_REST_ID, getRestaurantId());
		return obj;
	}
	public static DishReview fromJSON(JSONObject obj) throws JSONException {
		Review review =  Review.fromJSON(obj);
		DishReview dishReview = new DishReview(obj.getInt(JSON_REST_ID), review.getUsername(), review.getRanking(), review.getDescription());
		return dishReview;
	}
}
