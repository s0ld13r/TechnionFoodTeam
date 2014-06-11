package com.gmail.technionfoodteam;

import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gmail.technionfoodteam.model.Dish;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DishesAdapter extends BaseExpandableListAdapter  {
	private HashMap<Integer, LinkedList<Dish>> map;
	private LinkedList<Integer> keys;
	private HashMap<Integer, String> typeValuesMap;
	private ImageLoader imageLoader = ImageLoader.getInstance(); 
    private Context context;
	public DishesAdapter(JSONArray arr, Context context, HashMap<Integer, String> valuesMap) {
		typeValuesMap = valuesMap;
		map = new HashMap<Integer, LinkedList<Dish>>();
		keys = new LinkedList<Integer>();
		
		for(int i=0; i<arr.length();i++){
			try {
				Dish dish = Dish.fromJSON(arr.getJSONObject(i));
				if(keys.indexOf(dish.getDishType()) == -1){
					keys.add(dish.getDishType());
					LinkedList<Dish> lst = new LinkedList<Dish>();
					map.put(dish.getDishType(), lst);
				}
				map.get(dish.getDishType()).add(dish);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		this.context = context;
	}
	@Override
    public Object getChild(int groupPosition, int childPosititon) {
        
		return map.get(keys.get(groupPosition)).get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

	@Override
	public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
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
		Dish dish = (Dish)getChild(groupPosition, childPosition);
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
	@Override
    public int getChildrenCount(int groupPosition) {
		return map.get(keys.get(groupPosition)).size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return map.get(keys.get(groupPosition));
    }
 
    @Override
    public int getGroupCount() {
        return keys.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
    	String headerTitle = "";
    	try{
	    	String val = typeValuesMap.get(keys.get(groupPosition));
	    	headerTitle = val;
	    	if (val==null){
	    		headerTitle = " Unknown group at position " + groupPosition;
	    	}
    	}catch(Exception e){
    		headerTitle = " Unknown group at position " + groupPosition;
    	}
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
 
        CheckedTextView  lblListHeader = (CheckedTextView ) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        lblListHeader.setChecked(isExpanded);
        //lblListHeader.setCheckMarkDrawable(R.drawable.list_selector);
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
