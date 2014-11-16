package com.cs5248.androiddashplayer;

import java.util.LinkedList;

import android.app.Application;
import android.util.Log;

// This is the main application class. It maintains the global state of the app which is 
// available to all components of the application. 
public class ApplicationState extends Application
{
	// List of all videos downloaded
	private LinkedList<String> videoBuffer;
	
	private int numberOfVideos;
	private int numberPlayed;
	
	// List of all the videos available
	private String[] itemsArray;
	
	public String[] getItemsArray() {
		return itemsArray;
	}

	public void setItemsArray(String[] itemsArray) {
		this.itemsArray = itemsArray;
	}

	// is true only when videos are available in the buffer
	private boolean canPlay;
	
	@Override
	public void onCreate()
	{
		initVideo();
		Log.i("DashPlayer", "The create method has been called and the buffer is ready");
	}
	
	public void initVideo()
	{
		itemsArray = null;
		canPlay = false;
		videoBuffer = new LinkedList<String>();
		numberOfVideos = 0;
		numberPlayed = 0;
	}
	
	public int getBufferSize()
	{
		return videoBuffer.size();
	}
	
	public void addToBuffer(String nextVideo)
	{
		canPlay = true;
		videoBuffer.add(nextVideo);
	}
	
	public int getNumberOfVideosLeft()
	{
		int numberLeft = numberOfVideos - numberPlayed;
		return numberLeft;
	}
	
	public String getNextVideoToPlay()
	{
		// Only get the next video 
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
