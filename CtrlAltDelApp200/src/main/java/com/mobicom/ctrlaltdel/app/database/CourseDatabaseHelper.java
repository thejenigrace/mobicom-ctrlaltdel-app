package com.mobicom.ctrlaltdel.app.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.mobicom.ctrlaltdel.app.model.Course;

public class CourseDatabaseHelper extends SQLiteOpenHelper{
	Course[] courses;
	int i = 0;
	
	public CourseDatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE " + Course.TABLE_NAME + " (" + Course.COLUMN_ID + " integer PRIMARY KEY, " + Course.COLUMN_NAME + " text, " + Course.COLUMN_TIME + " text, " + Course.COLUMN_DAY + " text, " + Course.COLUMN_LOCATION + " text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	//add a course
	public void addCourse(Course c) {
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(Course.COLUMN_NAME, c.getName());
		values.put(Course.COLUMN_TIME, c.getTime());
		values.put(Course.COLUMN_DAY, c.getDayOfWeek());
		values.put(Course.COLUMN_LOCATION, c.getLocation());
		
		db.insert(Course.TABLE_NAME, null, values);
		db.close();
	}
	
	//get courses
	public ArrayList<Course> getCourses() {
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.query
				(
						Course.TABLE_NAME, 
						new String[]
								{
									Course.COLUMN_ID, 
									Course.COLUMN_NAME, 
									Course.COLUMN_LOCATION, 
									Course.COLUMN_DAY, 
									Course.COLUMN_TIME
								}, 
						null, 
						null, 
						null, 
						null, 
						null
				);
		
		ArrayList<Course> courses = new ArrayList<Course>();
		
		if(c.moveToFirst()) {
			do {
				int id = c.getInt(c.getColumnIndex(Course.COLUMN_ID));
				String name = c.getString(c.getColumnIndex(Course.COLUMN_NAME));
				String location = c.getString(c.getColumnIndex(Course.COLUMN_LOCATION));
				String time = c.getString(c.getColumnIndex(Course.COLUMN_TIME));
				String day = c.getString(c.getColumnIndex(Course.COLUMN_DAY));
				
				Course newCourse = new Course();
				newCourse.setId(id);
				newCourse.setName(name);
				newCourse.setLocation(location);
				newCourse.setTime(time);
				newCourse.setDayOfWeek(day);
				
				courses.add(newCourse);
				
			}while(c.moveToNext());
		}
		
		c.close();
		db.close();
		
		return courses;	
	}
	
	public ArrayList<String> getCourseNames() {
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.query(Course.TABLE_NAME, 
				new String[]{Course.COLUMN_NAME}, 
				null, 
				null, 
				null, 
				null, 
				null);
		
		
		ArrayList<String> courseNames = new ArrayList<String>();
		
		if(c.moveToFirst()) {
			do {
				String name = c.getString(c.getColumnIndex(Course.COLUMN_NAME));
				courseNames.add(name);
			}while(c.moveToNext());
		}
		
		c.close();
		db.close();
		
		return courseNames;	
	}
	
	//delete course
	public void deleteCourse(String name) {
		SQLiteDatabase db = getWritableDatabase();
		
		db.delete(Course.TABLE_NAME, Course.COLUMN_NAME + " =? ", new String[]{String.valueOf(name)});
		
		db.close();
	}
}
