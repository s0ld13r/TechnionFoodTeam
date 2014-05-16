package com.gmail.technionfoodteam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

import com.gmail.technionfoodteam.model.Restaurant;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RestaurantsActivity extends Activity {
	private ListView list;
	private RestaurantsAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurants);
		list = (ListView)findViewById(R.id.listOfRestaurants);
		GetRestaurantsListFromServer thread = new GetRestaurantsListFromServer();
		thread.execute();
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent(getApplicationContext(), RestaurantActivity.class);
				int id = ((Restaurant)((RestaurantsAdapter)arg0.getAdapter()).getItem(arg2)).getId();
				i.putExtra(RestaurantActivity.RESTAURANT_ID, id);
				startActivity(i);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private class GetRestaurantsListFromServer extends AsyncTask<Void, Void, String>{
		private HttpURLConnection connection;
		
		@Override
		protected String doInBackground(Void... params) {
			try{
				String path = TechnionFoodApp.pathToServer + "service/restaurants";
				URL url = new URL(path);
				connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "text/plain");
				connection.setReadTimeout(3000);
				connection.setConnectTimeout(3000);
				connection.connect();
				int statusCode = connection.getResponseCode();
				if(statusCode != HttpURLConnection.HTTP_OK){
					return "Error: Failed get data from " + path + " Status is :" + statusCode;
				}
				return readTextFromServer();
			}catch(Exception ex){
				return "Error: "+ ex.getMessage();
			}finally{
				if(connection!=null){
					connection.disconnect();
				}
			}
		}
		@Override
		protected void onPostExecute(String result) {
			JSONArray arr = new JSONArray();
			try {
				arr = new JSONArray(result);
				adapter = new RestaurantsAdapter(arr,RestaurantsActivity.this);
				list.setAdapter(adapter);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}
		private String readTextFromServer() throws IOException{
			InputStreamReader isr = new InputStreamReader(connection.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while(line != null){
				sb.append(line);
				line = br.readLine();
			}
			return sb.toString();
		}
	}
}
