package com.gmail.technionfoodteam;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gmail.technionfoodteam.model.Review;

public class ReviewsAdapter extends BaseAdapter {
	private LinkedList<Review> reviews;
    private Context context;
	public ReviewsAdapter(JSONArray arr, Context context) {
		reviews = new LinkedList<Review>();
		for(int i=0; i<arr.length();i++){
			try {
				reviews.add(Review.fromJSON(arr.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		this.context = context;
	}
	@Override
	public int getCount() {
		return reviews.size();
	}

	@Override
	public Object getItem(int position) {
		return reviews.get(position);
	}

	@Override
	public long getItemId(int position) {
		return reviews.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.review_list_item, parent, false);
			holder = new ViewHolder();
			holder.nameTv= (TextView)view.findViewById(R.id.rli_username);
			holder.descriptionTv= (TextView)view.findViewById(R.id.rli_description);
			holder.ratingRb= (RatingBar)view.findViewById(R.id.rli_rating);
			view.setTag(holder);
	    }else{
	    	holder = (ViewHolder) view.getTag();
	    }
		Review review = (Review)getItem(position);
	    holder.nameTv.setText(review.getUsername());
	    holder.descriptionTv.setText(review.getDescription());
	    holder.ratingRb.setRating((float)review.getRanking());
	    return view;
	}
	private class ViewHolder {
		public TextView nameTv;
		public TextView descriptionTv;
		public RatingBar ratingRb;
	}
}
