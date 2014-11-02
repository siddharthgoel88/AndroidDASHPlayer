package com.cs5248.androiddashplayer;

import java.util.LinkedList;

import android.app.Application;
import android.util.Log;

public class ApplicationState extends Application
{
	private LinkedList<String> videoBuffer;
	private int numberOfVideos;
	private int videosDownloaded;
	private int numberPlayed;
	
	private boolean canPlay;
	
	@Override
	public void onCreate()
	{
		initVideo();
		Log.i("DashPlayer", "The create method has been called and the buffer is ready");
	}
	
	public void initVideo()
	{
		canPlay = false;
		videoBuffer = new LinkedList<String>();
		numberOfVideos = 0;
		videosDownloaded = 0;
		numberPlayed = 0;
	}
	
	public int getBufferSize()
	{
		return videoBuffer.size();
	}
	
	public void addToBuffer(String nextVideo)
	{
		canPlay = true;
		videosDownloaded++;
		videoBuffer.add(nextVideo);
	}
	
	public int getNumberOfVideosLeft()
	{
		int numberLeft = numberOfVideos - numberPlayed;
		return numberLeft;
	}
	
	public String getNextVideoToPlay()
	{
		while(!canPlay)
		{
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String path = videoBuffer.poll();
		if (path !=null)
			numberPlayed++;
		Log.i("DASHPlayer", "The string returned from getNextVideoToPlay is "+path);
		return path;
	}

	public int getNumberOfVideos() {
		return numberOfVideos;
	}

	public void setNumberOfVideos(int numberOfVideos) 
	{
		this.numberOfVideos = numberOfVideos;
	}	
}
