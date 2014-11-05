package com.cs5248.androiddashplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class StartVideo extends Activity implements 
OnCompletionListener, SurfaceHolder.Callback, OnPreparedListener {
	private static final String TAG = "DASHPlayer";
    private MediaPlayer currentMediaPlayer;
    private MediaPlayer preparedMediaPlayer;
    private SurfaceView currentView;
    private SurfaceView preparedView;
    private SurfaceView tempView;
    private SurfaceHolder currentHolder;
    private SurfaceHolder preparedHolder;
    private RelativeLayout surfaceViewList;
    private String path;
    private int counter;
    private ApplicationState appState;
    private boolean isPlaying;

    /**
     * 
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.i(TAG, "Reached onCreate of start video");
        
        isPlaying = false;
        currentMediaPlayer = null;
        preparedMediaPlayer = null;
        currentView = null;
        preparedView = null;
        currentView = null;
        preparedView = null;
        currentHolder = null;
        preparedHolder = null;
        counter = 0;
        appState = (ApplicationState)getApplicationContext();
        
        if (appState.getNumberOfVideos() >=3)
        {
        	while (appState.getBufferSize() <3)
        	{
        		try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        
        path = getNextVideoPath();
        setContentView(R.layout.mediaplayer);
        surfaceViewList = (RelativeLayout) findViewById(R.id.surfaceViewList);
        
        Button playPauseButton = (Button) findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isPlaying && (currentMediaPlayer != null)) {
					currentMediaPlayer.pause();
					isPlaying = false;
				} else if (!isPlaying && (currentMediaPlayer != null)) {
					currentMediaPlayer.start();
					isPlaying = true;
				}
			}
		});
        
        prepareNext();
    }

	private String getNextVideoPath() 
	{
        Log.i(TAG, "Trying to get next video");

		String videoPath = null;
		while (true)
		{
			videoPath = appState.getNextVideoToPlay();
			if (appState.getNumberOfVideosLeft() == 0 || videoPath != null)
			{
		        Log.i(TAG, "Exiting get next video");

				break;
			}
	        Log.i(TAG, "Waiting for the queue");

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        Log.i(TAG, "The video is " + videoPath);
		
		return videoPath;
	}

	private void prepareNext() {
        Log.i(TAG, "Entering prepareNext()");
		if (currentView == null) {
	        Log.i(TAG, "Entering if part of prepareNext()");

			//currentView = (SurfaceView) findViewById(R.id.streamingSurface);
			currentView = (SurfaceView) new SurfaceView(getApplicationContext());
			currentHolder = currentView.getHolder();
			surfaceViewList.addView(currentView, 0);
			currentHolder.addCallback(new SurfaceHolder.Callback() {
				
				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {
					Log.i(TAG, "surfaceDestroyed of first surface called");
				}
				
				@Override
				public void surfaceCreated(SurfaceHolder holder) {
					Log.i(TAG, "surfaceCreated of first surface called");
					try {
						currentMediaPlayer = new MediaPlayer();
						currentMediaPlayer.setDataSource(path);
						currentMediaPlayer.setSurface(currentHolder.getSurface());
						//currentHolder.setFixedSize(currentMediaPlayer.getVideoWidth(), currentMediaPlayer.getVideoHeight());
						//currentHolder.setFixedSize(480, 640);
						currentMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							
							@Override
							public void onCompletion(MediaPlayer mp) {
								counter++;
								mp.release();
								Log.i(TAG, "The video has finished playing now ");
								playNext();
							}
						});
						
						currentMediaPlayer.prepare();
						currentMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
							
							@Override
							public void onPrepared(MediaPlayer mp) {
								mp.start();
								isPlaying = true;
							}
						});
						
						prepareNext();
						
					} catch(Exception e) {
						Log.e(TAG, "error:" + e.getMessage(), e);
					}
					
				}
				
				@Override
				public void surfaceChanged(SurfaceHolder holder, int format, int width,
						int height) {
					Log.d(TAG, "surfaceChanged of first surface called");
				}
			});
		} else {
	        Log.i(TAG, "Entering else part of prepareNext()");
	        
	        if (appState.getNumberOfVideosLeft()>0)
	        {

				preparedView = (SurfaceView) new SurfaceView(getApplicationContext());
				preparedHolder = preparedView.getHolder();
				preparedHolder.addCallback(this);
				surfaceViewList.addView(preparedView, 0);
	        }
		}
	}
	
	private void playNext() {
        Log.i(TAG, "Entering playNext()");

		prepareDispose();
		
		currentHolder = preparedHolder;
		currentMediaPlayer = preparedMediaPlayer;
		currentView = preparedView;
		
		currentMediaPlayer.start();
		performDispose();
		
		prepareNext();
	}
	
	private void prepareDispose() {
        Log.i(TAG, "Entering prepareDispose()");

		tempView = currentView;
	}
	
	private void performDispose() {
        Log.i(TAG, "Entering performDispose()");

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			Log.d(TAG, "Sleep before surface removing of View List interuppted");
			e.printStackTrace();
		}
		surfaceViewList.removeView(tempView);
	}

    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "Entering onCompletion()");

    	counter++;
/*    	if (appState.getNumberOfVideos() - counter >=1)
    	{
    		while (appState.getBufferSize() <1)
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}*/
        Log.d(TAG, "onCompletion called");
        mp.release();
		Log.i(TAG, "The video has finished playing now for the later ones");
        if (appState.getNumberOfVideosLeft()>0)
        {
            playNext();
        }
        else
        {
        	prepareDispose();
        	performDispose();
        	finish();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.d(TAG, "surfaceChanged for later called");
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.d(TAG, "surfaceDestroyed for later called");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated for later called");
        try {
			preparedMediaPlayer = new MediaPlayer();
			preparedMediaPlayer.setDataSource(getNextVideoPath());
			//preparedHolder.setFixedSize(preparedMediaPlayer.getVideoWidth(), preparedMediaPlayer.getVideoHeight());
			//preparedHolder.setFixedSize(480, 640);
			preparedMediaPlayer.setOnCompletionListener(this);
			preparedMediaPlayer.setSurface(preparedHolder.getSurface());
			preparedMediaPlayer.prepare();
			preparedMediaPlayer.setOnPreparedListener(this);
			currentMediaPlayer.setNextMediaPlayer(preparedMediaPlayer);
		} catch (Exception e) {
			Log.e(TAG, "error:" + e.getMessage(), e);
		}
    }
    
    @Override
    public void onBackPressed() {
    	currentMediaPlayer.stop();
    	currentMediaPlayer.release();
    	prepareDispose();
    	performDispose();
    	finish();
    }

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
		mp.pause();
	}
}
