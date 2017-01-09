package com.seaco.denguesitecapture.activities;

import java.io.File;
import java.util.ArrayList;

import com.seaco.denguesitecapture.R;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class CapturedPhotosGrid extends Activity {

	private static final String TAG = "CapturedPhotosGrid";
	Intent intentObject = getIntent();	
	String userID;

	public class ImageAdapter extends BaseAdapter {

		private Context mContext;
		ArrayList<String> itemList = new ArrayList<String>();

		public ImageAdapter(Context c) {
			mContext = c; 
		}

		void add(String path){
			itemList.add(path); 
		}

		@Override
		public int getCount() {
			return itemList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {  // if it's not recycled, initialize some attributes
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(220, 220));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}

			Bitmap bm = decodeSampledBitmapFromUri(itemList.get(position), 220, 220);

			imageView.setImageBitmap(bm);
			return imageView;
		}

		public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

			Bitmap bm = null;
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bm = BitmapFactory.decodeFile(path, options); 

			return bm;   
		}

		public int calculateInSampleSize(

				BitmapFactory.Options options, int reqWidth, int reqHeight) {
			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {
				Log.d(TAG,"inSampleSize 1"+inSampleSize);
				if (width > height) {
					inSampleSize = Math.round((float)height / (float)reqHeight);  
					Log.d(TAG,"inSampleSize 2"+inSampleSize);
				} else {
					Log.d(TAG,"inSampleSize 3"+inSampleSize);
					inSampleSize = Math.round((float)width / (float)reqWidth);    
				}   
			}
			Log.d(TAG,"inSampleSize 4"+inSampleSize);
			return inSampleSize;    
		}

	}

	ImageAdapter myImageAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gridview);

		//get Intent
		userID = getIntent().getExtras().getString("userID");

		GridView gridview = (GridView) findViewById(R.id.gridview);
		myImageAdapter = new ImageAdapter(this);
		gridview.setAdapter(myImageAdapter);

		String ExternalStorageDirectoryPath = Environment
				.getExternalStorageDirectory()
				.getAbsolutePath();

		String targetPath = ExternalStorageDirectoryPath + "/picupload/"+userID+"/";

		Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();
		File targetDirector = new File(targetPath);

		File[] files = targetDirector.listFiles();

		File folder = new File(targetPath); //fix if the user enter first time at this page without upload

		if(folder.exists()){ //fix if the user enter first time at this page without upload
			for (File file : files){
				Log.d(TAG,"targetPath: "+files);
				myImageAdapter.add(file.getAbsolutePath());
			}

		} 
	}

}