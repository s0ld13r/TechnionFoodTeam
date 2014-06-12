package com.gmail.technionfoodteam.model;

import org.json.JSONException;
import org.json.JSONObject;

public class IntStringPair {
	public static final String KEY = "key";
	public static final String VALUE = "value";
	private int key;
	private String value;
	public IntStringPair(int index, String val){
		key = index;
		value = val;
	}
	public  int getKey(){
		return key;
	}
	public String getValue(){
		return value;
	}
	public JSONObject toJSON() throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(KEY, getKey());
		obj.put(VALUE, getValue());
		return obj;
	}
	public static IntStringPair fromJSON(JSONObject obj){
		try {
			int k = obj.getInt(KEY);
			String str = obj.getString(VALUE);
			return new IntStringPair(k, str);
		} catch (JSONException e) {}
		return null;
	}
}
