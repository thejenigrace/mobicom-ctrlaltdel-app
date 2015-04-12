package com.mobicom.ctrlaltdel.app.graphicnote;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobicom.ctrlaltdel.app.amain.R;

public class GraphicNoteGalleryFragment extends Fragment {
	
	private ImageButton imgBtnBack;
	
	private GridView gridView;
//	private SimpleCursorAdapter gridAdapter;	
	private int count;
	private Bitmap[] thumbnails;
	private String[] arrPath;
	private ImageAdapter imageAdapter;

	private OnFragmentInteractionListener mCallBack;

	public GraphicNoteGalleryFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActivity().getActionBar().hide();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_graphic_note_gallery, container, false);
		
		this.imgBtnBack = (ImageButton) rootView.findViewById(R.id.gallery_back);
		this.imgBtnBack.setOnClickListener(back);
		
		this.gridView = (GridView) rootView.findViewById(R.id.gridview_pictures);
		
		final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
		final String orderBy = MediaStore.Images.Media._ID;
//		Cursor imagecursor = getActivity().managedQuery(
//				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
//				columns,
//				MediaStore.Images.Media.DATA + " LIKE ? ",
//				new String[]{"%MOBICOM%"}, 
//				orderBy);
		
		Cursor imagecursor = getActivity().getContentResolver().query
				(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
						columns,
						MediaStore.Images.Media.DATA + " LIKE ? ",
						new String[]{"%MOBICOM%"}, 
						orderBy
				);
		int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
		this.count = imagecursor.getCount();
		this.thumbnails = new Bitmap[this.count];
		this.arrPath = new String[this.count];
		for (int i = 0; i < this.count; i++) {
			imagecursor.moveToPosition(i);
			int id = imagecursor.getInt(image_column_index);
			int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
			thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
					getActivity().getApplicationContext().getContentResolver(), id,
					MediaStore.Images.Thumbnails.MINI_KIND, null);
			arrPath[i]= imagecursor.getString(dataColumnIndex);
		}

		imageAdapter = new ImageAdapter();
		gridView.setAdapter(imageAdapter);
		
		return rootView;
	}
	
	OnClickListener back = new OnClickListener(){

		@Override
		public void onClick(View v) {
			
			mCallBack.onGoBackToMainFragmentInteraction(false, 3);
		}
		
	};

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter() {
			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.graphic_note_image_gridcell_item, null);
				holder.imageview = (ImageView) convertView.findViewById(R.id.image_cell);
				holder.text = (TextView) convertView.findViewById(R.id.image_cell_text);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.imageview.setId(position);
			holder.imageview.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					int id = v.getId();
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
					getActivity().startActivity(intent);
				}
			});
			holder.imageview.setImageBitmap(thumbnails[position]);
			holder.text.setText(Uri.parse(arrPath[position]).toString());
			holder.id = position;
			
			return convertView;
		}
	}
	
	class ViewHolder {
		int id;
		ImageView imageview;
		TextView text;
	}
	
	//AUTO GENERATED
	public void onButtonPressed(boolean isCancel, int toastNo) {
		if (mCallBack != null) {
			mCallBack.onGoBackToMainFragmentInteraction(isCancel, toastNo);
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
		
		public void onGoBackToMainFragmentInteraction(boolean isCancel, int toastNo);
	}
}
