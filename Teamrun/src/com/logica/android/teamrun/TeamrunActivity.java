package com.logica.android.teamrun;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Logica Teamrun Activity.
 * @author Logica Teamrun.
 */
public class TeamrunActivity extends Activity {

	private TeamrunDeamon deamon;
	private boolean running = false;

	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	    	deamon = ((TeamrunDeamon.LocalBinder)service).getService();
	    	Toast.makeText(TeamrunActivity.this, R.string.local_service_connected, Toast.LENGTH_SHORT).show();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	    	deamon = null;
	        Toast.makeText(TeamrunActivity.this, R.string.local_service_disconnected, Toast.LENGTH_SHORT).show();
	    }
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Bind buttons
		Button testButton = (Button) findViewById(R.id.testButton);
		testButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Test connection with URL
			}
		});
		
		final Button deamonButton = (Button) findViewById(R.id.deamonButton);
		deamonButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!running) {
					Log.v("deamonButton : ", "connection");
					doBindService();
					deamonButton.setText("Stop Deamon");
				} else {
					Log.v("deamonButton : ", "disconnection");
					doUnbindService();
					deamonButton.setText("Start Deamon");
				}
			}
		});
		
		// Init locationListener
		initLocationListener();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	}
	
	private void doBindService() {
		// Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    bindService(new Intent(TeamrunActivity.this, TeamrunDeamon.class), mConnection, Context.BIND_AUTO_CREATE);
	    running = true;
	}
	
	private void doUnbindService() {
	    if (running) {
	        // Detach our existing connection.
	        unbindService(mConnection);
	        running = false;
	    }
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
