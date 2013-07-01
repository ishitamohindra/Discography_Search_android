package com.example.discographysearch;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MyTweet extends ListActivity  {
	String[] category;
	Spinner mSpin;
	EditText mText;
	HttpClient client;
	JSONObject json;
	Button button;
//	TextView httpStuff;
	String name,cat,url;
	ArrayList<HashMap<String, Object>> all_list;

	@TargetApi(9) @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);
		
		all_list = new ArrayList<HashMap<String, Object>>();
		
		//httpStuff = (TextView) findViewById(R.id.httpData);
		client = new DefaultHttpClient();
		Bundle gotBasket = getIntent().getExtras();
		url = gotBasket.getString("key");
		new Read().execute();
		
	}

	public JSONObject myjson() throws ClientProtocolException, IOException, JSONException
			{
			HttpGet get = new HttpGet(url);
			HttpResponse r = client.execute(get);
			int status = r.getStatusLine().getStatusCode();
		 
			if(status == 200){
				HttpEntity e = r.getEntity();
				String data = EntityUtils.toString(e);
			JSONObject timeline = new JSONObject(data);
			JSONObject timeline1 = timeline.getJSONObject("results");
			//return timeline1 and then use its object to extract data into the list
			//JSONArray timeline2 = timeline1.getJSONArray("result");
			Log.d("info: ", timeline1.toString());
			//int len = timeline2.length();
			//JSONObject ijson = timeline2.getJSONObject(0);
			return timeline1;		
		}
		else{
			Toast.makeText(MyTweet.this,"error", Toast.LENGTH_SHORT).show();
			return null;
		}
	}
	
	public class Read extends AsyncTask<String, Integer, String>{
		
		ProgressDialog dialog;
		protected void onPreExecute(){
			dialog = new ProgressDialog(MyTweet.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMessage("Loading Data...");
			dialog.setMax(100);
			dialog.show();
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			for(int i = 0; i < 20; i++){
				publishProgress(5);	
				
			}
	
			try {
				json = myjson();
				JSONArray json_arr = json.getJSONArray("result");
				int len = json_arr.length();
				for(int i = 0; i < len; i++){
					JSONObject o = json_arr.getJSONObject(i);
					
					HashMap<String, Object> map = new HashMap<String, Object>();
					Log.d("COVERLENGTH = ", String.format("Value = %d", o.getString("cover").trim().length()));
					/*if(o.getString("cover").trim().length()>8){
						String cover = o.getString("cover").trim();
						map.put("cover", cover);
					}
					else{
						int cover = R.drawable.mike;
						map.put("cover", cover);
					}
					*/
					
				/*
					try {
						  ImageView img = (ImageView)findViewById(R.id.ivCover);
						  Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(cover).getContent());
						  img.setImageBitmap(bitmap); 
						} catch (MalformedURLException e) {
						  e.printStackTrace();
						} catch (IOException e) {
						  e.printStackTrace();
						}
					*/
					
					//int cover = R.drawable.mike;
					String cover = o.getString("cover").trim();
					String name = o.getString("name").trim();
					String genre = o.getString("genre").trim();
					String year = o.getString("year").trim();
					Log.d("COVER = ", cover);
					Log.d("NAME = ", name);
					Log.d("GENRE = ", genre);
					Log.d("YEAR = ", year);
					map.put("cover", cover);
					map.put("name", name);
					map.put("genre", genre);
					map.put("year", year);
					all_list.add(map);
				}
				
//				return ijson.getString(arg0[0]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}

		protected void onProgressUpdate(Integer...integers){
			dialog.incrementProgressBy(integers[0]);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.dismiss();
			String[] header = {"cover","name","genre","year"};
			int[] content = {R.id.ivCover,R.id.tvName,R.id.tvGenre,R.id.tvYear};
			
			SimpleAdapter adapter = new SimpleAdapter(MyTweet.this, all_list, R.layout.list_item_ar, header, content);
			setListAdapter(adapter);
			}
		
	}
	
	
}
