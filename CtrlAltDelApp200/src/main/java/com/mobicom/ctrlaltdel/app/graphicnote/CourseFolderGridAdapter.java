package com.mobicom.ctrlaltdel.app.graphicnote;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobicom.ctrlaltdel.app.amain.R;
import com.mobicom.ctrlaltdel.app.model.Course;

public class CourseFolderGridAdapter extends ArrayAdapter<Course> {

	private ArrayList<Course> courseList;
//	private String courseName;
	
	private TextView tvCourseFolder;
	
	public CourseFolderGridAdapter(Context context, int resource, ArrayList<Course> courseList) {
		super(context, resource, courseList);
		this.courseList = courseList;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		  if (convertView == null) {
	            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = inflater.inflate(R.layout.graphic_note_course_folder_gridcell_item, parent, false);
	        }
	 
//		  ImageView ivCourseFolder = (ImageView) convertView.findViewById(R.id.course_folder_img);
//		  TextView tvCourseName = (TextView) convertView.findViewById(R.id.course_folder_title);
//		  Course course = courseList.get(position);
//	      tvCourseName.setText(course.getName());
//	      ivCourseFolder.setImageResource(R.drawable.ic_folder_big);
		  
		  Course course = courseList.get(position);
		  
		  this.tvCourseFolder = (TextView) convertView.findViewById(R.id.tv_course_folder);
		  this.tvCourseFolder.setText(course.getName());
		  
	      return convertView;
	}
	
//	public String getCourseName(){
//		return courseName;
//	}
}
