package com.mobicom.ctrlaltdel.app.database;

import java.util.ArrayList;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.provider.CalendarContract.Reminders;
import android.util.Log;

import com.mobicom.ctrlaltdel.app.model.Event;

public class CalendarProviderHelper 
{
	// Projection array. Creating indices for this array instead of doing
	// dynamic lookups improves performance.
	public static final String[] EVENT_PROJECTION = new String[] 
	{
		Calendars._ID, // 0
		Calendars.ACCOUNT_NAME, // 1
		Calendars.CALENDAR_DISPLAY_NAME, // 2
		Calendars.OWNER_ACCOUNT // 3
	};
	
	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
	
	private static final String MY_ACCOUNT_NAME = "TheXilent13";
	private static final String MY_CALENDAR_NAME = "Ctrl Alt Del";

	// email id for local calendar
	private static final String MY_EMAIL = "ctrlaltdel@mobicom.com";
	
	// TODO - Change this email id to your email id
	//email id for gmail calendar
	private static final String EMAIL_ID = "2014.cltraltdel@gmail.com";
	
	/*
	 * Create a local calendar in android device
	 */
	public void makeLocalCalendar(Context context) 
	{
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		
		values.put(Calendars.ACCOUNT_NAME, MY_ACCOUNT_NAME);
		values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
		values.put(Calendars.NAME, MY_CALENDAR_NAME);
		values.put(Calendars.CALENDAR_DISPLAY_NAME, MY_CALENDAR_NAME);
		values.put(Calendars.CALENDAR_COLOR, Color.CYAN);
		values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		values.put(Calendars.OWNER_ACCOUNT, MY_EMAIL);
		values.put(Calendars.CALENDAR_TIME_ZONE, TimeZone.getAvailableIDs()
				.toString());
		values.put(Calendars.SYNC_EVENTS, 1);
		
		resolver.insert(CalendarProviderHelper.buildCalendarUri(), values);
	}
	
	/*
	 * Function to insert a calendar event 
	 */
	public long insertEvent(Context context, long calendarID, Event event) 
	{
		long eventID = 0;

		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		
		values.put(Events.CALENDAR_ID, calendarID);
		values.put(Events.TITLE, event.getTitle());
		values.put(Events.EVENT_LOCATION, event.getLocation());
		values.put(Events.ALL_DAY, event.getIsAllDay());
		values.put(Events.DTSTART, event.getStartMillis());
		values.put(Events.DTEND, event.getEndMillis());
		values.put(Events.DESCRIPTION, event.getDescription());
		values.put(Events.RRULE, event.getRrule());
		values.put(Events.EVENT_TIMEZONE, "Philippines");
		
		Uri uri = resolver.insert(CalendarProviderHelper.buildEventUri(), values);
		eventID = Long.parseLong(uri.getLastPathSegment());
		
		return eventID;
	}
	
	public void insertReminderToAnEvent(Context context, Event event) {
		
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		
		values.put(Reminders.EVENT_ID, event.getID());
		values.put(Reminders.MINUTES, event.getReminderTime());
		values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
		
		resolver.insert(Reminders.CONTENT_URI, values);
	}
	
	public ArrayList<Event> getAllEvents(Context context, long calendarID) 
	{
		ArrayList<Event> allEvents = new ArrayList<Event>();
		
		final String[] PROJECTION = new String[] 
				{ 
					Instances.CALENDAR_ID, // 0
					Events._ID,   // 1
					Events.TITLE, // 2
					Events.EVENT_LOCATION, // 3
					Events.DESCRIPTION, // 4
					Events.ALL_DAY, // 5
					Events.DTSTART, // 6
					Events.DTEND // 7
                };
		
		final int CALENDAR_ID = 0;
		final int EVENT_ID = 1;
		final int EVENT_TITLE = 2;
		final int EVENT_LOCATION = 3;
		final int EVENT_DESCRIPTION = 4;
		final int EVENT_ALLDAY = 5;
		final int EVENT_STARTMILLIS = 6;
		final int EVENT_ENDMILLIS = 7;
				
		ContentResolver cr = context.getContentResolver();
		String selection = Instances.CALENDAR_ID + " = ?";
		String[] selectionArgs = new String[] {"" + calendarID};
		Cursor cursor = cr.query( Uri.parse("content://com.android.calendar/events"),
                PROJECTION,
                selection,
                selectionArgs,
                null
        );
		
		while(cursor.moveToNext()) 
		{
			long calendarid = cursor.getLong(CALENDAR_ID);
			long eventID = cursor.getLong(EVENT_ID);
			String title = cursor.getString(EVENT_TITLE);
			String location = cursor.getString(EVENT_LOCATION);
			String description = cursor.getString(EVENT_DESCRIPTION);
			String allDay = cursor.getString(EVENT_ALLDAY);
			long startMillis = cursor.getLong(EVENT_STARTMILLIS);
			long endMillis = cursor.getLong(EVENT_ENDMILLIS);
			
			
			boolean allday = Boolean.parseBoolean(allDay);
			
			Log.i("LOL1", "allday = " + allday);
			
			if(startMillis == endMillis)
				allday = true;
			else
				allday = false;
			
			Log.i("LOL2", "allday = " + allday);
			
			Event event = new Event();
			
			event.setCalendarID(calendarid);
			event.setID(eventID);
			event.setTitle(title);
			event.setLocation(location);
			event.setDescription(description);
			event.setIsAllDay(allday);
			event.setStartMillis(startMillis);
			event.setEndMillis(endMillis);
			event.setIsOriginal(true);
			
			allEvents.add(event);
		}
		
		return allEvents;
	}
	
	/*
	 * Returns a list of instances of an event with eventID
	 */
	public ArrayList<String> getEventByID(Context context, long eventID,
			long startMillis, long endMillis) 
	{
		ArrayList<String> eventList = new ArrayList<String>();

		final String[] INSTANCE_PROJECTION = new String[] 
		{ 
				Instances.EVENT_ID, // 0
				Instances.BEGIN, // 1
				Instances.TITLE // 2
		};

		// The indices for the projection array above.
		final int PROJECTION_ID_INDEX = 0;
		final int PROJECTION_BEGIN_INDEX = 1;
		final int PROJECTION_TITLE_INDEX = 2;

		Cursor cursor = null;
		ContentResolver cr = context.getContentResolver();

		// The ID of the recurring event whose instances you are searching
		// for in the Instances table
		
		String selection = Instances.EVENT_ID + " = ?";
		String[] selectionArgs = new String[] { "" + eventID };

		// Construct the query with the desired date range.
		Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(builder, startMillis);
		ContentUris.appendId(builder, endMillis);

		// Submit the query
		cursor = cr.query(builder.build(), INSTANCE_PROJECTION, selection,
				selectionArgs, null);

		while (cursor.moveToNext()) 
		{
			String title = null;

			// Get the field values
			title = cursor.getString(PROJECTION_TITLE_INDEX);

			eventList.add(title);
		}

		return eventList;
	}
	
	/*
	 * Returns a list of instances of an event with eventID
	 */
	public ArrayList<String> getRecurringEvents(Context context, long eventID,
			long startMillis, long endMillis) {

		ArrayList<String> eventList = new ArrayList<String>();

		final String[] INSTANCE_PROJECTION = new String[] { Instances.EVENT_ID, // 0
				Instances.BEGIN, // 1
				Instances.TITLE // 2
		};

		// The indices for the projection array above.
		final int PROJECTION_ID_INDEX = 0;
		final int PROJECTION_BEGIN_INDEX = 1;
		final int PROJECTION_TITLE_INDEX = 2;

		Cursor cursor = null;
		ContentResolver cr = context.getContentResolver();

		// The ID of the recurring event whose instances you are searching
		// for in the Instances table
		
		String selection = Instances.EVENT_ID + " = ?";
		String[] selectionArgs = new String[] { "" + eventID };

		// Construct the query with the desired date range.
		Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(builder, startMillis);
		ContentUris.appendId(builder, endMillis);

		// Submit the query
		cursor = cr.query(builder.build(), INSTANCE_PROJECTION, selection,
				selectionArgs, null);

		while (cursor.moveToNext()) {
			String title = null;

			// Get the field values
			title = cursor.getString(PROJECTION_TITLE_INDEX);

			eventList.add(title);
		}

		return eventList;
	}

	/*
	 * Function to insert a recurring calendar event 
	 */
	public long insertRecurringEvent(Context context, long calendarID, Event event) 
	{
		long eventID = 0;

		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Events.CALENDAR_ID, calendarID);
		values.put(Events.TITLE, event.getTitle());
		values.put(Events.DTSTART, event.getStartMillis());
		values.put(Events.DTEND, event.getEndMillis());
		values.put(Events.RRULE, event.getRrule());
		values.put(Events.DESCRIPTION, event.getDescription());
		values.put(Events.EVENT_TIMEZONE, "Philippines");
		
		Uri uri = resolver.insert(CalendarProviderHelper.buildEventUri(), values);

		eventID = Long.parseLong(uri.getLastPathSegment());
		
		return eventID;
	}

	/*
	 * This function reads the android device for existing calendars in android
	 * and returns its id
	 */
	public long getExistingCalendarId(Context context) 
	{
		long calID = 0;

		Cursor cursor = null;
		ContentResolver cr = context.getContentResolver();
		Uri uri = Calendars.CONTENT_URI;
		String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
				+ Calendars.ACCOUNT_TYPE + " = ?) AND ("
				+ Calendars.OWNER_ACCOUNT + " = ?))";
		String[] selectionArgs = new String[] 
				{ 
					MY_ACCOUNT_NAME,
					CalendarContract.ACCOUNT_TYPE_LOCAL, 
					MY_EMAIL 
				};
		// Submit the query and get a Cursor object back.
		cursor = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

		// Use the cursor to step through the returned records
		while (cursor.moveToNext()) 
		{
			calID = 0;

			String displayName = null;
			String accountName = null;
			String ownerName = null;

			// Get the field values
			calID = cursor.getLong(PROJECTION_ID_INDEX);
			displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
			accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
			ownerName = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
		}
		
		cursor.close();
		
		return calID;
	}

	/*
	 * This function returns a local calendar ID that was created earlier by
	 * this app
	 */
	public long getLocalCalendarId(Context context) 
	{
		String[] projection = new String[] { Calendars._ID };

		String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
				+ Calendars.ACCOUNT_TYPE + " = ?))";

		// use the same values as above:

		String[] selArgs = new String[] { MY_ACCOUNT_NAME,
				CalendarContract.ACCOUNT_TYPE_LOCAL };

		Cursor cursor = context.getContentResolver().query(Calendars.CONTENT_URI,
				projection, selection, selArgs, null);

		if (cursor.moveToFirst()) {

			return cursor.getLong(0);

		} else {
			System.out.println("Cursor did not return any values");
		}
		
		cursor.close();

		return -1;
	}
	
	public long updateEvent(Context context, Event event) {
		
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		// The new title for the event
		values.put(Events.TITLE, event.getTitle());
		values.put(Events.EVENT_LOCATION, event.getLocation());
		values.put(Events.ALL_DAY, event.getIsAllDay());
		values.put(Events.DTSTART, event.getStartMillis());
		values.put(Events.DTEND, event.getEndMillis());
		values.put(Events.DESCRIPTION, event.getDescription());
		values.put(Events.EVENT_TIMEZONE, "Philippines");
		
		Uri updateUri = null;
		updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, event.getID());
		int rows = resolver.update(updateUri, values, null, null);
		
		Log.i("UPDATE_EVENT", "Rows updated: " + rows);  
		
		return event.getID();
	}
	
	public void deleteEvent(Context context, Event event) {
		ContentResolver resolver = context.getContentResolver();
		
		Uri deleteUri = null;
		deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, event.getID());
		int rows = resolver.delete(deleteUri, null, null);
		
		Log.i("DELETE_EVENT", "Rows deleted: " + rows); 
	}
	
	/**Builds the Uri for your Calendar in android database (as a Sync Adapter)*/
	private static Uri buildCalendarUri() 
	{
		Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI
				.buildUpon();
		
		return builder
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
				.appendQueryParameter(Calendars.ACCOUNT_NAME, "com.mobicom.calendarlocal")
				.appendQueryParameter(Calendars.ACCOUNT_TYPE,
						CalendarContract.ACCOUNT_TYPE_LOCAL)
				.build();
	}
	
	/**Builds the Uri for events (as a Sync Adapter)*/
	private static Uri buildEventUri() 
	{
		Uri.Builder builder = CalendarContract.Events.CONTENT_URI
				.buildUpon();
		
		return builder
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
				.appendQueryParameter(Calendars.ACCOUNT_NAME, "com.mobicom.calendarlocal")
				.appendQueryParameter(Calendars.ACCOUNT_TYPE,
						CalendarContract.ACCOUNT_TYPE_LOCAL)
				.build();
	}
	
}
