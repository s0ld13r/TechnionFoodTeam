package com.gmail.technionfoodteam;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map.Entry;

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
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


public class SearchFragment extends Fragment {
	public static final String JSON_DISTANCE = "max_distance";
	public static final String JSON_PRICE = "max_price";
	public static final String JSON_TIME = "max_time";
	public static final String JSON_TYPES = "types";
	public static final String JSON_LAT = "current_lat";
	public static final String JSON_LNG = "current_lng";
	
	private Spinner distanceSpinner;
	private Spinner priceSpinner;
	private Spinner timeSpinner;
	private ListView dishTypesList;
	private int distance = -1;
	private double price = -1;
	private long time = -1;
	JSONObject obj = new JSONObject();
	private Button searchBtn;
	DishTypesValues[] dishTypesArray;
	QueryDishesAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_fragment_layout, container, false);
		distanceSpinner = (Spinner)view.findViewById(R.id.searchDistanceSpinner);
		priceSpinner = (Spinner)view.findViewById(R.id.searchPriceSpinner);
		timeSpinner = (Spinner)view.findViewById(R.id.searchTimeSpinner);
		dishTypesList = (ListView)view.findViewById(R.id.searchDishTypesList);
		searchBtn = (Button)view.findViewById(R.id.searchSearchBtn);
		
		searchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					obj.put(JSON_DISTANCE, distance);
					obj.put(JSON_PRICE,price);
					obj.put(JSON_TIME, time);
					Location location = ((TechnionFoodApp)getActivity().getApplication()).getCurrentLocation();
					obj.put(JSON_LAT, location.getLatitude());
					obj.put(JSON_LNG, location.getLongitude());
					JSONArray arr = new JSONArray();
					
					int len = dishTypesList.getCount();
					SparseBooleanArray checked = dishTypesList.getCheckedItemPositions();
					for (int i = 0; i < len; i++){
						if (checked.get(i)) {
							arr.put(i);
							if(i==0){
								break;
							}
						}
					}
					if(arr.length() == 0){
						arr.put(0);
					}
					obj.put(JSON_TYPES, arr);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_LONG).show();
				SendQueryToServer thread = new SendQueryToServer();
		    	thread.execute();
			}
		});
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initDistanceSpinner();
		initPriceSpinner();
		initTimeSpinner();
		initTypesList();
		adapter = ((MainActivity)getActivity()).getQueryDishesAdapter();
	}
	
	public void initTypesList(){
		HashMap<Integer, String> map = ((MainActivity)getActivity()).getDishTypeToValueMap();
		dishTypesArray = new DishTypesValues[map.size()+1];
		dishTypesArray[0] = new DishTypesValues(0, false, "All types");
		for(Entry<Integer, String> key : map.entrySet()){
			dishTypesArray[key.getKey()] = new DishTypesValues(key.getKey(), false, key.getValue());
		}
		ArrayAdapter<DishTypesValues> adapter = new ArrayAdapter<DishTypesValues>(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, dishTypesArray);
       
		dishTypesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        dishTypesList.setAdapter(adapter);
        //dishTypesList.setItemChecked(0, true);
        dishTypesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ArrayAdapter<DishTypesValues> a = (ArrayAdapter<DishTypesValues>)parent.getAdapter();
				dishTypesArray[position].isChecked = !dishTypesArray[position].isChecked ;
			}
		});
        
		
	}
	public void initTimeSpinner(){
		ArrayAdapter<TimeSpinnerValues> spinnerArrayAdapter = new ArrayAdapter<TimeSpinnerValues>(getActivity(),
		          android.R.layout.simple_spinner_item, new TimeSpinnerValues[] {   
		                new TimeSpinnerValues( -1), 
		                new TimeSpinnerValues( (new Time(0,15,0)).getTime()), 
		                new TimeSpinnerValues((new Time(0,30,0)).getTime()), 
		                new TimeSpinnerValues((new Time(0,45,0)).getTime()),
		                new TimeSpinnerValues((new Time(1,0,0)).getTime()),
		                new TimeSpinnerValues((new Time(1,30,0)).getTime()),
		                new TimeSpinnerValues((new Time(2,0,0)).getTime()),
		                });

		    // Step 3: Tell the spinner about our adapter
		timeSpinner.setAdapter(spinnerArrayAdapter);
		timeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				TimeSpinnerValues st = (TimeSpinnerValues)timeSpinner.getSelectedItem();
				time = st.timeInMinutes;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				TimeSpinnerValues st = (TimeSpinnerValues)timeSpinner.getSelectedItem();
				price = st.timeInMinutes;
			}
		});
	}
	
	
	
	public void initPriceSpinner(){
		ArrayAdapter<PriceSpinnerValues> spinnerArrayAdapter = new ArrayAdapter<PriceSpinnerValues>(getActivity(),
		          android.R.layout.simple_spinner_item, new PriceSpinnerValues[] {   
		                new PriceSpinnerValues( -1), 
		                new PriceSpinnerValues( 20), 
		                new PriceSpinnerValues( 30),
		                new PriceSpinnerValues( 40),
		                new PriceSpinnerValues( 50),
		                new PriceSpinnerValues( 60),
		                new PriceSpinnerValues( 70),
		                new PriceSpinnerValues( 80),
		                new PriceSpinnerValues( 90),
		                new PriceSpinnerValues( 100),
		                new PriceSpinnerValues( 150),
		                new PriceSpinnerValues( 200),
		                });

		    // Step 3: Tell the spinner about our adapter
		priceSpinner.setAdapter(spinnerArrayAdapter);
		priceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				PriceSpinnerValues st = (PriceSpinnerValues)priceSpinner.getSelectedItem();
				price = st.priceInNIS;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				PriceSpinnerValues st = (PriceSpinnerValues)priceSpinner.getSelectedItem();
				price = st.priceInNIS;
			}
		});
	}
	
	
	
	
	
	
	public void initDistanceSpinner(){
		ArrayAdapter<DistanceSpinnerValues> spinnerArrayAdapter = new ArrayAdapter<DistanceSpinnerValues>(getActivity(),
		          android.R.layout.simple_spinner_item, new DistanceSpinnerValues[] {   
		                new DistanceSpinnerValues( -1), 
		                new DistanceSpinnerValues( 250), 
		                new DistanceSpinnerValues( 500), 
		                new DistanceSpinnerValues( 1000),
		                new DistanceSpinnerValues( 2000),
		                new DistanceSpinnerValues( 5000)
		                });

		    // Step 3: Tell the spinner about our adapter
		distanceSpinner.setAdapter(spinnerArrayAdapter);
		distanceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				DistanceSpinnerValues st = (DistanceSpinnerValues)distanceSpinner.getSelectedItem();
				distance = st.distanceInMeters;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				DistanceSpinnerValues st = (DistanceSpinnerValues)distanceSpinner.getSelectedItem();
				distance = st.distanceInMeters;
			}
		});
	}
	public class DistanceSpinnerValues{
		public int distanceInMeters;
		public DistanceSpinnerValues(int distance) {
			distanceInMeters = distance;
		}
		@Override
		public String toString() {
			if(distanceInMeters < 0){
				return "Not Selected";
			}else if(distanceInMeters < 1000){
				return distanceInMeters + " meters ";
			}
			return (distanceInMeters / 1000) + " km";
		}
	}
	public class PriceSpinnerValues{
		public double priceInNIS;
		public PriceSpinnerValues(double price) {
			priceInNIS = price;
		}
		@Override
		public String toString() {
			if(priceInNIS < 0){
				return "Not Selected";
			}
			return priceInNIS + " NIS ";
			
		}
	}
	public class TimeSpinnerValues{
		public long timeInMinutes;
		public TimeSpinnerValues(long time) {
			timeInMinutes = time;
		}
		@Override
		public String toString() {
			Time t= new Time(timeInMinutes);
			if(timeInMinutes < 0){
				return "Not Selected";
			}else if(t.getHours() == 0){
				return t.getMinutes() + " minutes";
			}else if(t.getMinutes() == 0){
				return (t.getHours() + " hours");
			}
			return (t.getHours() + " hours and " + t.getMinutes() + " minutes");			
		}
	}
	public class DishTypesValues{
		public int typeId;
		public boolean isChecked;
		public String stringRepresentation;
		
		public DishTypesValues(int type, boolean status, String desc) {
			typeId = type;
			isChecked = status;
			stringRepresentation = desc;
		}
		@Override
		public String toString() {
			return stringRepresentation;		
		}
	}
	
	
	
	
	private class SendQueryToServer extends AsyncTask<Void, Void, String>{
		
		@Override
		protected String doInBackground(Void... params) {
			try{
				String path = TechnionFoodApp.pathToServer + "service/query";
				HttpClient client = new DefaultHttpClient();
				URL url = new URL(path);
				HttpPost post = new HttpPost(url.toString());
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
			JSONArray arr;
			try {
				TechnionFoodApp.isJSONError(result);
				arr = new JSONArray(result);
				//adapter
				
				Fragment fragment = Fragment.instantiate(getActivity(), (new QueryResultsFragment()).getClass().getName());
				((MainActivity)getActivity()).changeFragment(fragment);
				adapter.setOrderTo(0);
				adapter.update(arr);
				
			} catch (Exception e) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setTitle("Error").setMessage(e.getMessage());
				alertDialogBuilder.setCancelable(false)
				.setPositiveButton("Retry",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						SendQueryToServer thread = new SendQueryToServer();
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
