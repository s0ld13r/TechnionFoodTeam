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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gmail.technionfoodteam.model.Dish;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DishActivity extends Activity {
	private ListView list;
	private ReviewsAdapter adapter;
	public static String DISH_ID="dish_id";
	private int dishId;
	private Dish currentDish;
	private ImageView logoIv;
	private TextView dishNameTv;
	private TextView restaurantName;
	private TextView priceTv;
	private TextView descriptionTv;
	private RatingBar rating;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dish);
		dishId = getIntent().getIntExtra(DISH_ID, 1);
		list = (ListView)findViewById(R.id.ad_listOfReviews);
		logoIv = (ImageView)findViewById(R.id.ad_dishLogo);
		dishNameTv = (TextView)findViewById(R.id.ad_dishName);
		priceTv = (TextView)findViewById(R.id.ad_price);
		restaurantName = (TextView)findViewById(R.id.ad_restaurantName);
		descriptionTv = (TextView)findViewById(R.id.ad_dishDescription);
		rating = (RatingBar)findViewById(R.id.ad_ratingOfRestaurant);
		GetDishReviewsFromServer thread = new GetDishReviewsFromServer();

		thread.execute();
		
	}
	private class GetDishReviewsFromServer extends AsyncTask<Void, Void, String>{
		private HttpURLConnection connection;
		
		@Override
		protected String doInBackground(Void... params) {
			try{
				String path = TechnionFoodApp.pathToServer + "service/dish/"+dishId;
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
			JSONArray reviewsArr = new JSONArray();
			try {
				TechnionFoodApp.isJSONError(result);
				JSONObject obj = new JSONObject(result);
				JSONObject restObj = obj.getJSONObject(Dish.JSON_OBJECT_NAME);
				currentDish = Dish.fromJSON(restObj);
				dishNameTv.setText(currentDish.getName());
				restaurantName.setText(currentDish.getRestaurantName());
				priceTv.setText(""+currentDish.getPrice());
				descriptionTv.setText(currentDish.getDescription());
				rating.setRating((float)currentDish.getRanking());
				ImageLoader.getInstance().displayImage(currentDish.getPhoto(), logoIv);
				reviewsArr = obj.getJSONArray("reviews");
				
				adapter = new ReviewsAdapter(reviewsArr,DishActivity.this);
				list.setAdapter(adapter);
			} catch (Exception e) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						DishActivity.this);
				alertDialogBuilder.setTitle("Error").setMessage(e.getMessage());
				alertDialogBuilder.setCancelable(false)
				.setPositiveButton("Retry",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						DishActivity.this.finish();
						startActivity(new Intent(getApplicationContext(), DishActivity.class));
					}
				  })
				  .setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						DishActivity.this.finish();
					}
				});
				alertDialogBuilder.create().show();
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
