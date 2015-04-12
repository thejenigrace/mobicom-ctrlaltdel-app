package com.mobicom.ctrlaltdel.app.model;

public class Course {
	private int id;
	private String name;
	private String location;
	private String dayOfWeek;
	private String time;
	
	public static final String TABLE_NAME = "Course";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LOCATION = "location";
	public static final String COLUMN_DAY = "day";
	public static final String COLUMN_TIME = "time";
	
	
	public Course() {
		
	}
	
	public Course(String name, String time, String dayOfWeek, String location) {
		
		this.name = name;
		this.time = time;
		this.dayOfWeek = dayOfWeek;
		this.location = location;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
}
