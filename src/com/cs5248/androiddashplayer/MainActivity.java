package com.cs5248.androiddashplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
				playVideo();
			}

			private void playVideo() 
			{
				EditText mpdEt = (EditText) findViewById(R.id.mpdUrl);
				String mpdUrl = mpdEt.getText().toString();
				Log.d("DASHPlayer", "The url is "+ mpdUrl);
				PlayVideo pv = new PlayVideo(mpdUrl);
				pv.execute();
			}
		});
    }
}
