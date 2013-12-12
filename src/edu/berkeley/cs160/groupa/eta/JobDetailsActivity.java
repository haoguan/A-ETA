package edu.berkeley.cs160.groupa.eta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.berkeley.cs160.groupa.eta.DirectionsActivity.GeocodeAddress;
import edu.berkeley.cs160.groupa.eta.model.ApptContentProvider;
import edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper.ApptColumns;
import edu.berkeley.cs160.groupa.eta.widget.GMapV2Direction;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class JobDetailsActivity extends Activity {
	
	String name;
	String phone;
	String timeFrom;
	String timeTo;
	String date;
	String location;
	String notes;
	int cursorPos;
	
	TextView tvName;
	EditText etPhone;
	EditText etTimeFrom;
	EditText etTimeTo;
	EditText etDate;
	EditText etLocation;
	EditText etNotes;
	
	//ui elements
	Button bEdit;
	Button bCall;
	Button bCancel;
	FrameLayout flMakeCall;
	FrameLayout flDirections;
	
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateFields();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar -> Must be before adding content.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.job_details);

		// set all fields
		Intent in = getIntent();
//		name = in.getExtras().getString("name");
//		phone = in.getExtras().getString("phone");
//		timeFrom = in.getExtras().getString("from");
//		timeTo = in.getExtras().getString("to");
//		date = in.getExtras().getString("date");
//		location = in.getExtras().getString("location");
//		notes = in.getExtras().getString("notes");
		cursorPos = in.getExtras().getInt("position"); //used to update fields
		
		tvName = (TextView) findViewById(R.id.tv_check_job_name);
		etPhone = (EditText) findViewById(R.id.et_check_job_phone);
		etTimeFrom = (EditText) findViewById(R.id.et_check_job_time_from);
		etTimeTo = (EditText) findViewById(R.id.et_check_job_time_to);
		etDate = (EditText) findViewById(R.id.et_check_job_date);
		etLocation = (EditText) findViewById(R.id.et_check_job_map_location);
		etNotes = (EditText) findViewById(R.id.et_check_job_notes);
		bCancel = (Button) findViewById(R.id.b_check_job_cancel);
		bEdit = (Button) findViewById(R.id.b_check_job_edit);
		flMakeCall = (FrameLayout) findViewById(R.id.fl_make_call);
		flDirections = (FrameLayout) findViewById(R.id.fl_get_directions);
		
		MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map_check_job);
		map = mapFrag.getMap();
		checkLocation();
		updateFields();
		stringDest = location;
		new GeocodeAddress(this).execute();
		
		bCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		bEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(v.getContext(), EditJobActivity.class);
				i.putExtra("name", name);
				i.putExtra("phone", phone);
				i.putExtra("date", date);
				i.putExtra("from", timeFrom);
				i.putExtra("to", timeTo);
				i.putExtra("location", location);
				i.putExtra("notes", notes);
				i.putExtra("position", cursorPos);
				v.getContext().startActivity(i);
			}
		});
		
		flMakeCall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + phone));
				startActivity(intent);
			}
		});
		
		etPhone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + phone));
				startActivity(intent);
			}
		});
	}
	
	private void updateFields() {
		//need to requery database for updated values.
		String select = "((" + ApptColumns.NAME + " NOTNULL) AND (" + ApptColumns.PHONE + " NOTNULL) AND (" + ApptColumns.DATE + " != '' ) AND (" + ApptColumns.FROM + " != '' ) AND ("
				+ ApptColumns.TO + " != '' ) AND (" + ApptColumns.LOCATION + " != '' ) AND (" + ApptColumns.AM_PM + " != '' ) AND (" + ApptColumns.TWELVE + " != '' ))";
		String orderBy = ApptColumns.AM_PM + ", " + ApptColumns.TWELVE + ", " + ApptColumns.FROM;
		Cursor updateCursor = getContentResolver().query(ApptContentProvider.CONTENT_URI, ApptContentProvider.APPTS_PROJECTION, select, null, orderBy);
		updateCursor.moveToPosition(cursorPos);
		name = updateCursor.getString(updateCursor.getColumnIndex(ApptColumns.NAME));
		phone = updateCursor.getString(updateCursor.getColumnIndex(ApptColumns.PHONE));
		timeFrom = updateCursor.getString(updateCursor.getColumnIndex(ApptColumns.FROM));
		timeTo = updateCursor.getString(updateCursor.getColumnIndex(ApptColumns.TO));
		date = updateCursor.getString(updateCursor.getColumnIndex(ApptColumns.DATE));
		location = updateCursor.getString(updateCursor.getColumnIndex(ApptColumns.LOCATION));
		notes = updateCursor.getString(updateCursor.getColumnIndex(ApptColumns.NOTES));
		
		tvName.setText(name);
		etPhone.setText("Call " + phone);
		etTimeFrom.setText(timeFrom);
		etTimeTo.setText(timeTo);
		etDate.setText(date);
		etLocation.setText(location);
		etNotes.setText(notes);
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
	

	private void addMarker(GoogleMap map, double lat, double lon, String string, String string2, boolean start) {
		if (start) {
			map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_a)).position(new LatLng(lat, lon)).title(string).snippet(string2));
		} else {
			map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_b)).position(new LatLng(lat, lon)).title(string).snippet(string2));
		}
	}

	class GeocodeAddress extends AsyncTask<Void, Void, ArrayList<LatLng>> {

		Context mContext;

		GeocodeAddress(Context context) {
			super();
			mContext = context;
		}

		protected ArrayList<LatLng> doInBackground(Void... params) {
			// does everything to get the correct lat longs.
			return getLocationInfo(stringDest);
		}

		protected void onPostExecute(ArrayList<LatLng> points) {

			// checks to make sure no quota issues. still displays the page.
			if (toPosition != null) {

				// add markers and draw polylines for route
				addMarker(map, from_lat, from_long, "Start", "My Location", true);
				addMarker(map, to_lat, to_long, "End", "Destination", false);

				// correct the zoom level
				LatLngBounds.Builder b = new LatLngBounds.Builder();
				b.include(fromPosition);
				b.include(toPosition);

				LatLngBounds bounds = b.build();

				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 500, 500, 5); // bounding
																							// box
																							// is
																							// 300
																							// pixels
																							// surrounding
																							// markers.
				map.animateCamera(cu);

				// draw the route path.
				PolylineOptions rectLine = new PolylineOptions().width(20).color(getResources().getColor(R.color.etaRoutePurple));

				for (int i = 0; i < points.size(); i++) {
					rectLine.add(points.get(i));
				}

				map.addPolyline(rectLine);
			}

		}

	}

	// returns the direction points to draw the route path to destination
	public ArrayList<LatLng> getLocationInfo(String address) {

		// replace address with + in place of spaces
		String newAddress = address.replace(' ', '+');

		System.out.println("NEW GEOCODE ADDRESS: " + newAddress);

		HttpGet httpGet = new HttpGet("http://maps.googleapis.com/maps/api/geocode/json?address=" + newAddress + "&sensor=true"); // maps.googleapis.com
																																	// =
																																	// V3!
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"),8);
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			stream.close();
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());

			JSONArray data = jsonObject.getJSONArray("results");
			JSONObject mainObj = data.getJSONObject(0); // NOT WRONG. JUST OVER
														// QUERY LIMIT LOL.
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
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "Sorry, out of queries for the day!", Toast.LENGTH_SHORT);
				toast.show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
