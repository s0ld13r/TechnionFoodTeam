package com.gmail.technionfoodteam;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gmail.technionfoodteam.model.Restaurant;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RestaurantsAdapter extends BaseAdapter {
	public static final int BY_RANKING = 0;
	public static final int BY_DISTANCE = 1;
	private int orderBy;
	private LinkedList<Restaurant> restaurants;
	private ImageLoader imageLoader = ImageLoader.getInstance(); 
    private TechnionFoodApp app;
    
    public RestaurantsAdapter(TechnionFoodApp myApp) {
		orderBy = BY_RANKING;
		restaurants = new LinkedList<Restaurant>();
		this.app = myApp;
	}
    public RestaurantsAdapter(JSONArray arr,TechnionFoodApp myApp) {
		orderBy = BY_RANKING;
		setRestaurantsListBy(arr, BY_RANKING);
		this.app = myApp;
	}
	private void setRestaurantsListBy(JSONArray arr, int order){
		restaurants = new LinkedList<Restaurant>();
		
		for(int i=0; i<arr.length();i++){
			try {
				restaurants.add(Restaurant.fromJSON(arr.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		setOrderTo(order);
	}
	public void setOrderTo(int newOrder){
		if(newOrder != orderBy){
			if(newOrder == BY_RANKING){
				Comparator<Restaurant> rankingComperator = new Comparator<Restaurant>() {					
					@Override
					public int compare(Restaurant lhs, Restaurant rhs) {
						return (Double.valueOf(lhs.getRanking())).compareTo(Double.valueOf(rhs.getRanking()));
					}
				};
				
				Collections.sort(restaurants, Collections.reverseOrder(rankingComperator));
			}else if(newOrder == BY_DISTANCE){
				Comparator<Restaurant> distanceComperator = new Comparator<Restaurant>() {					
					@Override
					public int compare(Restaurant lhs, Restaurant rhs) {
						Location lhsLocation = new Location("Left POint");
						lhsLocation.setLatitude(lhs.getLat());
						lhsLocation.setLongitude(lhs.getLng());
						Location rhsLocation = new Location("Right POint");
						rhsLocation.setLatitude(rhs.getLat());
						rhsLocation.setLongitude(rhs.getLng());
						Location current = app.getCurrentLocation();
						return Float.valueOf(current.distanceTo(lhsLocation)).compareTo(Float.valueOf(current.distanceTo(rhsLocation)));
					}
				};
				Collections.sort(restaurants, distanceComperator);
			}
			orderBy = newOrder;
			notifyDataSetChanged();
		}
	}
	public void setRestaurants(JSONArray arr, int order){
		setRestaurantsListBy(arr,order);
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return restaurants.size();
	}

	@Override
	public Object getItem(int position) {
		return restaurants.get(position);
	}

	@Override
	public long getItemId(int position) {
		return restaurants.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) app
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.restaurant_list_item, parent, false);
			holder = new ViewHolder();
			holder.logoIv = (ImageView)view.findViewById(R.id.image_restaurant_list__item);
			holder.nameTv= (TextView)view.findViewById(R.id.dish_name_restaurant_list__item);
			holder.distanceTv= (TextView)view.findViewById(R.id.distance_dish_list__item);;
			holder.ratingRb= (RatingBar)view.findViewById(R.id.rating_dish_list__item);
			view.setTag(holder);
	    }else{
	    	holder = (ViewHolder) view.getTag();
	    }
		Restaurant restaurant = (Restaurant)getItem(position);
	    holder.nameTv.setText(restaurant.getName());
	    holder.distanceTv.setText(restaurant.getAddress());
	    holder.ratingRb.setRating((float)restaurant.getRanking());
	    String path = restaurant.getPathToLogo();
	    imageLoader.displayImage(path, holder.logoIv);
	    return view;
	}
	private class ViewHolder {
		public ImageView logoIv;
		public TextView nameTv;
		public TextView distanceTv;
		public RatingBar ratingRb;
	}

}
