package com.cs5248.androiddashplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	public final String URI = "http://pilatus.d1.comp.nus.edu.sg/~a0110280/upload/playlist.xml";

	// Had to declare a custom adapter to ensure that the colour of the text is black. 
	// Otherwise it was merging with the background of the app
	static class CustomArrayAdapter<T> extends ArrayAdapter<T>
	{
	    public CustomArrayAdapter(Context ctx, T [] objects)
	    {
	        super(ctx, android.R.layout.simple_spinner_item, objects);
	    }

	    @Override
	    public View getDropDownView(int position, View convertView, ViewGroup parent)
	    {
	        View view = super.getView(position, convertView, parent);

            TextView text = (TextView)view.findViewById(android.R.id.text1);
            text.setTextColor(Color.BLACK);//choose your color :)         

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

        // The listener ensures that the selected item is also black
        mpdListDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id) {
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        
        // Start the Async Task to get the playlist
        DisplayVideoPlayLists vp = new DisplayVideoPlayLists(URI, getApplicationContext());
        vp.execute();
        
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				// Call the initVideo each time a new video is to be played
				appState.initVideo();
				
				// downloadVideo just starts an AsyncTask and returns
				downloadVideo();
				
				playVideo();
			}

			// Start an AsyncTask to download the videos 
			private void downloadVideo()
			{
				String mpdUrl = String.valueOf(mpdListDropDown.getSelectedItem());
				Log.d("DASHPlayer", "The url is "+ mpdUrl);
				DownloadVideo pv = new DownloadVideo(mpdUrl, getApplicationContext());
				pv.execute();
			}
			
			// Start a new activity which plays the video
			private void playVideo()
			{
				Intent intent = new Intent(MainActivity.this, StartVideo.class);
				startActivity(intent);
			}
		});
        
        // appState.getItemsArray is populated by the contents of the playlist in an 
        // AsyncTask. Wait here till the playlist is updated
        while (appState.getItemsArray() == null)
        {
        	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        // Update the spinner with the list just downloaded
        
        CustomArrayAdapter<String> adapter = new CustomArrayAdapter<String>(getApplicationContext(), appState.getItemsArray());
        mpdListDropDown.setAdapter(adapter);
    }
}
