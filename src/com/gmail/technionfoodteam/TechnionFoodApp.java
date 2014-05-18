package com.gmail.technionfoodteam;

import org.json.JSONException;
import org.json.JSONObject;


public class TechnionFoodApp extends UILApplication {
	public static final String pathToServer = "http://ec2-54-72-218-202.eu-west-1.compute.amazonaws.com:8080/t/";
	public static final int READ_TIMEOUT = 5000;
	public static final int CONNECTION_TIMEOUT = 5000;
	public static final String JSON_ERROR = "error";
	
	public static void isJSONError(String jsonString) throws Exception{
		try {
			JSONObject obj = new JSONObject(jsonString);
			String res = obj.getString(TechnionFoodApp.JSON_ERROR);
			throw new Exception(res);
		} catch (JSONException e) {}
		
	}
}
