package com.seaco.denguesitecapture.activities;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.seaco.denguesitecapture.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class CapturedPhotosGridOff extends Activity  {

	private static final String TAG = "CapturedPhotosGridOff";
	private GridView gridV;
	private ImageAdapter imageAdapter;

	public int currentPage = 1;
	public int TotalPage = 0;

	public Button btnNext;
	public Button btnPre;

	ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, Object>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);        
		// ProgressBar
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.gridview_off);

		// GridView and imageAdapter
		gridV = (GridView) findViewById(R.id.gridView1);
		gridV.setClipToPadding(false);
		imageAdapter = new ImageAdapter(getApplicationContext()); 
		gridV.setAdapter(imageAdapter);


		// Next
		btnNext = (Button) findViewById(R.id.btnNext);
		// Perform action on click
		btnNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				currentPage = currentPage + 1;
				ShowData();
			}
		});

		// Previous
		btnPre = (Button) findViewById(R.id.btnPre);
		// Perform action on click
		btnPre.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				currentPage = currentPage - 1;
				ShowData();
			}
		});

		// Show first load
		ShowData();

	}

	public void ShowData()
	{
		btnNext.setEnabled(false);
		btnPre.setEnabled(false);

		setProgressBarIndeterminateVisibility(true); 
		new LoadContentFromServer().execute();
	}


	class LoadContentFromServer extends AsyncTask<Object, Integer, Object> {

		@Override
		protected Object doInBackground(Object... params) {

			String url = Config.URL_GET_ALL_UPLOADED;
			JSONArray data;
			try {
				data = new JSONArray(getJSONUrl(url));
				MyArrList = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> map;

				/*
				 * TotalRows = Show for total rows
				 * TotalPage = Show for total page
				 */

				int displayPerPage = 4;   // Per Page
				int TotalRows = data.length();
				int indexRowStart = ((displayPerPage*currentPage)-displayPerPage);

				if(TotalRows<=displayPerPage)
				{
					TotalPage =1;
				}
				else if((TotalRows % displayPerPage)==0)
				{
					TotalPage =(TotalRows/displayPerPage) ;
				}
				else
				{
					TotalPage =(TotalRows/displayPerPage)+1;
					TotalPage = (int)TotalPage;
				}
				int indexRowEnd = displayPerPage * currentPage;
				if(indexRowEnd > TotalRows)
				{
					indexRowEnd = TotalRows;
				}

				for(int i = indexRowStart; i < indexRowEnd; i++){
					JSONObject c = data.getJSONObject(i);
					map = new HashMap<String, Object>();
					map.put("ID", (String)c.getString("id"));
					map.put("Filename", (String)c.getString("filename"));
					map.put("Location", (String)c.getString("latitude")+","+(String)c.getString("longitude"));

					// Thumbnail Get ImageBitmap To Object
					//String urlPath = Config.URL_MAIN+"dengueSites/uploads/"+c.getString("filename"); 
					String urlPath = "https://storage.googleapis.com/dengue-seaco/"+c.getString("filename");
					Log.d(TAG,""+urlPath);

					Bitmap newBitmap = loadBitmap(urlPath);
					map.put("ImagePathBitmap", newBitmap);

					MyArrList.add(map);

					publishProgress(i);

				}


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.d(TAG,"ERRORRRRRRRRRRRRRRRRRRRRRR");
				e.printStackTrace();
			}

			return null;
		}

		@Override
		public void onProgressUpdate(Integer... progress) {
			imageAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Object result) {

			// Disabled Button Next
			if(currentPage >= TotalPage)
			{
				btnNext.setEnabled(false);
			}
			else
			{
				btnNext.setEnabled(true);
			}

			// Disabled Button Previos
			if(currentPage <= 1)
			{
				btnPre.setEnabled(false);
			}
			else
			{
				btnPre.setEnabled(true);
			}

			setProgressBarIndeterminateVisibility(false); // When Finish
		}
	}	


	class ImageAdapter extends BaseAdapter {

		private Context mContext; 

		public ImageAdapter(Context context) { 
			mContext = context; 
		} 

		public int getCount() { 
			return MyArrList.size();    
		} 

		public Object getItem(int position) { 
			return MyArrList.get(position); 
		} 

		public long getItemId(int position) { 
			return position; 
		} 

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


			if (convertView == null) {
				convertView = inflater.inflate(R.layout.gridview_column_off, null); 
			}

			// ColPhoto
			ImageView imageView = (ImageView) convertView.findViewById(R.id.ColPhoto);
			imageView.getLayoutParams().height = 300;
			imageView.getLayoutParams().width = 300;
			imageView.setPadding(5, 5, 5, 5);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			try
			{
				imageView.setImageBitmap((Bitmap)MyArrList.get(position).get("ImagePathBitmap"));
			} catch (Exception e) {
				// When Error
				imageView.setImageResource(android.R.drawable.ic_menu_report_image);
			}

			// ColImageID
			TextView txtImageID = (TextView) convertView.findViewById(R.id.ColImageID);
			txtImageID.setPadding(10, 10, 10, 10);
			txtImageID.setText("ID : " + MyArrList.get(position).get("ID").toString());	

			// ColItemID
			TextView txtFilename = (TextView) convertView.findViewById(R.id.ColFilename);
			txtFilename.setPadding(10, 10, 10, 10);
			txtFilename.setText("Filename : " + MyArrList.get(position).get("Filename").toString());
			
			// ColItemID
			TextView txtItemID = (TextView) convertView.findViewById(R.id.ColLocation);
			txtItemID.setPadding(10, 10, 10, 10);
			txtItemID.setText("Location : " + MyArrList.get(position).get("Location").toString());	

			return convertView;

		}

	}

	/*** Get JSON Code from URL 
	 * @return ***/
	public String getJSONUrl(String url){
		//String requestedUrl = request.getParameter("url");
		HttpGet httpGet = new HttpGet(url);
		StringBuilder str = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			response.setHeader("Content-Type","image/jpeg");

			if (statusCode == 200) { // Download OK
				Log.e(TAG, "Failed to download file 2..");
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					str.append(line);
					Log.e(TAG, "Failed to download file 3..");
				}
			} else {
				Log.e(TAG, "Failed to download file..");
			}
		} catch (ClientProtocolException e) {
			Log.e(TAG, "Failed to download file 4..");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "Failed to download file 5..");
			e.printStackTrace();
		}
		Log.e(TAG, "Failed to download file 6.."+str.toString());
		return str.toString();
	}

	/***** Get Image Resource from URL (Start) *****/
	private static final int IO_BUFFER_SIZE = 4 * 1024;
	public static Bitmap loadBitmap(String url) {
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;

		try {
			in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);//try change here

			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
			copy(in, out);
			out.flush();

			final byte[] data = dataStream.toByteArray();
			BitmapFactory.Options options = new BitmapFactory.Options();
			//options.inSampleSize = 1;

			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
		} catch (IOException e) {
			Log.e(TAG, "Could not load Bitmap from 1: " + url);
			Log.e(TAG, "Could not load Bitmap from 2: " + e.toString());
		} finally {
			closeStream(in);
			closeStream(out);
		}

		return bitmap;
	}

	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				android.util.Log.e(TAG, "Could not close stream", e);
			}
		}
	}

	private static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[IO_BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}
	/***** Get Image Resource from URL (End) *****/

}
