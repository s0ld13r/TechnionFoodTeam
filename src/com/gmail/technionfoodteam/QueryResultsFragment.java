package com.gmail.technionfoodteam;

import java.lang.reflect.Field;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gmail.technionfoodteam.model.Dish;

public class QueryResultsFragment extends Fragment{
	private static QueryDishesAdapter queryDishesAdapter;
	
	private SectionsPagerAdapter mSectionsPagerAdapter;  
    private ViewPager mViewPager; 
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
		View view = inflater.inflate(R.layout.pager_fragment,
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
    	 queryDishesAdapter = ((MainActivity)getActivity()).getQueryDishesAdapter();
    	 mViewPager.setAdapter(mSectionsPagerAdapter);
    	 mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
 			
 			@Override
 			public void onPageSelected(int arg0) {
 				queryDishesAdapter.setOrderTo(arg0);
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
            return 3;  
        }  
  
        @Override  
        public CharSequence getPageTitle(int position) {  
            Locale l = Locale.getDefault();  
            switch (position) {  
            case 0:  
                return "By Price".toUpperCase(l);  
            case 1:  
                return "By Ranking".toUpperCase(l);
            case 2:  
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
        	View rootView = inflater.inflate(R.layout.fragment_list, container, false);   
            return rootView;  
        }  
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
    		list = (ListView)view.findViewById(R.id.listOfItems);
    		list.setAdapter(queryDishesAdapter);
    		int sortingBy = getArguments().getInt(OREDERING);
    		queryDishesAdapter.setOrderTo(sortingBy);
    		list.setOnItemClickListener(new OnItemClickListener() {

    			@Override
    			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
    					long arg3) {
    				//Intent i = new Intent(getParentFragment().getActivity().getApplicationContext(), RestaurantFragment.class);
    				Bundle bundle = new Bundle();
    				int id = ((Dish)((QueryDishesAdapter)arg0.getAdapter()).getItem(arg2)).getId();
    				bundle.putInt(DishFragment.DISH_ID, id);
    				
    				Fragment fragment = Fragment.instantiate(getActivity(), (new DishFragment()).getClass().getName());
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
		
}

