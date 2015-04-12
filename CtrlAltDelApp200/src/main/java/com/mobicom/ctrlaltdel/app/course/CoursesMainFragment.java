package com.mobicom.ctrlaltdel.app.course;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;
import com.mobicom.ctrlaltdel.app.amain.R;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link CoursesMainFragment.OnFragmentInteractionListener}
 * interface to handle interaction events.
 *
 */
public class CoursesMainFragment extends Fragment {

	private OnFragmentInteractionListener mCallBack;
	
	private ExpandableListAdapter elvCourseAdapter;
    private ExpandableListView elvCourses;
//    private List<Course> listDataHeader;
//    private HashMap<String, List<String>> listDataChild;
    
    private FloatingActionButton fabAddCourse;
    
//    private CourseDatabaseHelper courseDBHelper; 

	public CoursesMainFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().getActionBar().show();
		
		mCallBack.toFindMainFragmentInteraction(2);
		
//		this.courseDBHelper = new CourseDatabaseHelper(getActivity().getBaseContext(), "courses", null, 1);
		
		
		// initialize
//		this.listDataHeader = new ArrayList<Course>();
//		this.listDataChild = new HashMap<String, List<String>>();
		
		// prepare course data
//		this.listDataHeader = courseDBHelper.getCourses();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_main_course, container, false);
		
		this.elvCourses = (ExpandableListView) rootView.findViewById(R.id.elv_courses);
        this.elvCourseAdapter = new CourseExpandableListAdapter(rootView.getContext());
		this.elvCourses.setAdapter(elvCourseAdapter);
		
		
		this.fabAddCourse = (FloatingActionButton) rootView.findViewById(R.id.fab_addcourse);
		this.fabAddCourse.setColor(rootView.getResources().getColor(R.color.material_design_blue_500));
		this.fabAddCourse.listenTo(elvCourses);
		
		return rootView;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(int position) {
		if (mCallBack != null) {
			mCallBack.onGoToAddOrEditCourseFragmentInteraction();
			mCallBack.toFindMainFragmentInteraction(position);
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

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onGoToAddOrEditCourseFragmentInteraction();

		public void toFindMainFragmentInteraction(int position);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.course_main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.action_add_new_course:
				mCallBack.onGoToAddOrEditCourseFragmentInteraction();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
		
	}
	
}
