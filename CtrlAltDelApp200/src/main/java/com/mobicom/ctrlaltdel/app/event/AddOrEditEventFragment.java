package com.mobicom.ctrlaltdel.app.event;


import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.timezonepicker.TimeZoneInfo;
import com.doomonafireball.betterpickers.timezonepicker.TimeZonePickerDialog;
import com.mobicom.ctrlaltdel.app.amain.R;
import com.mobicom.ctrlaltdel.app.database.CourseDatabaseHelper;
import com.mobicom.ctrlaltdel.app.model.Event;
import com.mobicom.ctrlaltdel.app.model.FixTime;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link AddOrEditEventFragment.OnFragmentInteractionListener}
 * interface to handle interaction events.
 *
 */
public class AddOrEditEventFragment extends Fragment
		implements CalendarDatePickerDialog.OnDateSetListener,
				   RadialTimePickerDialog.OnTimeSetListener,
				   TimeZonePickerDialog.OnTimeZoneSetListener
{
	
	private static final String FRAG_TAG_DATE_PICKER_START = "date_picker_dialog_fragment_start";
	private static final String FRAG_TAG_DATE_PICKER_END = "date_picker_dialog_fragment_end";
	
	private static final String FRAG_TAG_TIME_PICKER_START = "time_picker_dialog_fragment_start";
	private static final String FRAG_TAG_TIME_PICKER_END = "time_picker_dialog_fragment_end";
	
	private static final String FRAG_TAG_TIME_ZONE_PICKER = "time_zone_picker_dialog_fragment";
	
//	private static final String FRAG_TAG_RECUR_PICKER = "recurrence_picker_dialog_fragment";
	
//	private EventRecurrence mEventRecurrence = new EventRecurrence();
//    private String mRrule;

	private OnFragmentInteractionListener mCallBack;
	
	private ImageButton imgBtnCancel;
	private ImageButton imgBtnAccept;
	
	private EditText etEventName;
	private EditText etEventLocation;
	private EditText etEventDescription;
	
	private CheckBox cbAllDay;
	
	private TextView tvStartDate;
	private TextView tvEndDate;
	private TextView tvStartTime;
	private TextView tvEndTime;
	private TextView tvTimeZone;
//	private TextView tvRecurrence;
	
//	private Spinner sMinutes;
//	private TextView tvNotification;

	private Calendar startMillis;
	private Calendar endMillis;
	
	private Spinner spinnerCourses;
	
	private boolean isEdit;
	private Event editEvent;
	
	public AddOrEditEventFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
//		setHasOptionsMenu(true);
//		 //disable application icon from ActionBar
//	    getActivity().getActionBar().setDisplayShowHomeEnabled(false);

//	    //disable application name from ActionBar
//	    getActivity().getActionBar().setDisplayShowTitleEnabled(false);
		
		getActivity().getActionBar().hide();
		
		this.startMillis = Calendar.getInstance();
		this.endMillis = Calendar.getInstance();
		
		Bundle bundle = getArguments();
		
		this.isEdit = bundle.getBoolean("edit");
		this.editEvent = new Event();
		
		if(isEdit == true) 
		{
			this.editEvent = bundle.getParcelable("editevent");
			Log.i("DEBUG_3", "title = " + editEvent.getTitle());
			//this.editEventView();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View rootView =  inflater.inflate(R.layout.fragment_add_or_edit_event, container, false);
		
		this.imgBtnCancel = (ImageButton) rootView.findViewById(R.id.event_cancel);
		this.imgBtnCancel.setOnClickListener(cancel);
		this.imgBtnAccept = (ImageButton) rootView.findViewById(R.id.event_accept);
		this.imgBtnAccept.setOnClickListener(accept);
		
		this.etEventName = (EditText) rootView.findViewById(R.id.et_event_name);
		this.etEventLocation = (EditText) rootView.findViewById(R.id.et_event_location);
		this.etEventDescription = (EditText) rootView.findViewById(R.id.et_event_description);
		
		this.cbAllDay = (CheckBox) rootView.findViewById(R.id.cb_allday);
		
		this.tvStartDate = (TextView) rootView.findViewById(R.id.tv_start_date);
		this.tvEndDate = (TextView) rootView.findViewById(R.id.tv_end_date);
		this.tvStartTime = (TextView) rootView.findViewById(R.id.tv_start_time);
		this.tvEndTime = (TextView) rootView.findViewById(R.id.tv_end_time);
		this.tvTimeZone = (TextView) rootView.findViewById(R.id.tv_timezone);
		this.tvTimeZone.setText("Philippine Standard Time (GMT+8)");
		
//		this.tvRecurrence = (TextView) rootView.findViewById(R.id.tv_event_recurrence);
//		this.sMinutes = (Spinner) rootView.findViewById(R.id.spinner_minutes);
//		this.tvNotification = (TextView) rootView.findViewById(R.id.tv_notification);
		
//		ArrayAdapter<String> spinnerAdapter 
//			= new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, R.array.array_spinner_reminder);
//		this.sMinutes.setAdapter(spinnerAdapter);
		
		this.spinnerCourses = (Spinner) rootView.findViewById(R.id.spinner_event_saved_courses);
		
		CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(getActivity().getApplicationContext(),
				"courses", null, 1);
		
		ArrayList<String> courseList = dbHelper.getCourseNames();
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), 
				R.layout.event_course_spinner_dropdown_item, courseList);
	
		spinnerAdapter.setDropDownViewResource(R.layout.graphic_note_camera_spinner_dropdown_item);
		this.spinnerCourses.setAdapter(spinnerAdapter);
		
		if(this.isEdit == true) {
			this.editEventView();
			
			DateTime start = new DateTime(this.editEvent.getStartMillis());
			DateTime end = new DateTime(this.editEvent.getEndMillis());
			
			Log.i("EDITEVENT", "Dstart = " + this.dateTvView(start, false));
			Log.i("EDITEVENT", "Dend = " + this.dateTvView(end, false));
			
			this.tvStartDate.setText(this.dateTvView(start, false));
			this.tvEndDate.setText(this.dateTvView(end, false));
			
			Log.i("EDITEVENT", "Tstart = " + this.timeTvView(start, false));
			Log.i("EDITEVENT", "Tend = " + this.timeTvView(end, false));
			
			this.tvStartTime.setText(this.timeTvView(start, false));
			this.tvEndTime.setText(this.timeTvView(end, false));
		}
		else {
			DateTime now = DateTime.now();
			
			this.tvStartDate.setText(this.dateTvView(now, false));
			this.tvEndDate.setText(this.dateTvView(now, false));
			
			this.tvStartTime.setText(this.timeTvView(now, false));
			this.tvEndTime.setText(this.timeTvView(now, true));
		}
		
		this.cbAllDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(((CheckBox) v).isChecked()) {
					tvStartTime.setVisibility(View.GONE);
					tvEndTime.setVisibility(View.GONE);
					tvTimeZone.setVisibility(View.GONE);
				}
				else {
					tvStartTime.setVisibility(View.VISIBLE);
					tvEndTime.setVisibility(View.VISIBLE);
					tvTimeZone.setVisibility(View.VISIBLE);
				}
			}
			
		});
		
		this.tvStartDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getChildFragmentManager();
				DateTime now = DateTime.now();
	                
	            if(isEdit == true) 
	            	now = new DateTime(editEvent.getStartMillis());
				
				CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(AddOrEditEventFragment.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER_START);
			}
			
		});
		
		this.tvEndDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getChildFragmentManager();
                DateTime now = DateTime.now();
                
                if(isEdit == true) 
					now = new DateTime(editEvent.getEndMillis());
                
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(AddOrEditEventFragment.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER_END);	
			}
			
		});
		
		this.tvStartTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getChildFragmentManager();
				DateTime now = DateTime.now();
				
				if(isEdit == true) 
					now = new DateTime(editEvent.getStartMillis());
				
	            RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
	            			.newInstance(AddOrEditEventFragment.this, now.getHourOfDay(), now.getMinuteOfHour(),
	            					DateFormat.is24HourFormat(rootView.getContext()));
	            timePickerDialog.show(fm, FRAG_TAG_TIME_PICKER_START);
				
			}
			
		});
		
		this.tvEndTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getChildFragmentManager();
				DateTime now = DateTime.now();
				now = now.plusHours(1);
				
				if(isEdit == true) 
					now = new DateTime(editEvent.getEndMillis());
				
	            RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
	            		.newInstance(AddOrEditEventFragment.this, now.getHourOfDay(), now.getMinuteOfHour(),
	            				DateFormat.is24HourFormat(rootView.getContext()));
	            timePickerDialog.show(fm, FRAG_TAG_TIME_PICKER_END);
			}
			
		});
		
//		this.tvTimeZone.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				FragmentManager fm = getChildFragmentManager();
//                Bundle bundle = new Bundle();
//                Time time = new Time();
//                time.setToNow();
//                bundle.putLong(TimeZonePickerDialog.BUNDLE_START_TIME_MILLIS, time.toMillis(false));
//                bundle.putString(TimeZonePickerDialog.BUNDLE_TIME_ZONE, time.timezone);
//
//                // may be more efficient to serialize and pass in EventRecurrence
////                bundle.putString(RecurrencePickerDialog.BUNDLE_RRULE, mRrule);
//
//                TimeZonePickerDialog tzpd = (TimeZonePickerDialog) fm
//                        .findFragmentByTag(FRAG_TAG_TIME_ZONE_PICKER);
//               
//                if (tzpd != null) {
//                    tzpd.dismiss();
//                }
//                
//                tzpd = new TimeZonePickerDialog();
//                tzpd.setArguments(bundle);
//                tzpd.setOnTimeZoneSetListener(AddOrEditEventFragment.this);
//                tzpd.show(fm, FRAG_TAG_TIME_ZONE_PICKER);			
//			}
//			
//		});
		
//		this.tvRecurrence.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fm = getChildFragmentManager();
//                Bundle b = new Bundle();
//                Time t = new Time();
//                t.setToNow();
//                b.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS, t.toMillis(false));
//                b.putString(RecurrencePickerDialog.BUNDLE_TIME_ZONE, t.timezone);
//
//                // may be more efficient to serialize and pass in EventRecurrence
//                b.putString(RecurrencePickerDialog.BUNDLE_RRULE, mRrule);
//
//                RecurrencePickerDialog rpd = (RecurrencePickerDialog) fm.findFragmentByTag(
//                        FRAG_TAG_RECUR_PICKER);
//                if (rpd != null) {
//                    rpd.dismiss();
//                }
//                rpd = new RecurrencePickerDialog();
//                rpd.setArguments(b);
//                rpd.setOnRecurrenceSetListener(AddOrEditEventFragment.this);
//                rpd.show(fm, FRAG_TAG_RECUR_PICKER);
//            }
//        });
		
		
		return rootView;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(boolean isCancel, int eventOrCancel, Event event, boolean isEdit) {
		if (mCallBack != null) {
			mCallBack.onGoBackToMainFragmentInteraction(isCancel, eventOrCancel);
			mCallBack.onAddOrEditEventSaveToDataBase(event, isEdit);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallBack = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallBack = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onGoBackToMainFragmentInteraction(boolean isCancel, int eventOrCourse);
		
		public void onAddOrEditEventSaveToDataBase(Event event, boolean isEdit);
	}

	@Override
	public void onDateSet(CalendarDatePickerDialog dialog, int year,
			int monthOfYear, int dayOfMonth) {
		
		CalendarDatePickerDialog startDialog = (CalendarDatePickerDialog) getChildFragmentManager()
				.findFragmentByTag(FRAG_TAG_DATE_PICKER_START);
		    
		CalendarDatePickerDialog endDialog = (CalendarDatePickerDialog) getChildFragmentManager()
				.findFragmentByTag(FRAG_TAG_DATE_PICKER_END);
		  
		if(startDialog != null && dialog.getId() == startDialog.getId()) {
			tvStartDate.setText((monthOfYear+1) + " - " + dayOfMonth + " - " + year);
				
			startMillis.set(year, monthOfYear, dayOfMonth);
		}
		
		if(endDialog != null && dialog.getId() == endDialog.getId()) {
			tvEndDate.setText((monthOfYear+1) + " - " + dayOfMonth + " - " + year);
			
			endMillis.set(year, monthOfYear, dayOfMonth);
		}
	}

	@Override
	public void onTimeSet(RadialTimePickerDialog dialog, int hourOfDay,
			int minute) {
		RadialTimePickerDialog rtpdStart 
			= (RadialTimePickerDialog) getChildFragmentManager().findFragmentByTag(
               FRAG_TAG_TIME_PICKER_START);
	    
	    RadialTimePickerDialog rtpdEnd 
	    	= (RadialTimePickerDialog) getChildFragmentManager().findFragmentByTag(
               FRAG_TAG_TIME_PICKER_END);
	    
	    if(rtpdStart != null) {
	    	tvStartTime.setText(new FixTime(hourOfDay, minute).getFixedTime());
	    	
	    	startMillis.set(Calendar.HOUR_OF_DAY, hourOfDay);
	    	startMillis.set(Calendar.MINUTE, minute);
	    }
	    
	    if(rtpdEnd != null) {
	    	tvEndTime.setText(new FixTime(hourOfDay, minute).getFixedTime());
	    	
	    	endMillis.set(Calendar.HOUR_OF_DAY, hourOfDay);
	    	endMillis.set(Calendar.MINUTE, minute);
	    }
	}
	
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		super.onCreateOptionsMenu(menu, inflater);
//		
//		inflater.inflate(R.menu.new_fragment, menu);
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		
//		switch(item.getItemId()) {
//			case R.id.action_done:
//				mCallBack.onNewFragmentInteraction();
//				
//				mCallBack.onAddOrEditEventSaveToDataBase(this.addOrEditEvent(), this.isEdit);
//				/*insert event into google calendar*/
////				insertGoogleCalendarEvent();
//				
////				mCallBack.onNewEventSaveToDataBase(true);
//				return true;
//			default:
//				return super.onOptionsItemSelected(item);
//		}
//		
//	}
	
	OnClickListener accept = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			mCallBack.onAddOrEditEventSaveToDataBase(addOrEditEvent(), isEdit);
		}
		
	};
	
	OnClickListener cancel = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mCallBack.onGoBackToMainFragmentInteraction(true, 1);
		}
		
	};
	
	public Event addOrEditEvent() {
		Event event = new Event();
		
		if(this.isEdit == true) {
			
			event.setID(this.editEvent.getID());
		}
		
		event.setTitle(this.etEventName.getText().toString());
		event.setLocation(this.etEventLocation.getText().toString());
		event.setDescription(this.etEventDescription.getText().toString());
		event.setIsAllDay(this.cbAllDay.isChecked());
		event.setStartMillis(this.startMillis.getTimeInMillis());
		event.setEndMillis(this.endMillis.getTimeInMillis());
//		event.setRrule(this.mRrule);
		
//		Log.i("REMINDER", "minutes = " + this.convertTheSpinnerReminderString());
//		event.setReminderTime(this.convertTheSpinnerReminderString());
		
		return event;
	}
	
//	public int convertTheSpinnerReminderString() {
//		
//		String str = this.sMinutes.getSelectedItem().toString();
//		
//		int value = 0;
//		
//		switch(str) {
//			case "10 minutes": value = 10; break;
//			case "15 minutes": value = 15; break;
//			case "20 minutes": value = 20; break;
//			case "25 minutes": value = 25; break;
//			case "30 minutes": value = 30; break;
//			case "45 minutes": value = 45; break;
//			case "1 hour": value = 60; break;
//			case "2 hours": value = 120; break;
//		}
//		
//		return value;
//	}
	
	@Override
	public void onTimeZoneSet(TimeZoneInfo tzi) {
		// TODO Auto-generated method stub
		
	}
	
//	private void insertGoogleCalendarEvent() {
//		// Initialize Calendar service with valid OAuth credentials
//		Calendar service = new CalendarContract(httpTransport, jsonFactory, credentials)
//		    .setApplicationName("applicationName").build();
//
//		// Create and initialize a new event
//		Event event = new Event();
//		event.setSummary("Appointment");
//		event.setLocation("Somewhere");
//
//		DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
//		event.setStart(startDate);
//		event.setStart(new EventDateTime().setDateTime(start));
//		DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
//		event.setEnd(new EventDateTime().setDateTime(end));
//
//		// Insert the new event
//		Event createdEvent = service.events().insert("primary", event).execute();
//	}
	
	public void editEventView() {
		
		this.etEventName.setText(this.editEvent.getTitle());
		this.etEventLocation.setText(this.editEvent.getLocation());
		this.etEventDescription.setText(this.editEvent.getDescription());
		this.cbAllDay.setChecked(this.editEvent.getIsAllDay());
		
		if(this.cbAllDay.isChecked()) {
			tvStartTime.setVisibility(View.GONE);
			tvEndTime.setVisibility(View.GONE);
			tvTimeZone.setVisibility(View.GONE);
		}
	}
	
	public String dateTvView(DateTime now, boolean add) {
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM - dd - yyyy");
		
		if(add == true)
			now = now.plusDays(1);
		
		return formatter.print(now);
	}

	
	public String timeTvView(DateTime now, boolean add) {
		
		if(add == true)
			now = now.plusHours(1);
		
		return new FixTime(now.getHourOfDay(), now.getMinuteOfHour()).getFixedTime();
	}

//	@Override
//	public void onRecurrenceSet(String rrule) {
//		// TODO Auto-generated method stub
//		mRrule = rrule;
//        if (mRrule != null) {
//            mEventRecurrence.parse(mRrule);
//        }
//        
//        this.populateRepeats();
//	}
//	
//	private void populateRepeats() {
//        Resources r = getResources();
//        String repeatString = "";
//        //boolean enabled;
//        if (!TextUtils.isEmpty(mRrule)) {
//            repeatString = EventRecurrenceFormatter
//            					.getRepeatString(getActivity().getBaseContext(), r, mEventRecurrence, true);
//        }
//
////      tvDayOfWeek.setText(mRrule + "\n" + repeatString);
//        this.tvRecurrence.setText(repeatString); 
//        
////      this.day = repeatString;
//  
//        Log.i("RECURRENCE", "mRrule = " + mRrule);
//    }

}
