package com.cs5248.androiddashplayer;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	public final String URI = "http://pilatus.d1.comp.nus.edu.sg/~a0110280/upload/playlist.xml";

	static class CustomArrayAdapter<T> extends ArrayAdapter<T>
	{
	    public CustomArrayAdapter(Context ctx, T [] objects)
	    {
	        super(ctx, android.R.layout.simple_spinner_item, objects);
	    }

	    //other constructors

	    @Override
	    public View getDropDownView(int position, View convertView, ViewGroup parent)
	    {
	        View view = super.getView(position, convertView, parent);

	        //we know that simple_spinner_item has android.R.id.text1 TextView:         

	        /* if(isDroidX) {*/
	            TextView text = (TextView)view.findViewById(android.R.id.text1);
	            text.setTextColor(Color.RED);//choose your color :)         
	        /*}*/

	        return view;

	    }
	}
	

	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ApplicationState appState = (ApplicationState)getApplicationContext();
        appState.initVideo();

        final Spinner mpdListDropDown = (Spinner) findViewById(R.id.mpdList);
        DisplayVideoPlayLists vp = new DisplayVideoPlayLists(URI, getApplicationContext());
        vp.execute();
        
        while (appState.getItemsArray() == null)
        {
        	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        
        
        CustomArrayAdapter<String> adapter = new CustomArrayAdapter<String>(getApplicationContext(), appState.getItemsArray());
        mpdListDropDown.setAdapter(adapter);

        
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				appState.initVideo();
				downloadVideo();
				playVideo();
			}

			private void downloadVideo() 
			{
				String mpdUrl = String.valueOf(mpdListDropDown.getSelectedItem());
				Log.d("DASHPlayer", "The url is "+ mpdUrl);
				DownloadVideo pv = new DownloadVideo(mpdUrl, getApplicationContext());
				pv.execute();
			}
			private void playVideo()
			{
				Intent intent = new Intent(MainActivity.this, StartVideo.class);
				startActivity(intent);				
			}
		});
    }
}
