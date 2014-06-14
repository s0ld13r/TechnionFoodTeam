package com.gmail.technionfoodteam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gmail.technionfoodteam.model.Dish;
import com.gmail.technionfoodteam.model.DishReview;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DishFragment extends Fragment {
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
	private DishReview review;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.activity_dish,
		        container, false);
		
		list = (ListView)view.findViewById(R.id.ad_listOfReviews);
		logoIv = (ImageView)view.findViewById(R.id.ad_dishLogo);
		dishNameTv = (TextView)view.findViewById(R.id.ad_dishName);
		priceTv = (TextView)view.findViewById(R.id.ad_price);
		restaurantName = (TextView)view.findViewById(R.id.ad_restaurantName);
		descriptionTv = (TextView)view.findViewById(R.id.ad_dishDescription);
		rating = (RatingBar)view.findViewById(R.id.ad_ratingOfRestaurant);
		TextView tv = (TextView)view.findViewById(R.id.ad_review_title);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showRatingDialog();
			}
		});
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		dishId = getArguments().getInt(DISH_ID, 1);
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
				
				adapter = new ReviewsAdapter(getActivity());
				list.setAdapter(adapter);
				adapter.update(reviewsArr);
			} catch (Exception e) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setTitle("Error").setMessage(e.getMessage());
				alertDialogBuilder.setCancelable(false)
				.setPositiveButton("Retry",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						GetDishReviewsFromServer thread = new GetDishReviewsFromServer();
						thread.execute();
					}
				  })
				  .setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						getActivity().finish();
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
	private void showRatingDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
	    View dialoglayout = inflater.inflate(R.layout.dish_review_layout, null);
	    final EditText nameEditText = (EditText)dialoglayout.findViewById(R.id.drlNameEditText);
	    nameEditText.setText(((MainActivity)getActivity()).getCurrentUsername());
	    final EditText reviewEditText = (EditText)dialoglayout.findViewById(R.id.drlReviewEditText);
	    final RatingBar reviewRatingBar = (RatingBar)dialoglayout.findViewById(R.id.drlRating);
	    builder.setView(dialoglayout);
		builder.setTitle(getString(R.string.drl_title))
		    .setCancelable(false)
		    .setNegativeButton(getString(R.string.drl_cancel), new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();					
				}
			})
		    .setPositiveButton(getString(R.string.drl_ok), new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int id) {
			    	review  = new DishReview(currentDish.getId(),nameEditText.getText().toString().trim(),(double)reviewRatingBar.getRating() ,reviewEditText.getText().toString().trim());
			    	adapter.addReview(review);
			    	SendDishReviewToServer thread = new SendDishReviewToServer();
			    	thread.execute();
			    }
		    
		});
		
	    builder.show();
	}
	
	
	private class SendDishReviewToServer extends AsyncTask<Void, Void, String>{
		
		@Override
		protected String doInBackground(Void... params) {
			try{
				String path = TechnionFoodApp.pathToServer + "service/addDishReview";
				HttpClient client = new DefaultHttpClient();
				URL url = new URL(path);
				HttpPost post = new HttpPost(url.toString());
				JSONObject obj = review.toJSON();
				StringEntity se = new StringEntity(obj.toString());  
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
				post.setEntity(se);
				HttpResponse response = client.execute(post);
				int statusCode = response.getStatusLine().getStatusCode();
				if((statusCode < HttpURLConnection.HTTP_OK) || (statusCode >= 300)){
					JSONObject err = new JSONObject();
					err.put(TechnionFoodApp.JSON_ERROR, "Server error. Can not get information from server.");
					return err.toString();
				}
				String str = EntityUtils.toString(response.getEntity());
				response.getEntity().consumeContent();
				return str;
			}catch(Exception ex){
				JSONObject err = new JSONObject();
				try {
					err.put(TechnionFoodApp.JSON_ERROR, "Connection error: "+ ex.getMessage());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return err.toString();
			}
		}
		@Override
		protected void onPostExecute(String result) {
			JSONObject obj;
			try {
				TechnionFoodApp.isJSONError(result);
				obj = new JSONObject(result);
				double res = obj.getDouble(DishReview.JSON_RANKING);
				rating.setRating((float)res);
				
			} catch (Exception e) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setTitle("Error").setMessage(e.getMessage());
				alertDialogBuilder.setCancelable(false)
				.setPositiveButton("Retry",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						SendDishReviewToServer thread = new SendDishReviewToServer();
				    	thread.execute();
					}
				  })
				  .setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						//getActivity().finish();
					}
				});
				alertDialogBuilder.create().show();
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}
	}
	
}
