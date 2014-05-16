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

import com.gmail.technionfoodteam.model.Dish;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DishesAdapter extends BaseAdapter {
	private LinkedList<Dish> dishes;
	private ImageLoader imageLoader = ImageLoader.getInstance(); 
    private Context context;
	public DishesAdapter(JSONArray arr, Context context) {
		dishes = new LinkedList<Dish>();
		for(int i=0; i<arr.length();i++){
			try {
				dishes.add(Dish.fromJSON(arr.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		this.context = context;
		//imageLoader.init(ImageLoaderConfiguration.createDefault(context));
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
			LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.dish_list_item, parent, false);
			holder = new ViewHolder();
			holder.logoIv = (ImageView)view.findViewById(R.id.image_dish_list_item);
			holder.nameTv= (TextView)view.findViewById(R.id.dish_name_dish_list_item);
			holder.priceTv= (TextView)view.findViewById(R.id.price_dish_list_item);
			//holder.priceUnitsTv= (TextView)view.findViewById(R.id.price_units_dish_list_item);
			holder.ratingRb= (RatingBar)view.findViewById(R.id.rating_dish_list_item);
			view.setTag(holder);
	    }else{
	    	holder = (ViewHolder) view.getTag();
	    }
		Dish dish = (Dish)getItem(position);
	    holder.nameTv.setText(dish.getName());
	    holder.priceTv.setText(dish.getPrice()+"");
	    holder.ratingRb.setRating((float)dish.getRanking());
	    String path = dish.getPhoto();
	    imageLoader.displayImage(path, holder.logoIv);
	    return view;
	}
	private class ViewHolder {
		public ImageView logoIv;
		public TextView nameTv;
		public TextView priceTv;
		//public TextView priceUnitsTv;
		public RatingBar ratingRb;
	}
}
