package com.mobicom.ctrlaltdel.app.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import org.joda.time.DateTime;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import android.app.Service;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.mobicom.ctrlaltdel.app.amain.R;
import com.mobicom.ctrlaltdel.app.model.Event;
import com.mobicom.ctrlaltdel.app.model.FixTime;
import com.mobicom.ctrlaltdel.app.model.NormalDateTime;

public class EventBaseAdapter extends BaseAdapter
		implements StickyListHeadersAdapter, 
				   SectionIndexer {
	
	private final static String TAG = "DEBUGGING";
	
	private Context context;
    private ArrayList<Event> events;
    private LayoutInflater inflater;
    
    private ArrayList<Integer> sectionIndices;
    private NormalDateTime[] sectionDates;
    
    private ArrayList<Event> storageForDisplay;
    
    class HeaderViewHolder {
        TextView tvEventDate;
    }

    class ViewHolder {
        TextView tvEventName;
        TextView tvEventTime;
    }
    
    public EventBaseAdapter(Context context, ArrayList<Event> events, boolean hasContent) {
    	
    	this.context = context;
    	this.events = events;
    	this.inflater = (LayoutInflater) this.context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    	this.storageForDisplay = new ArrayList<Event>();
    	
    	if(hasContent == true) 
    	{
    		this.sortEvents(this.events);
    		
    		this.splitEventsIfPossible();
    		
    		this.sortEvents(this.storageForDisplay);
    		
    		this.sectionIndices = this.getSectionIndices();
    		this.sectionDates = this.getSectionDates();
    	}
    	else {
    		this.sectionIndices = new ArrayList<Integer>();
    		this.sectionDates = new NormalDateTime[0];
    	}
    	
    }
    
    public void sortEvents(ArrayList<Event> eventsToBeSorted) {
    	
    	Collections.sort(eventsToBeSorted, new Comparator<Event>()
    	{

			@Override
			public int compare(Event lhs, Event rhs) {
				if(lhs.getStartDateTime().getForComparingDate() > rhs.getStartDateTime().getForComparingDate())
				{
					return 1;
				}
				else if(lhs.getStartDateTime().getForComparingDate() == rhs.getStartDateTime().getForComparingDate()) 
				{
					if(lhs.getStartDateTime().getForComparingTime() > rhs.getStartDateTime().getForComparingTime())
						return 1;
					else 
						return -1;
				}
				else 
				{
					return -1;
				}
			}
    		
    	});
    }
    
    public void splitEventsIfPossible() {
    		
    	this.storageForDisplay = this.events;
    	
    	for(int i = 0; i < events.size(); i++) 
    	{
    		int days = this.events.get(i).howManyDaysIsTheEvent();
    		
    		Log.i(TAG, "days = " + days);
    		
    		Log.i(TAG, "storage.size = " + this.storageForDisplay.size());
    		
    		if(days > 0 && this.events.get(i).getIsAlreadyChecked() == false)
//    				&& this.events.get(i).getSplit() == false)
    		{
    			this.events.get(i).setIsAlreadyChecked(true);
    			this.events.get(i).setIsSplit(true);
    			
    			Event orig = events.get(i);
    			
    			DateTime dateTime = new DateTime(orig.getStartMillis());
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(dateTime.getYear(), dateTime.getMonthOfYear()-1, dateTime.getDayOfMonth());
				
				Log.i(TAG, "orig millis = " + calendar.getTimeInMillis());
    			
    			for(int j = 1; j <= days; j++) 
    			{	
    				DateTime newDateTime = new DateTime(calendar.getTimeInMillis());
    				newDateTime = newDateTime.plusDays(j);
    				
    				Log.i(TAG, j + "-new startMillis = " + newDateTime.getMillis());
    				
    				Event subevent = new Event();
    				subevent.setPreviousStartMillis(orig.getStartMillis());
    				subevent.setCalendarID(orig.getCalendarID());
    				subevent.setID(orig.getID());
    				subevent.setTitle(orig.getTitle());
    				subevent.setLocation(orig.getLocation());
    				subevent.setDescription(orig.getDescription());
    				subevent.setStartMillis(newDateTime.getMillis());
    				subevent.setEndMillis(orig.getEndMillis());
    				subevent.setIsSplit(true);
    				subevent.setIsAlreadyChecked(true);
    				
    				this.storageForDisplay.add(subevent);
    			}
    		}
    	}

    }
    
    private ArrayList<Integer> getSectionIndices() {
        ArrayList<Integer> tempSectionIndices = new ArrayList<Integer>();
        
        int lastValue = this.storageForDisplay.get(0).getStartDateTime().getForComparingDate();
        tempSectionIndices.add(0);
         
        for (int i = 1; i < this.storageForDisplay.size(); i++) {
        	
        	int compare = this.storageForDisplay.get(i).getStartDateTime().getForComparingDate();
        	
        	Log.i(TAG, i + "-lastValue = " + lastValue);
        	Log.i(TAG, i + "-compare = " + compare);
        	
        	if (compare != lastValue) 
        	{
        		lastValue = compare;
                tempSectionIndices.add(i);
        	}
        }
      
//        ArrayList<Integer> sections = new ArrayList<Integer>();
//        
//        for (int i = 0; i < tempSectionIndices.size(); i++) 
//        {
//        	sections.add(tempSectionIndices.get(i));
//        }
      
        return tempSectionIndices;
    }
    
    private NormalDateTime[] getSectionDates() {
    	NormalDateTime[] dates = new NormalDateTime[this.sectionIndices.size()];
    	
    	for(int i = 0; i < this.sectionIndices.size(); i++) {
    		dates[i] = this.storageForDisplay.get(i).getStartDateTime();
    	}
    	
    	return dates;
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.storageForDisplay.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.storageForDisplay.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		
		if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.event_list_item, parent, false);
            
            holder = new ViewHolder();
            holder.tvEventName = (TextView) convertView.findViewById(R.id.item_tv_event_name);
            holder.tvEventTime = (TextView) convertView.findViewById(R.id.item_tv_event_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
		
		Event event = this.storageForDisplay.get(position);
		
		holder.tvEventName.setText(event.getTitle());
		
		DateTime start = new DateTime(event.getStartMillis());
		DateTime end = new DateTime(event.getEndMillis());
		DateTime previous = new DateTime(event.getPreviousStartMillis());
		
		if(event.getIsAllDay() == true) 
		{
			holder.tvEventTime.setVisibility(View.GONE);
			
		} else if(event.getIsAllDay() == true && event.getIsSplit() == true) 
		{
			holder.tvEventTime.setVisibility(View.GONE);
			
		} else if(event.getIsOriginal() == true && event.getIsSplit() == true) 
		{
			holder.tvEventTime.setText(""
					+ new FixTime(start.getHourOfDay(), start.getMinuteOfHour()).getFixedTime()
					+ " - "
					+ event.getEndDateTime().getMonthString() + " " + event.getEndDateTime().getDay() 
					+ ", "
					+ new FixTime(end.getHourOfDay(), end.getMinuteOfHour()).getFixedTime());
		} else if(event.getIsOriginal() == false && event.getIsSplit() == true) 
		{
			holder.tvEventTime.setText(""
					+ event.getPreviousDateTime().getMonthString() + " " + event.getPreviousDateTime().getDay() 
					+ ", "
					+ new FixTime(previous.getHourOfDay(), previous.getMinuteOfHour()).getFixedTime()
					+ " - "
					+ event.getEndDateTime().getMonthString() + " " + event.getEndDateTime().getDay() 
					+ ", "
					+ new FixTime(end.getHourOfDay(), end.getMinuteOfHour()).getFixedTime());
		} else 
		{
			holder.tvEventTime.setText("" 
					+ new FixTime(start.getHourOfDay(), start.getMinuteOfHour()).getFixedTime()
					+ " - "
					+ new FixTime(end.getHourOfDay(), end.getMinuteOfHour()).getFixedTime());
		}
		
		
		return convertView;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return this.sectionDates;
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		// TODO Auto-generated method stub
		 if (this.sectionIndices.size() == 0) {
	            return 0;
	     }
	        
	     if (sectionIndex >= this.sectionIndices.size()) {
	    	 sectionIndex = this.sectionIndices.size() - 1;
	     } else if (sectionIndex < 0) {
	            sectionIndex = 0;
	     }
	        
	     return this.sectionIndices.get(sectionIndex);
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		for (int i = 0; i < sectionIndices.size(); i++) {
			if (position < sectionIndices.get(i)) {
				
				return i - 1;
	        }
		}
	        
		return this.sectionIndices.size() - 1;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
		
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.event_date_header, parent, false);
	        holder = new HeaderViewHolder();
			holder.tvEventDate = (TextView) convertView.findViewById(R.id.header_tv_event_date);
	        convertView.setTag(holder);
	    } else {
	        holder = (HeaderViewHolder) convertView.getTag();
	    }

	    // set header text as first char in name
//		CharSequence headerChar = mCountries[position].subSequence(0, 1);
//	    holder.text.setText(headerChar);
		Event event = this.storageForDisplay.get(position);
		
		holder.tvEventDate.setText(event.getStartDateTime().getForDisplayHeader());
		
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		// TODO Auto-generated method stub
		return this.storageForDisplay.get(position).getStartDateTime().getForComparingDate();
	}
	
	public void clear() {
		this.events = new ArrayList<Event>();
		this.storageForDisplay  = new ArrayList<Event>();
		this.sectionIndices = new ArrayList<Integer>();
	    this.sectionDates = new NormalDateTime[0];
	    notifyDataSetChanged();
	}

//	public void restore() {
//	    mCountries = context.getResources().getStringArray(R.array.countries);
//	    mSectionIndices = getSectionIndices();
//	    mSectionLetters = getSectionLetters();
//	    notifyDataSetChanged();
//	}

}
