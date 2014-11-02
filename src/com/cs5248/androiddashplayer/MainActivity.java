package com.cs5248.androiddashplayer;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Application;


public class MainActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        ApplicationState appState = (ApplicationState)getApplicationContext();
		        appState.initVideo();
				downloadVideo();
				playVideo();
			}

			private void downloadVideo() 
			{
				EditText mpdEt = (EditText) findViewById(R.id.mpdUrl);
				String mpdUrl = mpdEt.getText().toString();
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
        
        Button videoButton = (Button) findViewById(R.id.videoButton);
        videoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, StartVideo.class);
				startActivity(intent);
			}
		});
    }
}
