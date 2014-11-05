package com.cs5248.androiddashplayer;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.HttpClientBuilder;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

public class DisplayVideoPlayLists extends AsyncTask<String[], Integer, Void>
{
	
	
	private static final String ns = null;
	private String displayUri;
	private ApplicationState appState;
	
	
	public DisplayVideoPlayLists(String uri, Context context)
	{
		this.appState = (ApplicationState)context.getApplicationContext();

		this.displayUri = uri;
	}
	
	private void getFileAndDisplay() 
	{
		HttpGet uri = new HttpGet(displayUri);
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		try
		{
			HttpResponse resp = null;
			resp = httpClient.execute(uri);
	
			StatusLine status = resp.getStatusLine();
			if (status.getStatusCode() != 200) 
			{
			    Log.d("DASHPlayer", "HTTP error, invalid server status code: " + resp.getStatusLine());  
			}
			XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            InputStream is = resp.getEntity().getContent();
            Log.i("DASHPlayer", is.toString());
            parser.setInput(is, null);
            
            ArrayList<String> items = getListsFromXML(parser);
            String[] itemsArray= new String[items.size()];
            for (int j=0;j<itemsArray.length;j++)
            	itemsArray[j] = items.get(j);
            
            appState.setItemsArray(itemsArray);

		}
		catch(Exception e)
		{
			Log.i("DASHPlayer", "Damn the program died with the error " + e.getMessage());
		}
	}
	private ArrayList<String> getListsFromXML(XmlPullParser parser) 
	{
		
		try
		{
	        parser.nextTag();
	        return readParser(parser);			
		}
		catch(Exception e)
		{
			Log.i("DashPlayer", "At getDataIntoVideoListsFromParser, the exception is "+ e.getMessage());
		}

		return null;
	}

	private ArrayList<String> readParser(XmlPullParser parser)
	{
		ArrayList<String> list = new ArrayList<String>();
		try
		{
			parser.require(XmlPullParser.START_TAG, ns, "playlist");
		    while ((parser.next() != XmlPullParser.END_TAG || (parser.getName().compareToIgnoreCase("playlist") != 0))) 
		    {
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        String name = parser.getName();
		        // Starts by looking for the entry tag
		        if (name.equals("mpd")) 
		        {
		        	addToList(list, parser);
		        }
		    }  
		}
		catch (Exception e)
		{
			Log.i("DashPlayer", "The exception at getListsFromXML is " + e.getMessage());
		}
		return list;
	}

	private void addToList(ArrayList<String> list, XmlPullParser parser)
	{
		
		try
		{
		    parser.require(XmlPullParser.START_TAG, ns, "mpd");
		    list.add(parser.getAttributeValue(ns, "path"));
	        parser.nextTag();
		}
		catch (Exception e)
		{
			Log.i("DashPlayer", "The exception at setPresentQuality is " + e.getMessage());
		}
	}

	protected Void doInBackground(String[]... params)
	{
		Log.i("DASHPlayer" , "Inside doInBackground of DisplayVideoPlayLists");
		
		getFileAndDisplay();
		return null;
	}
}
