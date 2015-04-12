package com.mobicom.ctrlaltdel.app.splitview;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;
import com.mobicom.ctrlaltdel.app.amain.R;
import com.mobicom.ctrlaltdel.app.model.Event;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.roomorama.caldroid.WeekdayArrayAdapter;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link SplitViewMainFragment.OnFragmentInteractionListener}
 * interface to handle interaction events.
 *
 */
public class SplitViewMainFragment extends Fragment {

	private OnFragmentInteractionListener mCallBack;
	
	// added attributes
	private CaldroidFragment caldroidFragment;
	
	private ArrayList<Event> allEvents;
	private ArrayList<Event> storage;
	private boolean hasContent;
	
	private TextView tvMiniScheduleMonthDay;
	private TextView tvMiniScheduleDayOfWeek;
	private ListView lvMiniScheduleEvents;
	
	int month;
	int day;
	int year;
	
	int dateID;
	
	private FloatingActionButton fabAddEvent;
	
//	private String forMiniScheduleMonthDay;
//	private String forMiniScheduleDayOfWeek;

	public SplitViewMainFragment() {
		// Required empty public constructor
	}
	
	private void setCustomResourceForDates() {
//		Calendar cal = Calendar.getInstance();
//
//		// Min date is last 7 days
//		cal.add(Calendar.DATE, -18);
//		Date blueDate = cal.getTime();
//
//		// Max date is next 7 days
//		cal = Calendar.getInstance();
//		cal.add(Calendar.DATE, 16);
//		Date greenDate = cal.getTime();
//
//		if (caldroidFragment != null) {
//			caldroidFragment.setBackgroundResourceForDate(R.color.blue,
//					blueDate);
//			caldroidFragment.setBackgroundResourceForDate(R.color.green,
//					greenDate);
//			caldroidFragment.setTextColorForDate(R.color.white, blueDate);
//			caldroidFragment.setTextColorForDate(R.color.white, greenDate);
//			
//		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		getActivity().getActionBar().show();
		
		// Set up Caldroid
		//caldroidFragment = new CaldroidFragment();
				
		caldroidFragment = new CaldroidSampleCustomFragment();
//		Fragment fragment02 = new SplitViewSecondFragment();
				
		Bundle args = new Bundle();
		Calendar calendar = Calendar.getInstance();
		args.putInt(CaldroidFragment.MONTH, calendar.get(Calendar.MONTH) + 1);
		args.putInt(CaldroidFragment.YEAR, calendar.get(Calendar.YEAR));
		args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
		args.putBoolean(CaldroidFragment.SHOW_NAVIGATION_ARROWS, false);
		args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
				
				
		// Uncomment this to customize startDayOfWeek
		// args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
		// CaldroidFragment.TUESDAY); // Tuesday
		caldroidFragment.setArguments(args);

		// Attach to the activity
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.calendar_grid, caldroidFragment);
//		ft.add(R.id.calendar_schedule, fragment02);
		ft.commit();
						
		setCustomResourceForDates();
				
				
		// attach listener Caldroid
		caldroidFragment.setCaldroidListener(listener);
		
		this.allEvents = new ArrayList<Event>();
		
		Bundle bundle = getArguments();
		this.hasContent = bundle.getBoolean("hasContent");
		
		Log.i("EVENTS_MAIN", "hasContent = " + this.hasContent);
		
		if(hasContent == true) 
		{
			this.allEvents = bundle.getParcelableArrayList("allevents");
		}
		
		mCallBack.toFindMainFragmentInteraction(0);
	}
	
	CaldroidListener listener = new CaldroidListener() 
	{
		final SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy", Locale.US);
		final SimpleDateFormat monthdayFormatter = new SimpleDateFormat("MMMM d", Locale.US);
		final SimpleDateFormat dayOfWeekFormatter = new SimpleDateFormat("EEEE", Locale.US);
		
		@Override
		public void onSelectDate(Date date, View view) {
//			Toast.makeText(getApplicationContext(), formatter.format(date),
//						   Toast.LENGTH_SHORT).show();
			tvMiniScheduleMonthDay.setText(monthdayFormatter.format(date));
			tvMiniScheduleDayOfWeek.setText(dayOfWeekFormatter.format(date));
			
			
			StringTokenizer st = new StringTokenizer(formatter.format(date).toString());
			
			int[] x = new int[3];
			int index = 0;
			while(st.hasMoreTokens())
			{
				x[index] = Integer.parseInt(st.nextToken());
				index++;
			}
			
			day = x[0];
			month = x[1];
			year = x[2];
			
			Log.i("SPLITVIEW", "month = " + month + ", day = " + day + " ,year = " + year);
			
			String str = "" + x[2] + x[1] + x[0];
			dateID = Integer.parseInt(str);
			
			try {
				storage = new ArrayList<Event>();
				for(int i = 0; i < allEvents.size(); i++)
				{
					Log.i("SPLITVIEW", "dateID = " + dateID);
					if(dateID == allEvents.get(i).getStartDateTime().getForComparingDate()) {
						storage.add(allEvents.get(i));
					}
				}
			} catch(IndexOutOfBoundsException e) {
				
			}
			
//			forMiniScheduleMonthDay = "" + monthdayFormatter.format(date);
//			forMiniScheduleDayOfWeek = "" + dayOfWeekFormatter.format(date);
			
			caldroidFragment.setSelectedDates(date, date);
			caldroidFragment.refreshView();
		}

		@Override
		public void onChangeMonth(int month, int year) {
//			String text = "month: " + month + " year: " + year;
//			Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onLongClickDate(Date date, View view) {
			Toast.makeText(view.getContext(),
					"Long click " + formatter.format(date),
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCaldroidViewCreated() {
			WeekdayArrayAdapter.textColor = Color.WHITE;
			Calendar today = Calendar.getInstance();
			onSelectDate(today.getTime(), null);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView =  inflater.inflate(R.layout.fragment_main_split_view, container, false);
		
		this.tvMiniScheduleMonthDay = (TextView) rootView.findViewById(R.id.tv_mini_schedule_monthday);
		this.tvMiniScheduleDayOfWeek = (TextView) rootView.findViewById(R.id.tv_mini_schedule_dayOfWeek);
		
		this.lvMiniScheduleEvents = (ListView) rootView.findViewById(R.id.lv_mini_schedule_events);
		
		this.fabAddEvent = (FloatingActionButton) rootView.findViewById(R.id.fab_addevent);
		this.fabAddEvent.setColor(rootView.getResources().getColor(R.color.holo_green_light));
		this.fabAddEvent.listenTo(lvMiniScheduleEvents);
		
		try {
//			ArrayList<Event> storage = new ArrayList<Event>();
//			for(int i = 0; i < allEvents.size(); i++)
//			{
//				Log.i("SPLITVIEW", "dateID = " + dateID);
//				if(dateID == allEvents.get(i).getStartDateTime().getForComparingDate()) {
//					storage.add(allEvents.get(i));
//				}
//			}
			MiniScheduleAdapter adapter = new MiniScheduleAdapter(getActivity().getBaseContext(), R.layout.split_view_list_item, storage);
			this.lvMiniScheduleEvents.setAdapter(adapter);
		} catch(IndexOutOfBoundsException e) {
			
		}
		
		return rootView;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(int position) {
		if (mCallBack != null) {
			mCallBack.toFindMainFragmentInteraction(position);
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
		public void toFindMainFragmentInteraction(int position);
	}
}
