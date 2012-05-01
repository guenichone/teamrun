package com.logica.android.teamrun;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Logica Teamrun Deamon.
 * @author Logica Teamrun.
 */
public class TeamrunDeamon extends Service {

	private int NOTIFICATION = R.string.local_service_started;
	private NotificationManager notificationManager;
	
	private String url = null;
	private String team = null;
	
	private static FileOutputStream storage = null;
	private static final String STORAGE_SEPARATOR = ":";
	
    /**
     * Class for clients to access.
     */
    public class LocalBinder extends Binder {
    	TeamrunDeamon getService() {
            return TeamrunDeamon.this;
        }
    }
	
    @Override
    public void onCreate() {
    	notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	Log.v("Deamon : ", "Created.");
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }
	
    // This is the object that receives interactions from clients.
    private final IBinder mBinder = new LocalBinder();
    
	@Override
	public IBinder onBind(Intent arg0) {
		Log.v("Deamon : ", "Binded.");
		return mBinder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("Deamon : ", "Started.");
		
		initLocationListener();
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
    @Override
    public void onDestroy() {
    	Log.v("Deamon : ", "Stopped.");
    	
        // Cancel the persistent notification.
    	notificationManager.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(getBaseContext(), R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }
    
    // NOTIFICATION
    
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        // CharSequence text = getText(R.string.local_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_sprinter, "Teamrun Deamon Launched.", System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, TeamrunActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "Teamrun", "Teamrun Deamon.", contentIntent);

        // Send the notification.
        notificationManager.notify(NOTIFICATION, notification);
    }
    
    // LOCATION
    
    /**
     * Set the url to send data.
     * @param url The url destination of data.
     */
    public void setUrl(String url) {
    	this.url = url;
    }
    
    /**
     * Set the team used for data.
     * @param team The team used for sending data.
     */
    public void setTeam(String team) {
    	this.team = team;
    }
    
    /**
     * 
     */
    private void initLocationListener() {
		final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if (url != null && team != null) {
					sentLocation(url, team, location.getLatitude(), location.getLongitude());
				}
			}
	
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
	
			public void onProviderEnabled(String provider) {
			}
	
			public void onProviderDisabled(String provider) {
			}
		};
		
		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,	0, locationListener);
    }
    
    /**
     * Call server URL with correct parameters.
     * @param url The url of the server (without "http://")
     * @param team The team number.
     * @param latitude The latitude.
     * @param longitude The longitude.
     */
    private void sentLocation(String url, String team, double latitude, double longitude) {
		try {
			HttpGet httpGet = new HttpGet("http://" + url + "/SavePosition?teamNumber=" + team + "&latitude=" + latitude + "&longitude" + longitude);
			HttpClient httpclient = new DefaultHttpClient();
			
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);
			// Check response status
			int statusCode = response.getStatusLine().getStatusCode(); 
			if (statusCode != 200) {
				Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
			}
        } catch (Exception e) {
        	Toast.makeText(getApplicationContext(), "connection failed.", Toast.LENGTH_LONG).show();
			Log.v("testConnection : ", e.toString());
		}
    }
    
    // STORAGE
    
	/**
	 * 
	 * @throws IOException
	 */
	private void openStorage() throws IOException {
		String FILENAME = "dataTeamRunFile";
		storage = openFileOutput(FILENAME, Context.MODE_PRIVATE);
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void closeStorage() throws IOException {
		storage.close();
	}
	
 	/**
	 * 
	 * @param location
	 * @throws IOException 
	 */
	private void storeLocation(Location location) throws IOException {
		// Defining storage sequence
		StringBuffer buffer = new StringBuffer();
		buffer.append(System.currentTimeMillis());
		buffer.append(STORAGE_SEPARATOR);
		buffer.append(location.getLatitude());
		buffer.append(STORAGE_SEPARATOR);
		buffer.append(location.getLongitude());
		buffer.append(STORAGE_SEPARATOR);
		buffer.append(location.getAltitude());
		
		// Writing
		storage.write(buffer.toString().getBytes());
	}
}
