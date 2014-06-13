package com.gmail.technionfoodteam;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gmail.technionfoodteam.model.Dish;
import com.nostra13.universalimageloader.core.ImageLoader;

public class QueryDishesAdapter extends BaseAdapter {
	public static final int ORDER_PRICE = 0;
	public static final int ORDER_RANKING = 1;
	public static final int ORDER_DISTANCE = 2;

	private int orderBy = -1;
	private LinkedList<Dish> dishes;
	private ImageLoader imageLoader = ImageLoader.getInstance(); 
    private Activity activity;
    public QueryDishesAdapter(Activity activity){
    	this.activity =  activity;
    	dishes = new LinkedList<Dish>();
    	orderBy = -1;
    }
	public void update(JSONArray arr) {
		dishes = new LinkedList<Dish>();
		for(int i=0; i<arr.length();i++){
			try {
				dishes.add(Dish.fromJSON(arr.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return dishes.size();
	}

	@Override
	public Object getItem(int position) {
		return dishes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return dishes.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) activity
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.deal_list_item, parent, false);
			holder = new ViewHolder();
			holder.logoIv = (ImageView)view.findViewById(R.id.image_deal_list_item);
			holder.nameTv= (TextView)view.findViewById(R.id.deal_name_deal_list_item);
			holder.descTv= (TextView)view.findViewById(R.id.deal_desc_deal_list_item);
			holder.restTv= (TextView)view.findViewById(R.id.deal_rest_deal_list_item);
			holder.priceTv= (TextView)view.findViewById(R.id.price_deal_list_item);
			//holder.priceUnitsTv= (TextView)view.findViewById(R.id.price_units_dish_list_item);
			holder.ratingRb= (RatingBar)view.findViewById(R.id.rating_deal_list_item);
			view.setTag(holder);
	    }else{
	    	holder = (ViewHolder) view.getTag();
	    }
		Dish dish = (Dish)getItem(position);
	    holder.nameTv.setText(dish.getName());
	    holder.priceTv.setText(dish.getPrice()+"");
	    holder.ratingRb.setRating((float)dish.getRanking());
	    holder.restTv.setText(dish.getRestaurantName());
	    holder.descTv.setText(dish.getDescription());
	    String path = dish.getPhoto();
	    imageLoader.displayImage(path, holder.logoIv);
	    return view;
	}
	private class ViewHolder {
		public ImageView logoIv;
		public TextView nameTv;
		public TextView priceTv;
		public TextView descTv;
		public TextView restTv;
		//public TextView priceUnitsTv;
		public RatingBar ratingRb;
	}
	public void setOrderTo(int newOrder){
		if(newOrder != orderBy){
			if(newOrder == ORDER_RANKING){
				Comparator<Dish> rankingComperator = new Comparator<Dish>() {					
					@Override
					public int compare(Dish lhs, Dish rhs) {
						return (Double.valueOf(lhs.getRanking())).compareTo(Double.valueOf(rhs.getRanking()));
					}
				};
				Collections.sort(dishes, Collections.reverseOrder(rankingComperator));
				
			}else if(newOrder == ORDER_DISTANCE){
				Comparator<Dish> distanceComperator = new Comparator<Dish>() {					
					@Override
					public int compare(Dish lhs, Dish rhs) {
						Location lhsLocation = new Location("Left POint");
						lhsLocation.setLatitude(lhs.getRestLat());
						lhsLocation.setLongitude(lhs.getRestLng());
						Location rhsLocation = new Location("Right POint");
						rhsLocation.setLatitude(rhs.getRestLat());
						rhsLocation.setLongitude(rhs.getRestLng());
						Location current = ((TechnionFoodApp)activity.getApplication()).getCurrentLocation();
						return Float.valueOf(current.distanceTo(lhsLocation)).compareTo(Float.valueOf(current.distanceTo(rhsLocation)));
					}
				};
				Collections.sort(dishes, distanceComperator);
			}else if(newOrder == ORDER_PRICE){
				Comparator<Dish> priceComperator = new Comparator<Dish>() {					
					@Override
					public int compare(Dish lhs, Dish rhs) {
						return (Double.valueOf(lhs.getPrice())).compareTo(Double.valueOf(rhs.getPrice()));
					}
				};
				
				Collections.sort(dishes, priceComperator);
			}
			orderBy = newOrder;
			notifyDataSetChanged();
		}
	}
}