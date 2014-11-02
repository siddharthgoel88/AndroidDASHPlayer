package com.cs5248.androiddashplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;

import com.cs5248.androiddashplayer.VideoLists.VideoForLater;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.client.HttpClientBuilder;

public class DownloadVideo extends AsyncTask<Void, Integer, Void>
{
	private String mpdUrl;
	private VideoLists videoLists;
	private Context appContext;
	
	private final String directory;
	
	private static final String ns = null;
	
	public DownloadVideo(String mpdUrl, Context context)
	{
		this.appContext = context;
		this.mpdUrl = mpdUrl;
		videoLists = new VideoLists();
		directory = Environment.getExternalStorageDirectory().getPath() + "/DashRecorder/";
		File dir = new File(directory);
		if (!(dir.exists()) || !(dir.isDirectory()))
		{
			dir.mkdir();
		}
	}
	
	public void playVideo()
	{
		getXmlFileAndRetrieveIntoDS();
		getVideoAndAddToBuffer();
		
	}
	
	private void getVideoAndAddToBuffer() 
	{
		int noOfVideos = videoLists.getTotalNumberOfVideos();
		
		ApplicationState appState = (ApplicationState)appContext.getApplicationContext();
		appState.setNumberOfVideos(noOfVideos);
		
		for (int i=0;i<noOfVideos;i++)
		{
			setVideoQualityToDownload(i, appState);
			VideoForLater videoToDownloadStruct = videoLists.getVideoUrl(i); 
			HttpGet videoToDownload = videoToDownloadStruct.getVideoHttpGet();
			HttpClient httpClient = HttpClientBuilder.create().build();
			try
			{
				HttpResponse resp = null;
				resp = httpClient.execute(videoToDownload);
				
				StatusLine status = resp.getStatusLine();
				if (status.getStatusCode() !=200)
				{
					Log.d("DASHPlayer", "HTTP error, invalid server status code: " + resp.getStatusLine());
				}
				if (resp.getEntity()!=null)
				{
					String videoName = this.directory+videoToDownloadStruct.getVideoName();
					Log.i("DashPlayer", "Downloading the video and saving it to "+videoName);
					File newFile = new File(videoName);
					File dir = new File(newFile.getParent());
					if (!(dir.exists() || !(dir.isDirectory())))
					{
						dir.mkdir();
					}
					FileOutputStream fos = new FileOutputStream(videoName);
					resp.getEntity().writeTo(fos);
					fos.close();
					appState.addToBuffer(videoName);
					Log.i("DashPlayer", "Downlod of video "+ videoName + "is complete");
				}
			}
			catch(Exception e)
			{
				Log.i("DASHPlayer", "Damn the program died while getting the video with the error " + e.getMessage());
			}
		}
	}

	private void setVideoQualityToDownload(int i, ApplicationState appState) 
	{
		if (i<4)
		{
			videoLists.setInitialQuality();
			return;
		}
		while (appState.getBufferSize() > 10)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (appState.getBufferSize()>5)
			videoLists.increaseQuality();
		else if (appState.getBufferSize()<3)
		{
			videoLists.decreaseQuality();
		}
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
