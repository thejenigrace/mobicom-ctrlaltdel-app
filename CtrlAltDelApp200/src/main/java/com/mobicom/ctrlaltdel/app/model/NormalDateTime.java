package com.mobicom.ctrlaltdel.app.model;

public class NormalDateTime {
	
	private int month;
	private int day;
	private int year;
	
	private int hour;
	private int minute;
	
	public NormalDateTime() {
		
	}
	
	public int getForComparingDate() {
		
		String temp = "" + this.year + this.month + this.day;
		return Integer.parseInt(temp);
	}
	
	public int getForComparingTime() {
		
		String temp = "" + this.hour + this.minute;
		return Integer.parseInt(temp);
	}
	
	public String getForDisplayHeader() {
		
		String display = this.getMonthString() + " " + this.getDay() + ", " + this.getYear();
		
		return display;
	}
	
	public String getMonthString() {
		
		String strMonth = null;
		
		switch(this.month) {
			case 1: strMonth = "January"; break;
			case 2: strMonth = "February"; break;
			case 3: strMonth = "March"; break;
			case 4: strMonth = "April"; break;
			case 5: strMonth = "May"; break;
			case 6: strMonth = "June"; break;
			case 7: strMonth = "July"; break;
			case 8: strMonth = "August"; break;
			case 9: strMonth = "September"; break;
			case 10: strMonth = "October"; break;
			case 11: strMonth = "November"; break;
			case 12: strMonth = "December"; break;
		}
		
		return strMonth;
	}
	
	public int getMonth() {
		return month;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
	
	public int getDay() {
		return day;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

}
