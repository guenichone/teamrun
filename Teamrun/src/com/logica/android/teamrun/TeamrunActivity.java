package com.logica.android.teamrun;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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

	private boolean running = false;
	private TeamrunDeamon deamon = null;
	// Service connection used to bind service to this activity
	private ServiceConnection serviceConnection = new ServiceConnection() {
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			deamon = ((TeamrunDeamon.LocalBinder) service).getService();
		}
		
		public void onServiceDisconnected(ComponentName name) {
			deamon = null;
		}
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Bind test connection button
		Button testButton = (Button) findViewById(R.id.testButton);
		testButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				testConnection();
			}
		});
		
		// Bind deamon button
		final Button deamonButton = (Button) findViewById(R.id.deamonButton);
		deamonButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!running) {
					Log.v("deamonButton : ", "connection");
					runDeamon();
					deamonButton.setText(R.string.stop_deamon);
				} else {
					Log.v("deamonButton : ", "disconnection");
					stopDeamon();
					deamonButton.setText(R.string.start_deamon);
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
	    stopDeamon();
	}
	
	private void runDeamon() {
		bindService(new Intent(TeamrunActivity.this, TeamrunDeamon.class), serviceConnection, BIND_AUTO_CREATE);
	    //startService(new Intent(TeamrunActivity.this, TeamrunDeamon.class)); // Simple way without binding
		if (deamon != null) {
			EditText urlEditText = (EditText) findViewById(R.id.URL);
			deamon.setUrl(urlEditText.getText().toString());
			EditText teamEditText = (EditText) findViewById(R.id.TeamNumber);
			deamon.setTeam(teamEditText.getText().toString());
		} else {
			Log.e("deamon : " , "Run failed ...");
		}
		
	    running = true;
	}
	
	private void stopDeamon() {
	    if (running) {
	    	unbindService(serviceConnection);
	    	//stopService(new Intent(TeamrunActivity.this, TeamrunDeamon.class)); // Simple way without binding
	        running = false;
	    }
	}
	
	/**
	 * Test the network connection to the specified server. 
	 */
	private void testConnection() {
		EditText urlEditText = (EditText) findViewById(R.id.URL);
		String url = "http://" + urlEditText.getText().toString();
		Log.v("testConnection : ", url);
		
		try {
			HttpGet httpGet = new HttpGet(url);
			HttpClient httpclient = new DefaultHttpClient();
			
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);
			// Check response status
			int statusCode = response.getStatusLine().getStatusCode(); 
			if (statusCode == 200) {
				Toast.makeText(getApplicationContext(), "connection successful !", Toast.LENGTH_LONG).show();
				Log.v("testConnection : ", "OK");
			} else {
				Toast.makeText(getApplicationContext(), "connection failed (" + url + ").", Toast.LENGTH_LONG).show();
				Log.v("testConnection : ", String.valueOf(statusCode));
			}
        } catch (Exception e) {
        	Toast.makeText(getApplicationContext(), "connection failed (" + url + ").", Toast.LENGTH_LONG).show();
			Log.v("testConnection : ", e.toString());
		}
	}

	/**
	 * Initialize the locationListener and bind actions to EditText status and position.
	 */
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
    	xEditText.setText(String.valueOf(location.getLongitude()));
    	final EditText yEditText = (EditText) findViewById(R.id.gpsY);
    	yEditText.setText(String.valueOf(location.getLatitude()));
    }
}
