package com.mobicom.ctrlaltdel.app.graphicnote;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.mobicom.ctrlaltdel.app.amain.R;
import com.mobicom.ctrlaltdel.app.database.CourseDatabaseHelper;
import com.mobicom.ctrlaltdel.app.model.Course;

public class GraphicNotesMainFragment extends Fragment {
	
	private OnFragmentInteractionListener mCallBack;	
	
	private GridView gvFolders;

	public GraphicNotesMainFragment() {
		// Required empty public constructor
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setHasOptionsMenu(true);
		
		getActivity().getActionBar().show();
		
		mCallBack.toFindMainFragmentInteraction(3);
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_main_graphic_note, container, false);
		
		this.gvFolders = (GridView) rootView.findViewById(R.id.gridview_course_folders);

		CourseDatabaseHelper courseDBHelper = 
				new CourseDatabaseHelper(getActivity().getBaseContext(), "courses", null, 1);
		ArrayList<Course> courseList = courseDBHelper.getCourses();
		CourseFolderGridAdapter gridAdapter = new CourseFolderGridAdapter(rootView.getContext(), R.layout.graphic_note_course_folder_gridcell_item, courseList);
		this.gvFolders.setAdapter(gridAdapter);
		this.gvFolders.setOnItemClickListener(viewNotes);
		
		return rootView;
	}
	
	OnItemClickListener viewNotes = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) 
		{
			mCallBack.onGoToNoteGalleryFragmentInteraction();
		}
	};
	
	public String getCourseName() {
		
		return null;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(int position) {
		if (mCallBack != null) {
			mCallBack.toFindMainFragmentInteraction(position);
			mCallBack.onGoToGraphicNoteCameraFragmentInteraction();
			mCallBack.onGoToNoteGalleryFragmentInteraction();
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

	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void toFindMainFragmentInteraction(int position);
		
		public void onGoToGraphicNoteCameraFragmentInteraction();
		
		public void onGoToNoteGalleryFragmentInteraction();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.graphic_note_main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.action_camera:
				mCallBack.onGoToGraphicNoteCameraFragmentInteraction();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}		
	}
}
