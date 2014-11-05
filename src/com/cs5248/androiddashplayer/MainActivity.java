package com.cs5248.androiddashplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;


public class MainActivity extends Activity {
	
	public final String URI = "http://pilatus.d1.comp.nus.edu.sg/~a0110280/upload/playlist.xml";

	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final Spinner mpdListDropDown = (Spinner) findViewById(R.id.mpdList);
        DisplayVideoPlayLists vp = new DisplayVideoPlayLists(mpdListDropDown, URI, getApplicationContext());
        vp.execute();
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
		        ApplicationState appState = (ApplicationState)getApplicationContext();
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
