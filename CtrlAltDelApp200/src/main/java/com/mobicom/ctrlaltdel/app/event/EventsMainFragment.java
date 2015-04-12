package com.mobicom.ctrlaltdel.app.event;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;
import com.mobicom.ctrlaltdel.app.amain.R;
import com.mobicom.ctrlaltdel.app.model.Event;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link EventsMainFragment.OnFragmentInteractionListener}
 * interface to handle interaction events.
 *
 */
public class EventsMainFragment extends Fragment
		implements AdapterView.OnItemClickListener, 
				   AdapterView.OnItemLongClickListener,
        		   StickyListHeadersListView.OnHeaderClickListener,
        		   StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        		   StickyListHeadersListView.OnStickyHeaderChangedListener {

	private OnFragmentInteractionListener mCallBack;
		
	private StickyListHeadersListView stickyEventList;
	private SwipeRefreshLayout refreshLayout;
	private EventBaseAdapter stickAdapter;
	
	private FloatingActionButton fabAddNewEvent;
	
	private boolean hasContent;
	
	private ArrayList<Event> allEvents;
	
	public EventsMainFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		getActivity().getActionBar().show();
		
		mCallBack.toFindMainFragmentInteraction(1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_main_event, container, false);
		
		this.refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        this.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        
        this.allEvents = new ArrayList<Event>();
		
		Bundle bundle = getArguments();
		
		this.hasContent = bundle.getBoolean("hasContent");
		
		Log.i("EVENTS_MAIN", "hasContent = " + this.hasContent);
		
		if(hasContent == true) 
		{
			this.allEvents = bundle.getParcelableArrayList("allevents");
		}
        
		 
        this.stickyEventList = (StickyListHeadersListView) rootView.findViewById(R.id.stickylist_events);	
    	this.stickyEventList.addHeaderView(inflater.inflate(R.layout.event_list_header, null));
    	this.stickyEventList.addFooterView(inflater.inflate(R.layout.event_list_footer, null));
    	this.stickyEventList.setDrawingListUnderStickyHeader(true);
    	this.stickyEventList.setAreHeadersSticky(true);
    		
    	try {
    		Log.i("EVENTS_MAIN", "title = " + this.allEvents.get(0).getTitle());
    		this.stickAdapter = new EventBaseAdapter(getActivity().getBaseContext(), this.allEvents, this.hasContent);
    		this.stickyEventList.setAdapter(stickAdapter);
    	}catch(IndexOutOfBoundsException e) {
    		this.stickyEventList.setEmptyView(rootView.findViewById(R.id.empty));
    	}
    	
    	this.stickyEventList.setOnItemClickListener(this);
    	this.stickyEventList.setOnItemLongClickListener(this);
    	this.stickyEventList.setOnHeaderClickListener(this);
    	this.stickyEventList.setOnStickyHeaderChangedListener(this);
    	this.stickyEventList.setOnStickyHeaderOffsetChangedListener(this);
    	
    	this.fabAddNewEvent = (FloatingActionButton) rootView.findViewById(R.id.fab_addevent);
        this.fabAddNewEvent.setColor(rootView.getResources().getColor(R.color.material_design_red_500));
		
		return rootView;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(int position, Event editEvent, Event deleteEvent) {
		if (mCallBack != null) {
			mCallBack.toFindMainFragmentInteraction(position);
			mCallBack.onEditEventFragmentInteraction(editEvent);
			mCallBack.onDeleteEventFragmentInteraction(deleteEvent);
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
		
		public void onEditEventFragmentInteraction(Event editEvent);
		
		public void onDeleteEventFragmentInteraction(Event deleteEvent);
	}

	@Override
	public void onStickyHeaderChanged(StickyListHeadersListView l, View header,
			int itemPosition, long headerId) {
		// TODO Auto-generated method stub
		header.setAlpha(1);
	}

	@Override
	public void onStickyHeaderOffsetChanged(StickyListHeadersListView l,
			View header, int offset) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
		{
			header.setAlpha(1 - (offset / (float) header.getMeasuredHeight()));
	    }
	}

	@Override
	public void onHeaderClick(StickyListHeadersListView l, View header,
			int itemPosition, long headerId, boolean currentlySticky) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity().getBaseContext(), "Header " + headerId + " currentlySticky ? " + currentlySticky, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		try {
			Event storage = (Event) this.stickAdapter.getItem(position-1);
			Event currentItem = new Event();
		
			for(int i = 0; i < this.allEvents.size(); i++)
			{
				if(storage.getID() == allEvents.get(i).getID()) 
				{
					currentItem = allEvents.get(i);
					break;
				}
			}
			
			Toast.makeText(getActivity().getBaseContext(), "Edit " + currentItem.getTitle() + " Event", Toast.LENGTH_SHORT).show();
			
			Log.i("DEBUG_EDIT", "title = " + currentItem.getTitle());
			mCallBack.onEditEventFragmentInteraction(currentItem);
		} catch(IndexOutOfBoundsException e) {
			Toast.makeText(getActivity().getBaseContext(), "CtrlAltDel Calendar", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		try {
			Event storage = (Event) this.stickAdapter.getItem(position-1);
			Event currentItem = new Event();
		
			for(int i = 0; i < this.allEvents.size(); i++)
			{
				if(storage.getID() == allEvents.get(i).getID()) 
				{
					currentItem = allEvents.get(i);
					break;
				}
			}
		
			Toast.makeText(getActivity().getBaseContext(), "Delete " + currentItem.getTitle() + " Event", Toast.LENGTH_SHORT).show();
			
			Log.i("DEBUG_DELETE", "title = " + currentItem.getTitle());
			mCallBack.onDeleteEventFragmentInteraction(currentItem);
		} catch(IndexOutOfBoundsException e) {
			Toast.makeText(getActivity().getBaseContext(), "CtrlAltDel Calendar", Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}

}
