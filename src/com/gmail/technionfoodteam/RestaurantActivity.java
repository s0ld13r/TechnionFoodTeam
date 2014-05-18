package com.gmail.technionfoodteam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gmail.technionfoodteam.model.Dish;
import com.gmail.technionfoodteam.model.Restaurant;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RestaurantActivity extends Activity {
	private ListView list;
	private DishesAdapter adapter;
	public static String RESTAURANT_ID="restaurant_id";
	private int restaurantId;
	private Restaurant currentRestaurant;
	private ImageView logoIv;
	private ImageButton navigateBtn;
	private TextView restaurantNameTv;
	private TextView distanceTv;
	private RatingBar rating;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant);
		restaurantId = getIntent().getIntExtra(RESTAURANT_ID, 1);
		list = (ListView)findViewById(R.id.listOfRestaurants);
		logoIv = (ImageView)findViewById(R.id.restLogo);
		navigateBtn = (ImageButton)findViewById(R.id.navigateToRestaurantBtn);
		restaurantNameTv = (TextView)findViewById(R.id.restaurantName);
		distanceTv = (TextView)findViewById(R.id.restaurantDistanceTo);
		rating = (RatingBar)findViewById(R.id.ratingOfRestaurant);
		GetRestaurantFromServer thread = new GetRestaurantFromServer();
		thread.execute();
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent(getApplicationContext(), DishActivity.class);
				int id = ((Dish)((DishesAdapter)arg0.getAdapter()).getItem(arg2)).getId();
				i.putExtra(DishActivity.DISH_ID, id);
				startActivity(i);
			}
		});
	}
	
	private class GetRestaurantFromServer extends AsyncTask<Void, Void, String>{
		private HttpURLConnection connection;
		
		@Override
		protected String doInBackground(Void... params) {
			try{
				String path = TechnionFoodApp.pathToServer + "service/restaurant/"+restaurantId;
				URL url = new URL(path);
				connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "text/plain");
				connection.setReadTimeout(TechnionFoodApp.READ_TIMEOUT);
				connection.setConnectTimeout(TechnionFoodApp.CONNECTION_TIMEOUT);
				connection.connect();
				int statusCode = connection.getResponseCode();
				if(statusCode != HttpURLConnection.HTTP_OK){
					JSONObject err = new JSONObject();
					err.put(TechnionFoodApp.JSON_ERROR, "Server error. Can not get information from server.");
					return err.toString();
				}
				return readTextFromServer();
			}catch(Exception ex){
				JSONObject err = new JSONObject();
				try {
					err.put(TechnionFoodApp.JSON_ERROR, "Connection error: "+ ex.getMessage());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return err.toString();
			}finally{
				if(connection!=null){
					connection.disconnect();
				}
			}
		}
		@Override
		protected void onPostExecute(String result) {
			JSONArray dishesArr = new JSONArray();
			try {
				TechnionFoodApp.isJSONError(result);
				JSONObject obj = new JSONObject(result);
				JSONObject restObj = obj.getJSONObject(Restaurant.JSON_OBJECT_NAME);
				currentRestaurant = Restaurant.fromJSON(restObj);
				//navigateBtn;
				restaurantNameTv.setText(currentRestaurant.getName());
				distanceTv.setText(getString(R.string.distance) + ": unknown");
				rating.setRating((float)currentRestaurant.getRanking());
				ImageLoader.getInstance().displayImage(currentRestaurant.getPathToLogo(), logoIv);
				dishesArr = obj.getJSONArray("dishes");
				
				adapter = new DishesAdapter(dishesArr,RestaurantActivity.this);
				list.setAdapter(adapter);
			} catch (Exception e) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						RestaurantActivity.this);
				alertDialogBuilder.setTitle("Error").setMessage(e.getMessage());
				alertDialogBuilder.setCancelable(false)
				.setPositiveButton("Retry",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						RestaurantActivity.this.finish();
						startActivity(new Intent(getApplicationContext(), RestaurantActivity.class));
					}
				  })
				  .setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						RestaurantActivity.this.finish();
					}
				});
				alertDialogBuilder.create().show();
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
