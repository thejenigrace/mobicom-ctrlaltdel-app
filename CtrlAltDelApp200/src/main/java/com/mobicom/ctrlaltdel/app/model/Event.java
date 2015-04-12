package com.mobicom.ctrlaltdel.app.model;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Event implements Parcelable {
	
	private long calendarID;
	private long ID;
	private String title;
	private String location;
	private String description;
	private boolean isAllDay;
	private long startMillis;
	private long endMillis;
	private String rrule;
	private int reminderTime;
	
	private NormalDateTime startDateTime;
	private NormalDateTime endDateTime;
	
	private boolean isOriginal;
	private boolean isSubevent;
	private boolean isAlreadyChecked;
	
	private long previousStartMillis;
	private NormalDateTime previousDateTime;
	
	public Event() {
		this.startDateTime = new NormalDateTime();
		this.endDateTime = new NormalDateTime();
		this.previousDateTime = new NormalDateTime();
		this.isOriginal = false;
		this.isSubevent = false;
		this.isAlreadyChecked = false;
	}
	
	public Event(Parcel source) {
		
		this.readFromParcel(source);
	}
	
	public long getCalendarID() {
		return calendarID;
	}
	
	public void setCalendarID(long calendarID) {
		this.calendarID = calendarID;
	}
	
	public long getID() {
		return ID;
	}
	
	public void setID(long ID) {
		this.ID = ID;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean getIsAllDay() {
		return isAllDay;
	}
	
	public void setIsAllDay(boolean isAllDay) {
		this.isAllDay = isAllDay;
	}
	
	public long getStartMillis() {
		return startMillis;
	}

	public void setStartMillis(long startMillis) {
		this.startMillis = startMillis;
		
		this.convertStartMillis(startMillis);
	}

	public long getEndMillis() {
		return endMillis;
	}

	public void setEndMillis(long endMillis) {
		this.endMillis = endMillis;
		
		this.convertEndMillis(endMillis);
	}
	
	public void convertStartMillis(long startMillis) {
		
		DateTime normal = new DateTime(startMillis);
		
		this.startDateTime.setMonth(normal.getMonthOfYear());
		this.startDateTime.setDay(normal.getDayOfMonth());
		this.startDateTime.setYear(normal.getYear());
		this.startDateTime.setHour(normal.getHourOfDay());
		this.startDateTime.setMinute(normal.getMinuteOfHour());
	}
	
	public void convertEndMillis(long endMillis) {
		
		DateTime normal = new DateTime(endMillis);
		
		this.endDateTime.setMonth(normal.getMonthOfYear());
		this.endDateTime.setDay(normal.getDayOfMonth());
		this.endDateTime.setYear(normal.getYear());
		this.endDateTime.setHour(normal.getHourOfDay());
		this.endDateTime.setMinute(normal.getMinuteOfHour());
	}
	
	public long getPreviousStartMillis() {
		return this.previousStartMillis;
	}
	
	public void setPreviousStartMillis(long previousStartMillis) {
		this.previousStartMillis = previousStartMillis;
		
		this.convertPreviousStartMillis(previousStartMillis);
	}
		
	public void convertPreviousStartMillis(long previousStartMillis) {
		
		DateTime normal = new DateTime(previousStartMillis);
		
		this.previousDateTime.setMonth(normal.getMonthOfYear());
		this.previousDateTime.setDay(normal.getDayOfMonth());
		this.previousDateTime.setYear(normal.getYear());
		this.previousDateTime.setHour(normal.getHourOfDay());
		this.previousDateTime.setMinute(normal.getMinuteOfHour());
	}
	
	public String getRrule() {
		return rrule;
	}

	public void setRrule(String rrule) {
		this.rrule = rrule;
	}
	
	public int getReminderTime() {
		return reminderTime;
	}

	public void setReminderTime(int reminderTime) {
		this.reminderTime = reminderTime;
	}
	
	public NormalDateTime getStartDateTime() {
		return this.startDateTime;
	}
	
	public NormalDateTime getEndDateTime() {
		return this.endDateTime;
	}
	
	public NormalDateTime getPreviousDateTime() {
		return this.previousDateTime;
	}
	
	public int howManyDaysIsTheEvent() {
		
		DateTime start = new DateTime(this.startMillis);
		DateTime end = new DateTime(this.endMillis);
		
		Days days =  Days.daysBetween(start, end);
		Hours hours = Hours.hoursBetween(start, end);
		
		int howMany = days.getDays();
		
		Log.i("ORIG", "days = "  + days.getDays());
		
		if(howMany > 0) {
			
			if(howMany > 1 && hours.getHours() > 0 && 
					this.startDateTime.getForComparingTime() != this.endDateTime.getForComparingTime())
				howMany++;
			else 
				howMany = days.getDays();
		}
		
		return howMany;
	}
	
	public boolean isMoreThanOneDay() {
		
		if(this.howManyDaysIsTheEvent() > 0)
			return true;
		else
			return false;
	}
	
	public boolean getIsOriginal() {
		return isOriginal;
	}

	public void setIsOriginal(boolean original) {
		this.isOriginal = original;
	}
	
	public boolean getIsSplit() {
		
		return this.isSubevent;
	}
	
	public void setIsSplit(boolean result) {
		
		this.isSubevent = result;
	}
	
	public boolean getIsAlreadyChecked() {
		
		return this.isAlreadyChecked;
	}
	
	public void setIsAlreadyChecked(boolean result) {
		
		this.isAlreadyChecked = result;
	}
	
	
	/*
	 * For the Parcelable Interface
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeLong(calendarID);
		dest.writeLong(ID);
		dest.writeString(title);
		dest.writeString(location);
		dest.writeString(description);
		dest.writeLong(startMillis);
		dest.writeLong(endMillis);
	}
	
	public void readFromParcel(Parcel source) {	
		this.calendarID = source.readLong();
		this.ID = source.readLong();
		this.title = source.readString();
		this.location = source.readString();
		this.description = source.readString();
		this.startMillis = source.readLong();
		this.endMillis = source.readLong();
	}
	
	public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() 
	{
		@Override
		public Event createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Event(source) ;
		}

		@Override
		public Event[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Event[size];
		}
	};
}
