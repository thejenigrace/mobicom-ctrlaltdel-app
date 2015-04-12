package com.mobicom.ctrlaltdel.app.graphicnote;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobicom.ctrlaltdel.app.amain.R;
import com.mobicom.ctrlaltdel.app.database.CourseDatabaseHelper;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link AddGraphicNoteCameraFragment.OnFragmentInteractionListener}
 * interface to handle interaction events.
 *
 */
public class AddGraphicNoteCameraFragment extends Fragment {

	private OnFragmentInteractionListener mCallBack;
	
	// Activity request codes
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	
	// directory name to store captured images and videos
	private static String IMAGE_DIRECTORY_NAME = "Ctrl Alt Del";

	private Uri fileUri; // file url to store image
	
	private ImageButton imgBtnCancel;
	
	private ImageView imgPreview;
	private Spinner courseSpinner;
	private ImageButton capture;
	
	public AddGraphicNoteCameraFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		getActivity().getActionBar().hide();;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_add_graphic_note_camera, container, false);
		
		this.imgBtnCancel = (ImageButton) rootView.findViewById(R.id.camera_cancel);
		this.imgBtnCancel.setOnClickListener(cancel);
		
		this.courseSpinner = (Spinner) rootView.findViewById(R.id.spinner_saved_courses);
		this.capture = (ImageButton) rootView.findViewById(R.id.capture_image);
		
		CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(getActivity().getApplicationContext(),
				"courses", null, 1);
		
		ArrayList<String> courseList = dbHelper.getCourseNames();
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), 
				R.layout.graphic_note_camera_course_name, courseList);
	
		spinnerAdapter.setDropDownViewResource(R.layout.graphic_note_camera_spinner_dropdown_item);
		this.courseSpinner.setAdapter(spinnerAdapter);
		
		/*
		 * Capture image button click event
		 */
		this.capture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// capture picture
				captureImage();
			}
		});
		
		return rootView;
	}
	
	OnClickListener cancel = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mCallBack.onGoBackToMainFragmentInteraction(true, 3);
		}
		
	};
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// save file URL in bundle as it will be null on screen orientation
	    // changes
		outState.putParcelable("file_uri", fileUri);
	}
	
	
	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(boolean isCancel, int eventOrCourseOrGraphic) {
		if (mCallBack != null) {
			mCallBack.onGoBackToMainFragmentInteraction(isCancel, eventOrCourseOrGraphic);
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
		public void onGoBackToMainFragmentInteraction(boolean isCancel, int eventOrCourseOrGraphic);

		public void onCameraMainFragmentInteraction(boolean finish);
	}
	
	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		IMAGE_DIRECTORY_NAME = courseSpinner.getSelectedItem().toString();
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);	
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == getActivity().RESULT_OK) {
				// successfully captured the image
				// display it in image view
				Toast.makeText(getActivity().getApplicationContext(),
						"Image has been saved", Toast.LENGTH_LONG)
						.show();
				previewCapturedImage();				
			}
			else if (resultCode == getActivity().RESULT_CANCELED) {
			// user cancelled Image capture
			Toast.makeText(getActivity().getApplicationContext(),
					"User cancelled image capture", Toast.LENGTH_SHORT)
					.show();
			}
			else {
			// failed to capture image
			Toast.makeText(getActivity().getApplicationContext(),
					"Sorry! Failed to capture image", Toast.LENGTH_SHORT)
					.show();
		}
		}
			
	}
	
	/*
	 * Creating file uri to store image
	 */
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}
	
	/*
	 * returning image
	 */
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(),
						//.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						IMAGE_DIRECTORY_NAME);
						//^ DIRECTORY TO CHANGE
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} 
		else {
			return null;
		}

		return mediaFile;
	}
	
	/*
	 * Display image from a path to ImageView
	 */
	private void previewCapturedImage() {
		try {
			imgPreview.setVisibility(View.VISIBLE);

			// bimatp factory
			BitmapFactory.Options options = new BitmapFactory.Options();

			// downsizing image as it throws OutOfMemory Exception for larger
			// images
			options.inSampleSize = 8;

			final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
					options);

			imgPreview.setImageBitmap(bitmap);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}
