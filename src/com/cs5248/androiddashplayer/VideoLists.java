package com.cs5248.androiddashplayer;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

import ch.boye.httpclientandroidlib.client.methods.HttpGet;

public class VideoLists 
{
	
	public class VideoForLater
	{
		private String videoName;
		private HttpGet videoHttpGet;
		public String getVideoName() {
			return videoName;
		}
		public void setVideoName(String videoName) {
			this.videoName = videoName;
		}
		public HttpGet getVideoHttpGet() {
			return videoHttpGet;
		}
		public void setVideoHttpGet(HttpGet videoHttpGet) {
			this.videoHttpGet = videoHttpGet;
		}
		public VideoForLater(String name, HttpGet hg)
		{
			this.videoHttpGet = hg;
			this.videoName = name;
		}
	}
	
	private static final String ns = null;
	
	private final int LOW = 0;
	private final int MED = 1;
	private final int HIGH = 2;

	private String baseUrl;
	private ArrayList<VideoForLater>[] videoLists;
	private int presentQuality;
	private int qualityForPlay;
	
	@SuppressWarnings("unchecked")
	public VideoLists()
	{
		baseUrl = "";
		videoLists = new ArrayList[3];
		for (int i=0;i<3;i++)
		{
			videoLists[i] = new ArrayList<VideoForLater>();
		}
	}
	
	public int getTotalNumberOfVideos()
	{
		return videoLists[1].size();
	}
	
	public void setInitialQuality()
	{
		qualityForPlay = MED;
	}
	
	public void increaseQuality()
	{
		qualityForPlay = qualityForPlay >= 2 ? 2 : qualityForPlay+1;
	}
	
	public void decreaseQuality()
	{
		qualityForPlay = qualityForPlay <= 0 ? 0 : qualityForPlay-1;
	}
	
	public void getDataIntoVideoListsFromParser(XmlPullParser parser) 
	{
		try
		{
	        parser.nextTag();
	        readParser(parser);			
		}
		catch(Exception e)
		{
			Log.i("DashPlayer", "At getDataIntoVideoListsFromParser, the exception is "+ e.getMessage());
		}
	}

	private void readParser(XmlPullParser parser) 
	{
		try
		{
			parser.require(XmlPullParser.START_TAG, ns, "MPD");
		    while ((parser.next() != XmlPullParser.END_TAG || (parser.getName().compareToIgnoreCase("MPD") != 0))) 
		    {
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        String name = parser.getName();
		        // Starts by looking for the entry tag
		        if (name.equals("BaseURL")) 
		        {	        	
		        	getBaseUrl(parser);
		        }
		        else if (name.equals("Representation"))
		        {
		        	setPresentQuality(parser);
		        }
		        else if (name.equals("Url"))
		        {
		        	setVideoSegmentName(parser);
		        }
		    }  
		}
		catch (Exception e)
		{
			Log.i("DashPlayer", "The exception at readParser is " + e.getMessage());
		}
	}

	private void setVideoSegmentName(XmlPullParser parser) 
	{
		try
		{
		    parser.require(XmlPullParser.START_TAG, ns, "Url");
	        String videoUrl = parser.getAttributeValue(ns, "sourceUrl");
	        String fullUrl = baseUrl + videoUrl;
	        Log.d("DASHPlayer", "The url of the segment being added is " + fullUrl);
	        this.videoLists[presentQuality].add(new VideoForLater(videoUrl, new HttpGet(fullUrl))); 
	        parser.nextTag();
		}
		catch (Exception e)
		{
			Log.i("DashPlayer", "The exception at setVideoSegmentName is " + e.getMessage());
		}				
	}

	private void setPresentQuality(XmlPullParser parser) 
	{
		try
		{
		    parser.require(XmlPullParser.START_TAG, ns, "Representation");
	        String qualityType = parser.getAttributeValue(ns, "id");
	        Log.d("DASHPlayer", "The quality to be processed now is "+ qualityType);
	        if (qualityType.compareToIgnoreCase("Low") == 0)
	        {
	        	this.presentQuality = LOW;
	        }
	        else if (qualityType.compareToIgnoreCase("Medium") == 0)
	        {
	        	this.presentQuality = MED;
	        }
	        else if (qualityType.compareToIgnoreCase("High") == 0)
	        {
	        	this.presentQuality = HIGH;
	        }
	        parser.nextTag();
		}
		catch (Exception e)
		{
			Log.i("DashPlayer", "The exception at setPresentQuality is " + e.getMessage());
		}		
	}

	private void getBaseUrl(XmlPullParser parser) 
	{
		try
		{
		    parser.require(XmlPullParser.START_TAG, ns, "BaseURL");
		    if (parser.next() == XmlPullParser.TEXT) {
		        baseUrl = parser.getText();
		        parser.nextTag();
		    }
		    parser.require(XmlPullParser.END_TAG, ns, "BaseURL");
		}
		catch (Exception e)
		{
			Log.i("DashPlayer", "The exception at getBaseUrl is " + e.getMessage());
		}
	}

	public VideoForLater getVideoUrl(int i) 
	{
		return videoLists[qualityForPlay].get(i);
	}
}
