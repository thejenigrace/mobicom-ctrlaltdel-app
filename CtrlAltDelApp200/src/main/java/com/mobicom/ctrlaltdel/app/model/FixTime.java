package com.mobicom.ctrlaltdel.app.model;

public class FixTime {
	
	private int hours;
	private int minutes;
	
	private String mHours;
	private String mMinutes;
	private String mAMorPM;
	
	public FixTime(int hours, int minutes) {
		
		this.hours = hours;
		this.minutes = minutes;
		
		this.mHours = "" + hours;
		this.mMinutes = "" + minutes;
		
		this.changeHours();
		this.changeMinutes();
	}
	
	public String getFixedTime() {
		
		String fixedTime = "" + mHours + ":" + mMinutes + " " + mAMorPM;
		
		return fixedTime;
	}
	
	public void changeHours() {
		
		if(hours == 0 || hours >= 12) {
			
			if(hours == 0) {
				mAMorPM = "AM";
			}
			else {
				mAMorPM = "PM";
			}
				
			switch(hours) {
				case 13: mHours = "01"; break;
				case 14: mHours = "02"; break;
				case 15: mHours = "03"; break;
				case 16: mHours = "04"; break;
				case 17: mHours = "05"; break;
				case 18: mHours = "06"; break;
				case 19: mHours = "07"; break;
				case 20: mHours = "08"; break;
				case 21: mHours = "09"; break;
				case 22: mHours = "10"; break;
				case 23: mHours = "11"; break;
				case 0: mHours = "12"; break;
				default: System.out.println("N/A Hours 1");
			}
		}	
		else {
			mAMorPM = "AM";
			
			switch(minutes) {
				case 0: mMinutes = "00"; break;
				case 1: mMinutes = "01"; break;
				case 2: mMinutes = "02"; break;
				case 3: mMinutes = "03"; break;
				case 4: mMinutes = "04"; break;
				case 5: mMinutes = "05"; break;
				case 6: mMinutes = "06"; break;
				case 7: mMinutes = "07"; break;
				case 8: mMinutes = "08"; break;
				case 9: mMinutes = "09"; break;
				default: System.out.println("N/A Hours 2");
			}
		}
	}
	
	public void changeMinutes() {
		
		if(minutes >= 0 || minutes <= 9) {
			switch(minutes) {
				case 0: mMinutes = "00"; break;
				case 1: mMinutes = "01"; break;
				case 2: mMinutes = "02"; break;
				case 3: mMinutes = "03"; break;
				case 4: mMinutes = "04"; break;
				case 5: mMinutes = "05"; break;
				case 6: mMinutes = "06"; break;
				case 7: mMinutes = "07"; break;
				case 8: mMinutes = "08"; break;
				case 9: mMinutes = "09"; break;
				default: System.out.println("N/A Minutes");
			}
		}
	}
}
