package com.example.discographysearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.widget.LoginButton;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.facebook.FacebookException;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
//import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
//import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class MyListclass extends ListActivity  {
	String[] category;
	Spinner mSpin;
	EditText mText;
	MediaPlayer mp;
	HttpClient client;
	String sample;
	JSONObject json;
	boolean fl = false;
	
	 String n_ar,y_ar,g_ar,d_ar,c_ar;
	 String t_al,y_al,g_al,d_al,c_al,a_al;
	 String t_s,p_s,com_s,d_s,pic_s,s_s;
	 String samUrl;
	ListView lv;
	int myflag;
	//String APP_ID = "456237357787982";
	Facebook myfb;
	String name,cat,url;
	ArrayList<HashMap<String, Object>> all_list;
	private UiLifecycleHelper uiHelper;
	//private Button publishButton;
	LoginButton authButton;
	
	
	
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	

	private void publishFeedDialog(int f) {
	    Bundle params = new Bundle();
	    
	    if(f == 1)
	    {
		    params.putString("name", n_ar);
		    params.putString("caption", "Genre of music: " + g_ar);
		    params.putString("description", "I like " + n_ar + " who is active since " + y_ar);
		    params.putString("link", d_ar);
		    params.putString("picture", c_ar);
		    params.putString("properties", "{\"Look at the details\":{ \"text\": \"here\", \"href\":\""+ d_ar + "\"}}" );
		 }

	    if(f == 2)
	    {
		    params.putString("name", t_al);
		    params.putString("description", "Artist : " + a_al + " & Genre : " + g_al);
		    params.putString("caption", "I like " + t_al + " released in " + y_al);
		    params.putString("link", d_al);
		    params.putString("picture", c_al);
		    params.putString("properties", "{\"Look at the details\":{ \"text\": \"here\", \"href\":\""+ d_al + "\"}}" );
		 }

	    if(f == 3)
	    {
		    params.putString("name", t_s);
		    params.putString("caption", "Performer : " + p_s );
		    params.putString("description", "I like " + t_s + " composed by " + com_s);
		    params.putString("link", d_s);
		    params.putString("picture", pic_s);
		    params.putString("properties", "{\"Look at the details\":{ \"text\": \"here\", \"href\":\""+ d_s + "\"}}" );
		 }
	    
	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(MyListclass.this,
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(MyListclass.this,
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(MyListclass.this.getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(MyListclass.this.getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(MyListclass.this.getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}
	
	
	
	@TargetApi(9) @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);
		lv = (ListView)findViewById(android.R.id.list);
		
		all_list = new ArrayList<HashMap<String, Object>>();
	
		//httpStuff = (TextView) findViewById(R.id.httpData);
		client = new DefaultHttpClient();
		Bundle gotBasket = getIntent().getExtras();
		url = gotBasket.getString("key");
		myflag = gotBasket.getInt("flag");
		
		uiHelper = new UiLifecycleHelper(MyListclass.this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		//Button publish = (Button) findViewById(R.id.publishButton);
		
		authButton = (LoginButton) findViewById(R.id.authButton);
		
		Log.d("FLAG +", String.format("value = %d", myflag));
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
			
			Log.d("info: ", timeline1.toString());
			
			return timeline1;		
		}
		else{
			Toast.makeText(MyListclass.this,"error", Toast.LENGTH_SHORT).show();
			return null;
		}
	}
	
	public class Read extends AsyncTask<String, Integer, String>{
		
		ProgressDialog dialog;
		protected void onPreExecute(){
			dialog = new ProgressDialog(MyListclass.this);
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
					//Log.d("COVERLENGTH = ", String.format("Value = %d", o.getString("cover").trim().length()));
					//ARTISTS
			if(myflag == 1){	
					String cover = o.getString("cover").trim();
					if(cover.equals("mike.jpg")){
						int cover1 = R.drawable.mike;
						map.put("cover", cover1);
						String mike = "http://cs-server.usc.edu:12712/examples/servlets/mike.jpg";
						map.put("cover_ar", mike);
						
					}
					else{
						map.put("cover", cover);
						map.put("cover_ar", cover);
					}
					String name = o.getString("name").trim().replace("&amp;", "&");
					String genre = o.getString("genre").trim();
					String year = o.getString("year").trim();
					String details = o.getString("details").trim();
					Log.d("COVER = ", cover);
					Log.d("NAME = ", "Name="+ name);
					Log.d("GENRE = ", "Genre="+ genre);
					Log.d("YEAR = ", "Year =" + year);
					//map.put("cover", cover);
					map.put("name",  name);
					map.put("genre",  genre);
					map.put("year",  year);
					map.put("details", details);
					
					map.put("dname",  "Name : " + name);
					map.put("dgenre",  "Genre : " + genre);
					map.put("dyear",  "Year : "  + year);
					
					
					all_list.add(map);
				}
			//ALBUMS
			if(myflag == 2){	
				String cover = o.getString("cover").trim();
				if(cover.equals("album.jpg")){
					int cover1 = R.drawable.album;
					map.put("cover", cover1);
					String album = "http://cs-server.usc.edu:12712/examples/servlets/album.jpg";
					map.put("cover_al", album);
					
				}
				else{
					map.put("cover", cover);
					map.put("cover_al", cover);
				}
				String title = o.getString("title").trim().replace("&amp;", "&");
				String artist = o.getString("artist").trim().replace("&amp;", "&");
				String genre = o.getString("genre").trim();
				String year = o.getString("year").trim();
				String details = o.getString("details").trim();
				
				Log.d("COVER = ", cover);
				Log.d("TITLE = ", "title="+ title );
				Log.d("ARTIST = ", "artist="+ artist );
				Log.d("GENRE = ", "Genre="+ genre);
				Log.d("YEAR = ", "Year =" + year);
				//map.put("cover", cover);
				map.put("dtitle", "Title : " + title);
				map.put("dartist", "Artist : " + artist);
				map.put("dgenre", "Genre : " + genre);
				map.put("dyear", "Year : " + year);
				
				map.put("title", title);
				map.put("artist", artist);
				map.put("genre", genre);
				map.put("year", year);
				map.put("details", details);
				all_list.add(map);
			}
			//SONGS
			if(myflag == 3){	
				sample = o.getString("sample").trim();
				int sample1 = R.drawable.headphone;
				map.put("sample", sample1);
				String headphone = "http://cs-server.usc.edu:12712/examples/servlets/headphone.jpg";
				map.put("sample_s", headphone);
				map.put("song",sample);
				String title = o.getString("title").trim().replace("&amp;", "&");
				String performer = o.getString("performer").trim().replace("&amp;", "&");
				String composer = o.getString("composer").trim().replace("&amp;", "&");
				String details = o.getString("details").trim();
				Log.d("TITLE = ", "title="+ title );
				Log.d("PERFORMER = ", "Performer="+ performer);
				Log.d("COMPOSER = ", "Composer =" + composer);
				//map.put("cover", cover);
				map.put("dtitle", "Title : " + title);
				map.put("dperformer", "Performer : " + performer);
				map.put("dcomposer", "Composer : " + composer);
				
				map.put("title",   title);
				map.put("performer",   performer);
				map.put("composer",  composer);
				map.put("details", details);
				all_list.add(map);
			}
			
			
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
		
		@SuppressWarnings("unchecked")
		@Override
		
		/*follow the code from that link and shift the list adapter setting up in the above function..and perform the checks there*/
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			dialog.dismiss();
			
			
			//Artists
			if(myflag == 1){
					String[] header = {"cover","dname","dgenre","dyear"};
					int[] content = {R.id.ivCover,R.id.tvName,R.id.tvGenre,R.id.tvYear};
					
					SimpleAdapter adapter = new SimpleAdapter(MyListclass.this, all_list, R.layout.image_list_ar, header, content);
					setListAdapter(adapter);
					Log.d("ADAPTER COUNT = ", String.format("Value = %d",adapter.getCount()));
				
					/*Use adapter count to check for no results found*/
					for(int j = 0; j < adapter.getCount(); j++){
						HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(j);
						
						Object mytype = hm.get("cover");
						hm.put("position", j);
		                
						if(mytype instanceof java.lang.String){
							String imgUrl = (String)hm.get("cover");
							
							ImageLoaderTask imageLoaderTask = new ImageLoaderTask();
							
							hm.put("cover",imgUrl);
			                imageLoaderTask.execute(hm);
					}
						lv.setOnItemClickListener(new OnItemClickListener(){

							
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int pos, long id) {
								// TODO Auto-generated method stub
								 HashMap<String, Object> o = (HashMap<String, Object>) lv.getItemAtPosition(pos);

								 //Toast.makeText(MyListclass.this, "POSITION '" + o.get("position") + "' was clicked.", Toast.LENGTH_LONG).show();
								 n_ar = o.get("name").toString();
								 y_ar = o.get("year").toString();
								 g_ar = o.get("genre").toString();
								 d_ar = o.get("details").toString();
								 c_ar = o.get("cover_ar").toString();
								 
								 AlertDialog.Builder alert = new AlertDialog.Builder(MyListclass.this);
								 alert.setTitle("Post to Facebook");
								 alert.setPositiveButton("Facebook", new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										if(fl){
										publishFeedDialog(myflag);
										}
										else{
											Toast.makeText(MyListclass.this, "HEY!! Log in to post on facebook", Toast.LENGTH_LONG).show();
										}
									}
									 
								 }).create().show();
								
							}
							
						});
				}
					
			}
			
			
			//Albums
			if(myflag == 2){
				String[] header = {"cover","dtitle","dartist","dgenre","dyear"};
				int[] content = {R.id.ivCover,R.id.tvTitle,R.id.tvArtist,R.id.tvGenre,R.id.tvYear};
				
				SimpleAdapter adapter = new SimpleAdapter(MyListclass.this, all_list, R.layout.image_list_al, header, content);
				setListAdapter(adapter);
				Log.d("ADAPTER COUNT = ", String.format("Value = %d",adapter.getCount()));
				
				for(int j = 0; j < adapter.getCount(); j++){
					HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(j);
					hm.put("position", j);
					Object mytype = hm.get("cover");

					if(mytype instanceof java.lang.String){
						String imgUrl = (String)hm.get("cover");
						ImageLoaderTask imageLoaderTask = new ImageLoaderTask();
						//HashMap<String, Object> hmDownload = new HashMap<String, Object>();
		                hm.put("cover",imgUrl);
		                imageLoaderTask.execute(hm);
		                
		                lv.setOnItemClickListener(new OnItemClickListener(){

							
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int pos, long id) {
								// TODO Auto-generated method stub
								 HashMap<String, Object> o = (HashMap<String, Object>) lv.getItemAtPosition(pos);

								// Toast.makeText(MyListclass.this, "POSITION '" + o.get("position") + "' was clicked.", Toast.LENGTH_LONG).show();
								 t_al = o.get("title").toString();
								 y_al = o.get("year").toString();
								 g_al = o.get("genre").toString();
								 d_al = o.get("details").toString();
								 c_al = o.get("cover_al").toString();
								 a_al = o.get("artist").toString();
								 
								 AlertDialog.Builder alert = new AlertDialog.Builder(MyListclass.this);
								 alert.setTitle("Post to Facebook");
								 alert.setPositiveButton("Facebook", new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										if(fl){
											publishFeedDialog(myflag);
											}
											else{
												Toast.makeText(MyListclass.this, "HEY!! Log in to post on facebook", Toast.LENGTH_LONG).show();
											}
									}
									 
								 }).create().show();
								 
								 
							}
							
						});

		                
				}
			}
		}
			
			
		//Songs	
			if(myflag == 3){
				String[] header = {"sample","dtitle","dperformer","dcomposer"};
				int[] content = {R.id.ivSample,R.id.tvTitleSong,R.id.tvPerformer,R.id.tvComposer};
				
				SimpleAdapter adapter = new SimpleAdapter(MyListclass.this, all_list, R.layout.image_list_so, header, content);
				setListAdapter(adapter);
				Log.d("ADAPTER COUNT = ", String.format("Value = %d",adapter.getCount()));
				
				for(int j = 0; j < adapter.getCount(); j++){
					HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(j);
					hm.put("position", j);

	            
                lv.setOnItemClickListener(new OnItemClickListener(){

					
					@Override
					public void onItemClick(AdapterView<?> parent,
							View view, int pos, long id) {
						// TODO Auto-generated method stub
						 HashMap<String, Object> o = (HashMap<String, Object>) lv.getItemAtPosition(pos);

						// Toast.makeText(MyListclass.this, "POSITION '" + o.get("position") + "' was clicked.", Toast.LENGTH_LONG).show();
						 t_s = o.get("title").toString();
						 p_s = o.get("performer").toString();
						 com_s = o.get("composer").toString();
						 d_s = o.get("details").toString();
						 pic_s = o.get("sample_s").toString();
						 samUrl = o.get("song").toString();
						 
						 AlertDialog.Builder alert = new AlertDialog.Builder(MyListclass.this);
						 alert.setTitle("Post to Facebook");
						 alert.setPositiveButton("Facebook", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								if(fl){
									publishFeedDialog(myflag);
									}
									else{
										Toast.makeText(MyListclass.this, "HEY!! Log in to post on facebook", Toast.LENGTH_LONG).show();
									}
							}
							 
						 }).setNeutralButton("Play Sample", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								if(samUrl.equals("headphone.jpg")){
									Toast.makeText(MyListclass.this, "There is no song to play for this entry", Toast.LENGTH_LONG).show();
								}else{
									
									
									mp = new MediaPlayer();
									Log.d("SONG SAMPLE = ", samUrl);
									
									Uri playurl = Uri.parse(samUrl);
									try {
										Log.d("I am trying to play1", samUrl);
										mp.setDataSource(MyListclass.this,playurl);
										mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
										mp.prepare();
										
										
									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (SecurityException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalStateException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} finally{
										mp.start();
										
									}
									
									//dialog box to stop music

									 AlertDialog.Builder newalert = new AlertDialog.Builder(MyListclass.this);
									 newalert.setTitle("Stop the music");
									 newalert.setNegativeButton("Stop", new DialogInterface.OnClickListener(){

										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											if (mp.isPlaying()) {
							                    mp.stop();
							                    mp.release();
											}
										
										} 
									 }).create().show();	
								}
						 }
						}).create().show();
						
					}
					
				});

				}
			}
		}
	}


	/*The following class code has been referenced from : 
	 * http://wptrafficanalyzer.in/blog/android-lazy-loading-images-and-text-in-listview-from-http-json-data/*/
	
	private class ImageLoaderTask extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>>{
		 
        @Override
        protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hm) {
 
            InputStream inStr=null;
            String imgUrl = (String) hm[0].get("cover");
            int position = (Integer) hm[0].get("position");
 
            URL url;
            try {
                url = new URL(imgUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                inStr = urlConnection.getInputStream();
                File cacheDir = getBaseContext().getCacheDir();
                File tmpFile = new File(cacheDir.getPath() + "/listimg_"+position+".png");
                FileOutputStream fOutStream = new FileOutputStream(tmpFile);
                Bitmap b = BitmapFactory.decodeStream(inStr);
                b.compress(Bitmap.CompressFormat.PNG,100, fOutStream);
                fOutStream.flush();
                fOutStream.close();
 
                // Create a hashmap object to store image path and its position in the listview
                HashMap<String, Object> hmBitmap = new HashMap<String, Object>();
                hmBitmap.put("cover",tmpFile.getPath());
                hmBitmap.put("position",position);
                return hmBitmap;
 
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
 
        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            String path = (String) result.get("cover");
            int position = (Integer) result.get("position");
            SimpleAdapter adapter = (SimpleAdapter)getListAdapter();
            @SuppressWarnings("unchecked")
			HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(position);
            hm.put("cover",path);
 
            // Notifying listview about the dataset changes
            adapter.notifyDataSetChanged();
        }
    }
	

	@Override
	public void onResume() {
	    super.onResume();
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }

	    uiHelper.onResume();
	}
	   

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	    	fl = true;
	    } else if (state.isClosed()) {
	    	fl = false;
	    }
	}
	

	
	
	
}
	
	

