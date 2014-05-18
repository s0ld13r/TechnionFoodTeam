package com.gmail.technionfoodteam;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
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
	private LinkedList<Restaurant> restaurants;
	private ImageLoader imageLoader = ImageLoader.getInstance(); 
    private Context context;
	public RestaurantsAdapter(JSONArray arr, Context context) {
		restaurants = new LinkedList<Restaurant>();
		for(int i=0; i<arr.length();i++){
			try {
				restaurants.add(Restaurant.fromJSON(arr.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		this.context = context;
		//imageLoader.init(ImageLoaderConfiguration.createDefault(context));
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
			LayoutInflater inflater = (LayoutInflater) context
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
