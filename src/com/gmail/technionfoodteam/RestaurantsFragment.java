package com.gmail.technionfoodteam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gmail.technionfoodteam.model.Restaurant;

public class RestaurantsFragment extends Fragment{
	private static RestaurantsAdapter restaurantsAdapter;
	
	SectionsPagerAdapter mSectionsPagerAdapter;  
    ViewPager mViewPager; 

    @Override
 	public void onDetach() {
 	    super.onDetach();

 	    try {
 	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
 	        childFragmentManager.setAccessible(true);
 	        childFragmentManager.set(this, null);

 	    } catch (NoSuchFieldException e) {
 	        throw new RuntimeException(e);
 	    } catch (IllegalAccessException e) {
 	        throw new RuntimeException(e);
 	    }
 	}
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.activity_restaurants,
		        container, false);  
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
        // Create the adapter that will return a fragment for each of the three  
        // primary sections of the app.  

		return view; 

	}
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState); 
    	mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager()); 
    	 restaurantsAdapter = new RestaurantsAdapter((TechnionFoodApp)getActivity().getApplication());
    	 mViewPager.setAdapter(mSectionsPagerAdapter);
    	 mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
 			
 			@Override
 			public void onPageSelected(int arg0) {
 				restaurantsAdapter.setOrderTo(arg0);
 			}
 			
 			@Override
 			public void onPageScrolled(int arg0, float arg1, int arg2) {
 				// TODO Auto-generated method stub
 				
 			}
 			
 			@Override
 			public void onPageScrollStateChanged(int arg0) {
 				// TODO Auto-generated method stub
 				
 			}
 		})  ;
 		GetRestaurantsListFromServer thread = new GetRestaurantsListFromServer();
 		thread.execute();
    	
    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter {
    	
        public  SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {  
            super(fm);  
        }  
  
        @Override  
        public Fragment getItem(int position) {  
            // getItem is called to instantiate the fragment for the given page.  
            // Return a DummySectionFragment (defined as a static inner class  
            // below) with the page number as its lone argument.  
            Fragment fragment = Fragment.instantiate(getActivity(), DummySectionFragment.class.getName());  
            Bundle args = new Bundle();  
            args.putInt(DummySectionFragment.OREDERING, position);  
            fragment.setArguments(args);  
            return fragment;  
        }  
  
        @Override  
        public int getCount() {  
            // Show 3 total pages.  
            return 2;  
        }  
  
        @Override  
        public CharSequence getPageTitle(int position) {  
            Locale l = Locale.getDefault();  
            switch (position) {  
            case 0:  
                return "By Ranking".toUpperCase(l);  
            case 1:  
                return "By Distance".toUpperCase(l);
            }  
            return null;  
        }  
    }  
	
	
    public static class DummySectionFragment extends Fragment {  
        /** 
         * The fragment argument representing the section number for this 
         * fragment. 
         */  
        public static final String OREDERING = "orderBy";  
        private ListView list;
        public DummySectionFragment() {  } 
  
        @Override  
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	View rootView = inflater.inflate(R.layout.fragment_restaurants, container, false);   
            return rootView;  
        }  
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
    		list = (ListView)view.findViewById(R.id.listOfRestaurants);
    		list.setAdapter(restaurantsAdapter);
    		list.setOnItemClickListener(new OnItemClickListener() {

    			@Override
    			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
    					long arg3) {
    				//Intent i = new Intent(getParentFragment().getActivity().getApplicationContext(), RestaurantFragment.class);
    				Bundle bundle = new Bundle();
    				int id = ((Restaurant)((RestaurantsAdapter)arg0.getAdapter()).getItem(arg2)).getId();
    				bundle.putInt(RestaurantFragment.RESTAURANT_ID, id);
    				
    				Fragment fragment = Fragment.instantiate(getActivity(), (new RestaurantFragment()).getClass().getName());
    	   			fragment.setArguments(bundle);
    	    		((MainActivity)getActivity()).changeFragment(fragment);  				
    			}
    		});
        	super.onViewCreated(view, savedInstanceState);
        }
    	@Override
    	public void onDetach() {
    	    super.onDetach();

    	    try {
    	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
    	        childFragmentManager.setAccessible(true);
    	        childFragmentManager.set(this, null);

    	    } catch (NoSuchFieldException e) {
    	        throw new RuntimeException(e);
    	    } catch (IllegalAccessException e) {
    	        throw new RuntimeException(e);
    	    }
    	}

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
			JSONArray arr = new JSONArray();
			try {
				TechnionFoodApp.isJSONError(result);
				arr = new JSONArray(result);
				restaurantsAdapter.setRestaurants(arr, RestaurantsAdapter.BY_RANKING);
			} catch (Exception e) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setTitle("Error").setMessage(e.getMessage());
				alertDialogBuilder.setCancelable(false)
				.setPositiveButton("Retry",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						getParentFragment().getActivity().finish();
						startActivity(new Intent(getParentFragment().getActivity().getApplicationContext(), RestaurantsFragment.class));
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
	
}

