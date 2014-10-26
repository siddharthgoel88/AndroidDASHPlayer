package com.cs5248.androiddashplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

public class StartVideo extends Activity implements 
OnCompletionListener, SurfaceHolder.Callback {
	private static final String TAG = "DASHPlayer";
    private MediaPlayer currentMediaPlayer;
    private MediaPlayer preparedMediaPlayer;
    private MediaPlayer tempMediaPlayer;
    private SurfaceView currentView;
    private SurfaceView preparedView;
    private SurfaceView tempView;
    private SurfaceHolder currentHolder;
    private SurfaceHolder preparedHolder;
    private SurfaceHolder tempHolder;
    private RelativeLayout surfaceViewList;
    private String path;

    /**
     * 
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        currentMediaPlayer = null;
        preparedMediaPlayer = null;
        currentView = null;
        preparedView = null;
        currentView = null;
        preparedView = null;
        currentHolder = null;
        preparedHolder = null;
        
        setContentView(R.layout.mediaplayer);
        surfaceViewList = (RelativeLayout) findViewById(R.id.surfaceViewList);
        path = Environment.getExternalStorageDirectory().getPath() + "/DASHRecorder/video/DASH_Video_06_10_2014_01_03_03.mp4";
        prepareNext();
    }

	private void prepareNext() {
		if (currentView == null) {
			//currentView = (SurfaceView) findViewById(R.id.streamingSurface);
			currentView = (SurfaceView) new SurfaceView(getApplicationContext());
			currentHolder = currentView.getHolder();
			surfaceViewList.addView(currentView);
			currentHolder.addCallback(new SurfaceHolder.Callback() {
				
				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {
					Log.d(TAG, "surfaceDestroyed of first surface called");
				}
				
				@Override
				public void surfaceCreated(SurfaceHolder holder) {
					Log.d(TAG, "surfaceCreated of first surface called");
					try {
						currentMediaPlayer = new MediaPlayer();
						currentMediaPlayer.setDataSource(path);
						currentMediaPlayer.setSurface(currentHolder.getSurface());
						//currentHolder.setFixedSize(currentMediaPlayer.getVideoWidth(), currentMediaPlayer.getVideoHeight());
						currentHolder.setFixedSize(480, 640);
						currentMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							
							@Override
							public void onCompletion(MediaPlayer mp) {
								mp.release();
								playNext();
							}
						});
						
						currentMediaPlayer.prepare();
						currentMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
							
							@Override
							public void onPrepared(MediaPlayer mp) {
								currentMediaPlayer.start();
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
			preparedView = (SurfaceView) new SurfaceView(getApplicationContext());
			preparedHolder = preparedView.getHolder();
			preparedHolder.addCallback(this);
			surfaceViewList.addView(preparedView, 0);
		}
	}
	
	private void playNext() {
		prepareDispose();
		
		currentHolder = preparedHolder;
		currentMediaPlayer = preparedMediaPlayer;
		currentView = preparedView;
		
		currentMediaPlayer.start();
		performDispose();
		
		prepareNext();
	}
	
	private void prepareDispose() {
		tempMediaPlayer = currentMediaPlayer;
		tempHolder = currentHolder;
		tempView = currentView;
	}
	
	private void performDispose() {
		tempHolder = null;
		tempMediaPlayer = null;
		surfaceViewList.removeView(tempView);
	}

    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion called");
        mp.release();
        playNext();
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.d(TAG, "surfaceChanged called");
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.d(TAG, "surfaceDestroyed called");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated called");
        try {
			preparedMediaPlayer = new MediaPlayer();
			preparedMediaPlayer.setDataSource(path);
			//preparedHolder.setFixedSize(preparedMediaPlayer.getVideoWidth(), preparedMediaPlayer.getVideoHeight());
			preparedHolder.setFixedSize(480, 640);
			preparedMediaPlayer.setOnCompletionListener(this);
			preparedMediaPlayer.setSurface(preparedHolder.getSurface());
			preparedMediaPlayer.prepare();
		} catch (Exception e) {
			Log.e(TAG, "error:" + e.getMessage(), e);
		}
    }
}