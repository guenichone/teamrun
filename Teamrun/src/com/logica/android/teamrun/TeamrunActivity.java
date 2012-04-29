package com.logica.android.teamrun;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TeamrunActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Bind buttons
		Button testButton = (Button) findViewById(R.id.testButton);
		testButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		
		Button deamonButton = (Button) findViewById(R.id.deamonButton);
		deamonButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		
		// Init locationListener
		initLocationListener();
	}
	
	@Override
	public void onStop() {
		
	}
	
	// BUTTONS METHODS
	private void toggleRunDeamon() {
		TeamrunDeamon deamon = new TeamrunDeamon();
		
	}


	private void initLocationListener() {
		// Acquire a reference to the system Location Manager
		final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		//locationManager.getGpsStatus(null).
		
		// ProviderStatus textbox
		final EditText statusEditText = (EditText) findViewById(R.id.LocationStatus);
		statusEditText.setText("gps : Waiting for provider ...");
		
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				setTextBoxPosition(location);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
				if (status == LocationProvider.AVAILABLE) {
					statusEditText.setText(provider + " : Avalaible with " + extras.getInt("satellites") + " sats.");
				} else if (status == LocationProvider.OUT_OF_SERVICE) {
					statusEditText.setText(provider + " : Out Of Service.");
				} else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
					statusEditText.setText(provider + " : Waiting to initialization ...");
				}
			}

			public void onProviderEnabled(String provider) {
				statusEditText.setText(provider + " : enabled");
			}

			public void onProviderDisabled(String provider) {
				statusEditText.setText(provider + " : disabled");
			}
		};
		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,	0, locationListener);
	}
	
	/**
	 * Set textbox content with the current user position.
	 * 
	 * @param location
	 *            The location of the user.
	 */
	private void setTextBoxPosition(Location location) {
    	final EditText xEditText = (EditText) findViewById(R.id.gpsX);
    	xEditText.setText("" + location.getLatitude());
    	final EditText yEditText = (EditText) findViewById(R.id.gpsY);
    	yEditText.setText("" + location.getLongitude());
    }
}
