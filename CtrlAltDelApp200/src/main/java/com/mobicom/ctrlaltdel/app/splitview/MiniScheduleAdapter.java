package com.mobicom.ctrlaltdel.app.splitview;

import java.util.List;

import org.joda.time.DateTime;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobicom.ctrlaltdel.app.amain.R;
import com.mobicom.ctrlaltdel.app.model.Event;
import com.mobicom.ctrlaltdel.app.model.FixTime;

public class MiniScheduleAdapter extends ArrayAdapter<Event> {
	
	private TextView tvEventName;
	private TextView tvEventTime;

	public MiniScheduleAdapter(Context context, int resource,
			List<Event> objects) {
		super(context, resource, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.split_view_list_item, parent, false);
		}
		
		this.tvEventName = (TextView) convertView.findViewById(R.id.split_view_tv_event_name);
		this.tvEventTime = (TextView) convertView.findViewById(R.id.split_view_tv_event_time);
		
		Event event = getItem(position);
		
		DateTime start = new DateTime(event.getStartMillis());
		DateTime end = new DateTime(event.getEndMillis());
		
		this.tvEventName.setText(event.getTitle());
		this.tvEventTime.setText(""
					+ new FixTime(start.getHourOfDay(), start.getMinuteOfHour()).getFixedTime()
					+ " - "
					+ event.getEndDateTime().getMonthString() + " " + event.getEndDateTime().getDay() 
					+ ", "
					+ new FixTime(end.getHourOfDay(), end.getMinuteOfHour()).getFixedTime());
		
		return convertView;
	}

}
