package com.mobicom.ctrlaltdel.app.course;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;
import com.mobicom.ctrlaltdel.app.amain.R;
import com.mobicom.ctrlaltdel.app.database.CourseDatabaseHelper;
import com.mobicom.ctrlaltdel.app.model.Course;
import com.mobicom.ctrlaltdel.app.model.FixTime;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link AddOrEditCourseFragment.OnFragmentInteractionListener}
 * interface to handle interaction events.
 *
 */
public class AddOrEditCourseFragment extends Fragment
		implements RadialTimePickerDialog.OnTimeSetListener,
				   RecurrencePickerDialog.OnRecurrenceSetListener {
	
	private static final String FRAG_TAG_RECUR_PICKER = "recurrence_picker_dialog_fragment";
	
	private static final String FRAG_TAG_TIME_PICKER_START = "time_picker_dialog_fragment_start";
	private static final String FRAG_TAG_TIME_PICKER_END = "time_picker_dialog_fragment_end";
	
	private OnFragmentInteractionListener mCallBack;
	
	private EventRecurrence mEventRecurrence = new EventRecurrence();
	
	private String mRrule;
	
	private ImageButton imgButtonCancel;
	private ImageButton imgButtonAccept;
	
	private EditText etCourseName;
	private EditText etCourseLocation; 
	
	private TextView tvDayOfWeek;
	private TextView tvStartTime;
	private TextView tvEndTime;
	
	private String dayOfWeek;
	private String starttime;
	private String endtime;
	
	// database
	private CourseDatabaseHelper courseDBHelper;
	
	public AddOrEditCourseFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActivity().getActionBar().hide();
		
		this.dayOfWeek = "";
		this.starttime = "";
		this.endtime = "";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View rootView = inflater.inflate(R.layout.fragment_add_or_edit_course, container, false);
		
		this.imgButtonCancel = (ImageButton) rootView.findViewById(R.id.course_cancel);
		this.imgButtonCancel.setOnClickListener(cancel);
		this.imgButtonAccept = (ImageButton) rootView.findViewById(R.id.course_accept);
		this.imgButtonAccept.setOnClickListener(accept);

		this.etCourseName = (EditText) rootView.findViewById(R.id.et_course_name);
		this.etCourseLocation = (EditText) rootView.findViewById(R.id.et_course_location);
		
		this.tvDayOfWeek = (TextView) rootView.findViewById(R.id.tv_course_day);
		this.tvStartTime = (TextView) rootView.findViewById(R.id.tv_course_start_time);
		this.tvEndTime = (TextView) rootView.findViewById(R.id.tv_course_end_time);
		
		this.tvDayOfWeek.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getChildFragmentManager();
                Bundle b = new Bundle();
                Time t = new Time();
                t.setToNow();
                b.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS, t.toMillis(false));
                b.putString(RecurrencePickerDialog.BUNDLE_TIME_ZONE, t.timezone);

                // may be more efficient to serialize and pass in EventRecurrence
                b.putString(RecurrencePickerDialog.BUNDLE_RRULE, mRrule);

                RecurrencePickerDialog rpd = (RecurrencePickerDialog) fm.findFragmentByTag(
                        FRAG_TAG_RECUR_PICKER);
                if (rpd != null) {
                    rpd.dismiss();
                }
                rpd = new RecurrencePickerDialog();
                rpd.setArguments(b);
                rpd.setOnRecurrenceSetListener(AddOrEditCourseFragment.this);
                rpd.show(fm, FRAG_TAG_RECUR_PICKER);
				
			}
			
		}); 
		
		this.tvStartTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getChildFragmentManager();
				DateTime now = DateTime.now();
	            RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
	            			.newInstance(AddOrEditCourseFragment.this, now.getHourOfDay(), now.getMinuteOfHour(),
	            					DateFormat.is24HourFormat(rootView.getContext()));
	            timePickerDialog.show(fm, FRAG_TAG_TIME_PICKER_START);
				
			}
			
		});
		
		this.tvEndTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getChildFragmentManager();
				DateTime now = DateTime.now();
	            RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
	            		.newInstance(AddOrEditCourseFragment.this, now.getHourOfDay(), now.getMinuteOfHour(),
	            				DateFormat.is24HourFormat(rootView.getContext()));
	            timePickerDialog.show(fm, FRAG_TAG_TIME_PICKER_END);
			}
			
		});
		
		return rootView;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(boolean isCancel, int eventOrCourseOrGraphic) {
		if (mCallBack != null) {
			mCallBack.onGoBackToMainFragmentInteraction(isCancel, eventOrCourseOrGraphic);
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
		public void onGoBackToMainFragmentInteraction(boolean isCancel, int eventOrCourseOrGraphic);
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
	    	this.starttime = new FixTime(hourOfDay, minute).getFixedTime();
	    	
	    	tvStartTime.setText(starttime);
	    }
	    
	    if(rtpdEnd != null) {
	    	this.endtime = new FixTime(hourOfDay, minute).getFixedTime();
	    	
	    	tvEndTime.setText(endtime);
	    }
	}
	
	@Override
    public void onRecurrenceSet(String rrule) {
        mRrule = rrule;
        if (mRrule != null) {
            mEventRecurrence.parse(mRrule);
        }
        
        this.populateRepeats();
    }
	
	private void populateRepeats() {
        Resources r = getResources();
        String repeatString = "";
        //boolean enabled;
        if (!TextUtils.isEmpty(mRrule)) {
            repeatString = EventRecurrenceFormatter
            					.getRepeatString(getActivity().getBaseContext(), r, mEventRecurrence, true);
        }

//      tvDayOfWeek.setText(mRrule + "\n" + repeatString);
        tvDayOfWeek.setText(repeatString); 
        
        this.dayOfWeek = repeatString;
    }
	
	OnClickListener cancel = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mCallBack.onGoBackToMainFragmentInteraction(true, 2);
		}
		
	};
	

	OnClickListener accept = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			addCourseToDataBase();
			
			Toast.makeText(getActivity().getBaseContext(), "Course Save", Toast.LENGTH_SHORT).show();
			
			mCallBack.onGoBackToMainFragmentInteraction(false, 2);
		}
		
	};
	
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
//				addCourseToDataBase();
//				mCallBack.onNewFragmentInteraction(true);
//				return true;
//			default:
//				return super.onOptionsItemSelected(item);
//		}
//		
//	}
//	
	// input the new course in the database
	public void addCourseToDataBase() {
		
		courseDBHelper = new CourseDatabaseHelper(getActivity().getBaseContext(), "courses", null, 1);
		
		Course course = new Course();
		
		Log.i("COURSE PUSH", this.dayOfWeek);
		Log.i("COURSE PUSH", this.starttime);
		Log.i("COURSE PUSH", this.endtime);
		
		course.setName(this.etCourseName.getText().toString());
		course.setLocation(this.etCourseLocation.getText().toString());
		course.setDayOfWeek(this.dayOfWeek);
		course.setTime(starttime + " - " + endtime);
		
		courseDBHelper.addCourse(course);
	}

}
