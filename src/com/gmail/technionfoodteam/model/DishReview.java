package com.gmail.technionfoodteam.model;

import org.json.JSONException;
import org.json.JSONObject;

public class DishReview extends Review {
	public static String JSON_OBJECT_NAME = "dish_review";
	public static String JSON_DISH_ID = "dish_id";
	private int dishId;
	public DishReview(){}
	public DishReview(int dishId, String username, double ranking, String description){
		super(username,ranking,description);
		this.dishId = dishId;
	}
	public DishReview(int reviewId,int dishId, String username, double ranking, String description){
		super(reviewId,username,ranking,description);
		this.dishId = dishId;
	}
	public int getDishId(){
		return dishId;
	}
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject obj = super.toJSON();
		obj.put(JSON_DISH_ID, getDishId());
		return obj;
	}
	public static DishReview fromJSON(JSONObject obj) throws JSONException {
		Review review =  Review.fromJSON(obj);
		DishReview dishReview = new DishReview(obj.getInt(JSON_DISH_ID), review.getUsername(), review.getRanking(), review.getDescription());
		return dishReview;
	}
	
}
