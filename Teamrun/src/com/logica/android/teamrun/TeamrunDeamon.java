package com.logica.android.teamrun;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

/**
 * 
 * @author Logica Teamrun.
 */
public class TeamrunDeamon extends Service {

	private static FileOutputStream storage = null;
	private static final String STORAGE_SEPARATOR = ":";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO When someone want to bind with this service ...
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// S
		
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
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
