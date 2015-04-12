package com.mobicom.ctrlaltdel.app.amain;

import java.util.ArrayList;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mobicom.ctrlaltdel.app.course.AddOrEditCourseFragment;
import com.mobicom.ctrlaltdel.app.course.CoursesMainFragment;
import com.mobicom.ctrlaltdel.app.database.CalendarProviderHelper;
import com.mobicom.ctrlaltdel.app.event.AddOrEditEventFragment;
import com.mobicom.ctrlaltdel.app.event.EventsMainFragment;
import com.mobicom.ctrlaltdel.app.graphicnote.AddGraphicNoteCameraFragment;
import com.mobicom.ctrlaltdel.app.graphicnote.GraphicNoteGalleryFragment;
import com.mobicom.ctrlaltdel.app.graphicnote.GraphicNotesMainFragment;
import com.mobicom.ctrlaltdel.app.model.Event;
import com.mobicom.ctrlaltdel.app.splitview.SplitViewMainFragment;

@SuppressWarnings("deprecation")
public class MainActivity extends FragmentActivity 
		implements EventsMainFragment.OnFragmentInteractionListener,
				   AddOrEditEventFragment.OnFragmentInteractionListener,
				   SplitViewMainFragment.OnFragmentInteractionListener,
				   CoursesMainFragment.OnFragmentInteractionListener,
				   AddOrEditCourseFragment.OnFragmentInteractionListener,
				   GraphicNotesMainFragment.OnFragmentInteractionListener, 
				   AddGraphicNoteCameraFragment.OnFragmentInteractionListener,
				   GraphicNoteGalleryFragment.OnFragmentInteractionListener
{
	private final static String TAG = "DEBUGGING";
	
	private CalendarProviderHelper calendarHelper;
	
	private long calendarID;
	private ArrayList<Long> eventID;
	
	private DrawerLayout drawerLayout;
	private ListView lvForDrawer;
	private ActionBarDrawerToggle drawerToggle;
	
	private CharSequence drawerTitle;
	private CharSequence currentTitle;
	private String[] drawerListTitles;
	
	private ArrayList<Event> allEvents;
	
	private int position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.allEvents = new ArrayList<Event>();
		
		this.calendarHelper = new CalendarProviderHelper();
		this.eventID = new ArrayList<Long>();
		
		// Make a new calendar
		try {
			long existingID = this.calendarHelper.getExistingCalendarId(getBaseContext());
			
			Log.i("EXISTING_ID", "existingID = " + existingID);
			
			if(existingID == 0) {
				this.calendarHelper.makeLocalCalendar(getBaseContext());
				// Calendar Provider
				this.calendarID = this.calendarHelper.getLocalCalendarId(getBaseContext());
				Log.i("CALENDAR_ID", "calendarID = " + this.calendarID);
			} else {
				this.calendarID = existingID;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// initialize the attributes
		currentTitle = drawerTitle = getTitle();
		drawerListTitles = getResources().getStringArray(R.array.array_drawer_list_titles);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		lvForDrawer = (ListView) findViewById(R.id.lv_left_drawer);
				
		// set a custom shadow that overlays the main content when the drawer opens
		this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		this.lvForDrawer.setAdapter(new ArrayAdapter<String>(this,
		        							R.layout.navigation_drawer_list_item, this.drawerListTitles));
		this.lvForDrawer.setOnItemClickListener(new DrawerItemClickListener());
		        
		// enable ActionBar application icon to behave as action to toggle navigation drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		drawerToggle = new ActionBarDrawerToggle
				(
						this,                 	/* host Activity */
						drawerLayout,         	/* DrawerLayout object */ 
						R.drawable.ic_drawer,  	/* navigation drawer image to replace 'Up' caret */
						R.string.drawer_open,  	/* "open drawer" description for accessibility */
						R.string.drawer_close  	/* "close drawer" description for accessibility */
                ) {
        
        	public void onDrawerClosed(View view) {
                getActionBar().setTitle(currentTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        
        drawerLayout.setDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
	}
	
	/* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
		 	// update the main content by replacing fragments
	 
    	Fragment fragment = new Fragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		 	
		switch(position) 
		{
			case 0: fragment = new SplitViewMainFragment();
				break;
			case 1: fragment = new EventsMainFragment(); 
				break;
			case 2: fragment = new CoursesMainFragment();
				break;
			case 3: fragment = new GraphicNotesMainFragment();
				break;
		}
		
		Bundle bundle = new Bundle();
		this.allEvents = this.calendarHelper.getAllEvents(getBaseContext(), this.calendarID);
		Log.i(TAG, "event size = " + allEvents.size());
		
		if(allEvents.size() > 0) {
			bundle.putBoolean("hasContent", true);
			bundle.putParcelableArrayList("allevents", allEvents);
			Log.i(TAG, "event title = " + allEvents.get(0).getTitle());
		} else
			bundle.putBoolean("hasContent", false);
		fragment.setArguments(bundle);
	      
	    ft.replace(R.id.main_fragment_container, fragment);
	    ft.commit();
	    
	    // update selected item and title, then close the drawer
        this.lvForDrawer.setItemChecked(position, true);
        this.setTitle(this.drawerListTitles[position]);
        drawerLayout.closeDrawer(this.lvForDrawer);
	}
    
    @Override
    public void setTitle(CharSequence title) {
        this.currentTitle = title;
        getActionBar().setTitle(currentTitle);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    /* Called whenever we call invalidateOptionsMenu() */
    @SuppressWarnings("unused")
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the navigation drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(lvForDrawer);
       
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    
    private void goToEventsMainFragment(boolean isEdit, long editedEventID) {
		 
    	Fragment fragment = new EventsMainFragment();
		Bundle bundle = new Bundle();	
		 
		this.allEvents = this.calendarHelper.getAllEvents(getBaseContext(), this.calendarID);
		Log.i(TAG, "event size = " + allEvents.size());
		 
		if(allEvents.size() > 0)
			bundle.putBoolean("hasContent", true);
		else
			bundle.putBoolean("hasContent", false);
		 
		if(isEdit = true) 
		{
			for(int i = 0; i < allEvents.size(); i++)
			{
				if(editedEventID == allEvents.get(i).getID()) {
					allEvents.get(i).setIsSplit(false);
					allEvents.get(i).setIsAlreadyChecked(false);
					break;
				}
			}
		}
				 
		bundle.putParcelableArrayList("allevents", allEvents);
		fragment.setArguments(bundle);
	        
	    FragmentManager fragmentManager = getSupportFragmentManager();
	    FragmentTransaction ft = fragmentManager.beginTransaction();
	    ft.replace(R.id.main_fragment_container, fragment);
	    ft.commit();
	 }
	 
	 public void fabAddEvent(View view) {
			
		 Toast.makeText(view.getContext(), "Add New Event", Toast.LENGTH_SHORT)
	     		.show();
	        
		 Fragment fragment = new AddOrEditEventFragment();
		 
		 Bundle bundle = new Bundle();
		 bundle.putBoolean("edit", false);
		 fragment.setArguments(bundle);
	        
		 FragmentManager fm = getSupportFragmentManager();
	     FragmentTransaction ft = fm.beginTransaction();
	     ft.replace(R.id.main_fragment_container, fragment);
	     ft.commit();
	 }
	 
//	 public void fabAddCourse(View view) {
//			
//	        Toast.makeText(view.getContext(), "Add New Course", Toast.LENGTH_SHORT)
//	        		.show();
//	        
//	        Fragment fragment = new AddOrEditCourseFragment();
//	        
//	        FragmentManager fm = getSupportFragmentManager();
//	        FragmentTransaction ft = fm.beginTransaction();
//	        ft.replace(R.id.main_fragment_container, fragment);
//	        ft.commit();
//	}

	 @Override
	 public void onGoBackToMainFragmentInteraction(boolean isCancel, int eventOrCourseOrGraphic) {
		 // TODO Auto-generated method stub
		 
		 this.selectItem(this.position);
		
		 if(isCancel == true && eventOrCourseOrGraphic == 1)
			 Toast.makeText(getBaseContext(), "Event Cancel", Toast.LENGTH_SHORT).show();
		 else if(isCancel == true && eventOrCourseOrGraphic == 2)
			 Toast.makeText(getBaseContext(), "Course Cancel", Toast.LENGTH_SHORT).show();
		 else if(isCancel == true && eventOrCourseOrGraphic == 3)
			 Toast.makeText(getBaseContext(), "Graphic Note Cancel", Toast.LENGTH_SHORT).show();
		 else if(isCancel == false && eventOrCourseOrGraphic == 3)
			 Toast.makeText(getBaseContext(), "Graphic Notes", Toast.LENGTH_SHORT).show();
	 }

	@Override
	public void onAddOrEditEventSaveToDataBase(Event event, boolean isEdit) {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "Event Save", Toast.LENGTH_SHORT).show();
		
		long editedEventID = 0;
		
		if(isEdit == true)
			editedEventID = this.calendarHelper.updateEvent(getBaseContext(), event);
		else
			this.eventID.add(this.calendarHelper.insertEvent(getBaseContext(), this.calendarID, event));
		
		this.goToEventsMainFragment(isEdit, editedEventID);
	}

	@Override
	public void onEditEventFragmentInteraction(Event editEvent) {
		// TODO Auto-generated method stub
		Log.i("DEBUG_2", "title = " + editEvent.getTitle());

		Fragment fragment = new AddOrEditEventFragment();
		 
		Bundle bundle = new Bundle();
		bundle.putBoolean("edit", true);
		bundle.putParcelable("editevent", editEvent);
		fragment.setArguments(bundle);
	        
		FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction ft = fm.beginTransaction();
	    ft.replace(R.id.main_fragment_container, fragment);
	    ft.commit();
	}

	@Override
	public void onDeleteEventFragmentInteraction(Event deleteEvent) {
		// TODO Auto-generated method stub
		this.calendarHelper.deleteEvent(getBaseContext(), deleteEvent);
		this.goToEventsMainFragment(false, 0);
	}

	@Override
	public void toFindMainFragmentInteraction(int position) {
		// TODO Auto-generated method stub
		
		this.position = position;
		
		Log.i("POSITION","current_position = " + this.position);
	}

	@Override
	public void onCameraMainFragmentInteraction(boolean uri) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGoToGraphicNoteCameraFragmentInteraction() {
		// TODO Auto-generated method stub
		
		Toast.makeText(getBaseContext(), "Graphic Notes Camera", Toast.LENGTH_SHORT).show();
    
		Fragment fragment = new AddGraphicNoteCameraFragment();
    
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.main_fragment_container, fragment);
		ft.commit();
	}

	@Override
	public void onGoToNoteGalleryFragmentInteraction() {
		// TODO Auto-generated method stub
		Fragment fragment = new GraphicNoteGalleryFragment();	
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.main_fragment_container, fragment);
		ft.commit();
	}

	@Override
	public void onGoToAddOrEditCourseFragmentInteraction() {
		// TODO Auto-generated method stub
		
		Toast.makeText(getBaseContext(), "Add New Course", Toast.LENGTH_SHORT)
    		.show();
    
		Fragment fragment = new AddOrEditCourseFragment();
    
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.main_fragment_container, fragment);
		ft.commit();
	}

}
