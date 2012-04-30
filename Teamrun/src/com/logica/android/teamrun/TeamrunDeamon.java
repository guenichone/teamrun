package com.logica.android.teamrun;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
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
	
	private static FileOutputStream storage = null;
	private static final String STORAGE_SEPARATOR = ":";
	
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
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
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO When someone want to bind with this service ...
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("Deamon : ", "Started.");
		
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
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }
    
    // NOTIFICATION
    
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        // CharSequence text = getText(R.string.local_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_sprinter, "test", System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, TeamrunActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this,"service", "test2", contentIntent);

        // Send the notification.
        notificationManager.notify(NOTIFICATION, notification);
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
