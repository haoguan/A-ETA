package edu.berkeley.cs160.groupa.eta;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.berkeley.cs160.groupa.eta.widget.GMapV2Direction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class DirectionsActivity extends Activity {

	Button bHome;
	Button bRunningLate;
	AutoCompleteTextView actvDest;

	LocationManager manager;
	String providerName;
	Location lastKnownLocation;

	GoogleMap map;
	
	LatLng geoDest;
	String stringDest;
	
	double from_lat;
	double from_long;
	double to_lat;
	double to_long;
	
	LatLng fromPosition;
	LatLng toPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.directions);

		bHome = (Button) findViewById(R.id.b_directions_home);
		bRunningLate = (Button) findViewById(R.id.b_directions_late);
		actvDest = (AutoCompleteTextView) findViewById(R.id.actv_directions_destination);
		MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		map = mapFrag.getMap();
		checkLocation();

		// set actv field and geocode street address
		Intent in = getIntent();
		stringDest = in.getExtras().getString("location");
		actvDest.setText(stringDest);
		
		//run async task here
		new GeocodeAddress(this).execute();
		
		bHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish(); // no need to come back.
				Intent i = new Intent(v.getContext(), HomeActivity.class);
				startActivity(i);
			}
		});
		
		bRunningLate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish(); // no need to come back.
				Intent i = new Intent(v.getContext(), RunningLateActivity.class);
				startActivity(i);
			}
		});
	}

	private void addMarker(GoogleMap map, double lat, double lon, String string, String string2) {
		map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(string).snippet(string2));
	}

	public void checkLocation() {

		// initialize location manager
		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// check if GPS is enabled
		// if not, notify user with a toast
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "GPS is disabled.", Toast.LENGTH_SHORT).show();
		} else {

			// get a location provider from location manager empty criteria
			// searches through all providers and returns the best one
			providerName = manager.getBestProvider(new Criteria(), true);
			lastKnownLocation = manager.getLastKnownLocation(providerName);
			from_lat = lastKnownLocation.getLatitude();
			from_long = lastKnownLocation.getLongitude();
			fromPosition = new LatLng(from_lat, from_long);

			if (lastKnownLocation != null) {
				Log.d("LOCATION LAT", String.valueOf(lastKnownLocation.getLatitude()));
				Log.d("LOCATION LONG", String.valueOf(lastKnownLocation.getLongitude()));
			}
			// manager.requestLocationUpdates(providerName, 15000, 1,
			// locationListener);
		}
	}
	
	class GeocodeAddress extends AsyncTask<Void, Void, ArrayList<LatLng>> {
		
		Context mContext;
		
		GeocodeAddress(Context context) {
			super();
			mContext = context;
		}

		protected ArrayList<LatLng> doInBackground(Void... params) {
			//does everything to get the correct lat longs.
			return getLocationInfo(stringDest);
		}
		
		protected void onPostExecute(ArrayList<LatLng> points) {
			
			//checks to make sure no quota issues. still displays the page.
			if (toPosition != null) {
				
				//add markers and draw polylines for route
				addMarker(map, from_lat, from_long, "Start", "Start Here!");
				addMarker(map, to_lat, to_long, "End", "End Here!");
				
				//correct the zoom level
				LatLngBounds.Builder b = new LatLngBounds.Builder();
				b.include(fromPosition);
				b.include(toPosition);
				
				LatLngBounds bounds = b.build();
				
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 500,500,5); //bounding box is 300 pixels surrounding markers.
				map.animateCamera(cu);
				
				//draw the route path.
	            PolylineOptions rectLine = new PolylineOptions().width(20).color(getResources().getColor(R.color.etaRoutePurple));
	            
	            for(int i = 0; i < points.size(); i++) {
	            	rectLine.add(points.get(i));
	            }
	            
	            map.addPolyline(rectLine);
			}
			
		}
		
	}
	
	//returns the direction points to draw the route path to destination
	public ArrayList<LatLng> getLocationInfo(String address) {
		
		//replace address with + in place of spaces
		String newAddress = address.replace(' ', '+');

	    HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + newAddress + "&sensor=true");
	    HttpClient client = new DefaultHttpClient();
	    HttpResponse response;
	    StringBuilder stringBuilder = new StringBuilder();

	    try {
	        response = client.execute(httpGet);
	        HttpEntity entity = response.getEntity();
	        InputStream stream = entity.getContent();
	        int b;
	        while ((b = stream.read()) != -1) {
	            stringBuilder.append((char) b);
	        }
	    } catch (ClientProtocolException e) {
	        } catch (IOException e) {
	    }

	    JSONObject jsonObject = new JSONObject();
	    try {
	        jsonObject = new JSONObject(stringBuilder.toString());
	        
	        JSONArray data = jsonObject.getJSONArray("results");
	        JSONObject mainObj = data.getJSONObject(0); //NOT WRONG. JUST OVER QUERY LIMIT LOL.
	        if (mainObj != null) {
				JSONObject locationObj = mainObj.getJSONObject("geometry").getJSONObject("location");
				geoDest = new LatLng(locationObj.getDouble("lat"), locationObj.getDouble("lng"));
				
				to_lat = geoDest.latitude;
				to_long = geoDest.longitude;
				
	            toPosition = new LatLng(to_lat, to_long);
	            
	            GMapV2Direction md = new GMapV2Direction();
	            
	            Document doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);
	            ArrayList<LatLng> directionPoint = md.getDirection(doc);
	            return directionPoint;
	        }
	        else {
	        	Toast toast = Toast.makeText(getApplicationContext(), "Sorry, out of queries for the day!", Toast.LENGTH_SHORT);
				toast.show();
	        }
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }
	    
		//get first object.
		//get location object
//		JSONObject locationObj = mainObj.optJSONObject("geometry").optJSONObject("location");
		return null;
	}
	
	

}
