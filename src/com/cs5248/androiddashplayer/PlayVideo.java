package com.cs5248.androiddashplayer;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.client.HttpClientBuilder;

public class PlayVideo extends AsyncTask<Void, Integer, Void>
{
	private String mpdUrl;
	private VideoLists videoLists;
	
	private static final String ns = null;
	
	public PlayVideo(String mpdUrl)
	{
		this.mpdUrl = mpdUrl;
		videoLists = new VideoLists();
	}
	
	public void playVideo()
	{
		getXmlFileAndRetrieveIntoDS();
//		parseXMLIntoDS();
		
	}
	
	private void getXmlFileAndRetrieveIntoDS()
	{

		HttpGet uri = new HttpGet(mpdUrl);
	
		HttpClient httpClient = HttpClientBuilder.create().build();
		try
		{
			HttpResponse resp = null;
			resp = httpClient.execute(uri);
	
			StatusLine status = resp.getStatusLine();
			if (status.getStatusCode() != 200) {
			    Log.d("DASHPlayer", "HTTP error, invalid server status code: " + resp.getStatusLine());  
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(resp.getEntity().getContent(), null);

            videoLists.getDataIntoVideoListsFromParser(parser);
            
		}
		catch(Exception e)
		{
			Log.i("DASHPlayer", "Damn the program died with the error " + e.getMessage());
		}
	}


	@Override
	protected Void doInBackground(Void... params)
	{
		Log.i("DASHPlayer" , "Inside doInBackground");
		
		playVideo();
		return null;
	}
	
}
