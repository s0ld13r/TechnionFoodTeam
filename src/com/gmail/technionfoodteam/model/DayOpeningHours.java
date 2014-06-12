package com.gmail.technionfoodteam.model;

import java.sql.Time;

import org.json.JSONException;
import org.json.JSONObject;

public class DayOpeningHours {
	public static final String JSON_DAY ="day";
	public static final String JSON_START_TIME ="start_time";
	public static final String JSON_END_TIME ="end_time";
	private Time startTime;
	private Time endTime;
	private int day;
	
	public DayOpeningHours(int day, Time startTime, Time endTime){
		this.day = day;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public Time getStartTime(){
		return startTime;
	}
	public Time getEndTime(){
		return endTime;
	}
	public int getDay(){
		return  day;
	}
	public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		try {
			obj.put(JSON_DAY, getDay());
			obj.put(JSON_START_TIME, getStartTime().getTime());
			obj.put(JSON_END_TIME, getEndTime().getTime());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	public static DayOpeningHours fromJSON(JSONObject obj){
		DayOpeningHours res = null;
		try {
			int _day = obj.getInt(JSON_DAY);
			Time _startTime = new Time(obj.getLong(JSON_START_TIME));
			Time  _endTime = new Time(obj.getLong(JSON_END_TIME));
			res = new DayOpeningHours(_day, _startTime, _endTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}
}
