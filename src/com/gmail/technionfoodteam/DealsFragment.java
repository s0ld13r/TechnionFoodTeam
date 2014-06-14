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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gmail.technionfoodteam.model.Dish;


public class DealsFragment extends Fragment {
	ListView dealsList;
	DealsAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.deals_fragment_layout, container, false);
		dealsList = (ListView)view.findViewById(R.id.dealsList);
		dealsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Bundle bundle = new Bundle();
				int dishid = ((Dish)((DealsAdapter)parent.getAdapter()).getItem(position)).getId();
				bundle.putInt(DishFragment.DISH_ID, dishid);
				
				Fragment fragment = Fragment.instantiate(getActivity(), (new DishFragment()).getClass().getName());
	   			fragment.setArguments(bundle);
	    		((MainActivity)getActivity()).changeFragment(fragment); 
				
			}
		});
		adapter = new DealsAdapter(getActivity());
		dealsList.setAdapter(adapter);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	 
		GetDealsFromServer thread = new GetDealsFromServer();
		thread.execute();
	
	}
	private class GetDealsFromServer extends AsyncTask<Void, Void, String>{
		private HttpURLConnection connection;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			((MainActivity)getActivity()).ringProgressDialog = ProgressDialog.show((MainActivity)getActivity(), "Please wait ...", "Downloading Deals from server ...", true);
			((MainActivity)getActivity()).ringProgressDialog.setCancelable(true);
		}
		@Override
		protected String doInBackground(Void... params) {
			try{
				String path = TechnionFoodApp.pathToServer + "service/deals";
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
			if(((MainActivity)getActivity()).ringProgressDialog != null){
				((MainActivity)getActivity()).ringProgressDialog.dismiss();
			}
			JSONArray arr = new JSONArray();
			try {
				TechnionFoodApp.isJSONError(result);
				arr = new JSONArray(result);
				adapter.update(arr);
				
			} catch (Exception e) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setTitle("Error").setMessage(e.getMessage());
				alertDialogBuilder.setCancelable(false)
				.setPositiveButton("Retry",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						GetDealsFromServer thread = new GetDealsFromServer();
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