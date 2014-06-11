package com.gmail.technionfoodteam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gmail.technionfoodteam.model.Dish;
import com.gmail.technionfoodteam.model.Restaurant;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RestaurantFragment extends Fragment {
	private ExpandableListView list;
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
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_restaurant, container, false);
		
		list = (ExpandableListView)rootView.findViewById(R.id.listOfRestaurants);
		logoIv = (ImageView)rootView.findViewById(R.id.restLogo);
		navigateBtn = (ImageButton)rootView.findViewById(R.id.navigateToRestaurantBtn);
		restaurantNameTv = (TextView)rootView.findViewById(R.id.restaurantName);
		distanceTv = (TextView)rootView.findViewById(R.id.restaurantDistanceTo);
		rating = (RatingBar)rootView.findViewById(R.id.ratingOfRestaurant);

		list.setOnChildClickListener(new OnChildClickListener() {
				 
	            @Override
	            public boolean onChildClick(ExpandableListView parent, View v,
	                    int groupPosition, int childPosition, long id) {
	            	
	            	
					int dishId = ((Dish)((DishesAdapter)parent.getExpandableListAdapter()).getChild(groupPosition, childPosition)).getId();
					Bundle bundle = new Bundle();
					bundle.putInt(DishFragment.DISH_ID, dishId);
					
					Fragment fragment = Fragment.instantiate(getActivity(), (new DishFragment()).getClass().getName());
		   			fragment.setArguments(bundle);
		    		((MainActivity)getActivity()).changeFragment(fragment);
	                return false;
	            }
	        });
			
		return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		restaurantId = getArguments().getInt(RESTAURANT_ID, 1);
		GetRestaurantFromServer thread = new GetRestaurantFromServer();
		thread.execute();
		
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
				distanceTv.setText(currentRestaurant.getAddress());
				rating.setRating((float)currentRestaurant.getRanking());
				ImageLoader.getInstance().displayImage(currentRestaurant.getPathToLogo(), logoIv);
				dishesArr = obj.getJSONArray("dishes");
				
				adapter = new DishesAdapter(dishesArr,getActivity(),((MainActivity)getActivity()).getDishTypeToValueMap());
				list.setAdapter(adapter);
			} catch (Exception e) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setTitle("Error").setMessage(e.getMessage());
				alertDialogBuilder.setCancelable(false)
				.setPositiveButton("Retry",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						getActivity().finish();
						startActivity(new Intent(getActivity().getApplicationContext(), RestaurantFragment.class));
					}
				  })
				  .setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						getActivity().finish();
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
	public void navigateFunc(View view){
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +currentRestaurant.getLat()+","+currentRestaurant.getLng()));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
