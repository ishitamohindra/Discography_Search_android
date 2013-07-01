package com.example.discographysearch;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
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
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class getDisco extends Activity implements OnClickListener  {
	Spinner mSpin;
	EditText mText;
	Button button;
	TextView httpStuff;
	String name,cat,myurl;
	HttpResponse r;
	String data;
	JSONObject timeline;
	JSONObject timeline1;
	JSONArray json_arr;
	int flag=0;
	String URL = "http://cs-server.usc.edu:12712/examples/servlet/HelloWorldExample?myname=";
	
	
	@TargetApi(9) @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getdisc);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build();
		StrictMode.setThreadPolicy(policy);
		mText = (EditText)findViewById(R.id.etName);
		mSpin = (Spinner)findViewById(R.id.spCat);
		httpStuff = (TextView) findViewById(R.id.tvhttp);
		button = (Button) findViewById(R.id.submit);
		button.setOnClickListener(this);
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		name =  mText.getText().toString();
		if(name.equals("")){
			Toast.makeText(getDisco.this, "STOP!!  Please enter some name first!",Toast.LENGTH_LONG).show();
		}
		
		else{
			
		
		cat = mSpin.getSelectedItem().toString();
		myurl = URL + name.replace(' ', '+') + "&mycategory=" + cat.toLowerCase();
		if(cat.toLowerCase().equals("artists")){
			flag = 1;
		}
		if(cat.toLowerCase().equals("albums")){
			flag = 2;
		}
		if(cat.toLowerCase().equals("songs")){
			flag = 3;
		}
		

				HttpGet get = new HttpGet(myurl);
				HttpClient client = new DefaultHttpClient();
				
				try {
					r = client.execute(get);
				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int status = r.getStatusLine().getStatusCode();
			 
				if(status == 200){
				
					HttpEntity e = r.getEntity();
					try {
						data = EntityUtils.toString(e);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						timeline = new JSONObject(data);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						timeline1 = timeline.getJSONObject("results");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						json_arr = timeline1.getJSONArray("result");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					if(json_arr.length()==0){
						 Toast.makeText(getDisco.this, "No Discography found!!", Toast.LENGTH_LONG).show();
					}
					else{
	
						Intent myintent;
						Bundle basket = new Bundle();
						basket.putString("key", myurl);
						basket.putInt("flag", flag);
						myintent = new Intent(getDisco.this, MyListclass.class);
						myintent.putExtras(basket);
						startActivity(myintent);
						
						}
		
	}
}

}

}