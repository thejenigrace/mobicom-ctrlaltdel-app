package com.mobicom.ctrlaltdel.app.course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mobicom.ctrlaltdel.app.amain.R;
import com.mobicom.ctrlaltdel.app.database.CourseDatabaseHelper;
import com.mobicom.ctrlaltdel.app.model.Course;

public class CourseExpandableListAdapter extends BaseExpandableListAdapter {
	
	private Context _context;
    private List<Course> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    
    // GroupView
    private TextView tvCourseName;
    private TextView tvCourseLocation;
    private TextView tvCourseDayOfWeek;
    private TextView tvCourseTime;
    
    // ChildView
    private TextView tvEventName;
    private TextView tvEventDate;
    
    private CourseDatabaseHelper courseDBHelper; 
    
    // Constructor
    public CourseExpandableListAdapter(Context context, List<Course> listDataHeader, HashMap<String, List<String>> listChildData) {
    	
    	this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
	}
    
    public CourseExpandableListAdapter(Context context) {
  
    	this._context = context;
    	this._listDataHeader = new ArrayList<Course>();
    	this._listDataChild = new HashMap<String, List<String>>();
    	
    	this.courseDBHelper = new CourseDatabaseHelper(context, "courses", null, 1);
    	
    	this._listDataHeader = this.courseDBHelper.getCourses();
    }

	@Override
	public int getGroupCount() {
		
		return this._listDataHeader.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int size = 0;
		
		try {
			
			size = this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
			
		} catch(NullPointerException e) {
			
			size = 0;
			Toast.makeText(this._context, "No Events under this Course", Toast.LENGTH_SHORT).show();
		}
		
		return size;
	}

	@Override
	public Object getGroup(int groupPosition) {
		
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		Course course = (Course) getGroup(groupPosition);
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.course_list_group_item, parent, false);
        }
 
        this.tvCourseName = (TextView) convertView.findViewById(R.id.tv_course_name);
        this.tvCourseLocation = (TextView) convertView.findViewById(R.id.tv_course_location);
        this.tvCourseDayOfWeek = (TextView) convertView.findViewById(R.id.tv_course_dayofweek);
        this.tvCourseTime = (TextView) convertView.findViewById(R.id.tv_course_time);
        
        this.tvCourseName.setText(course.getName());
        this.tvCourseLocation.setText(course.getLocation());
        this.tvCourseDayOfWeek.setText(course.getDayOfWeek());
        this.tvCourseTime.setText(course.getTime());
        
        return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		final String childText = (String) getChild(groupPosition, childPosition);
		 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.course_list_child_item, parent, false);
        }
 
        this.tvEventName = (TextView) convertView.findViewById(R.id.tv_course_eventname);
        this.tvEventDate = (TextView) convertView.findViewById(R.id.tv_course_eventdate);
        
        return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		
		return true;
	}
	
}
