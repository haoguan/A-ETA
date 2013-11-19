package edu.berkeley.cs160.groupa.eta;

import edu.berkeley.cs160.groupa.eta.model.ApptContentProvider;
import edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper;
import edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper.ApptColumns;
import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class HomeActivity extends Activity implements OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	private static HomeActivity instance;
	private SQLiteDatabase mDb;
	ListView mApptList;
	SimpleCursorAdapter mApptAdapter;
	
	//ui elements
	Button bAddJob;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		// Remove title bar -> Must be before adding content.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home);

		mDb = ETASQLiteHelper.getInstance(this).getWritableDatabase();

		bAddJob = (Button) findViewById(R.id.b_add_new_job);
		mApptList = (ListView) findViewById(R.id.lv_appts);
		
		deleteTestData();
//		createTestData();

		// just need to set up appointments in list.
		String[] from = new String[] { ApptColumns.NAME, ApptColumns.FROM };
		int[] to = new int[] { R.id.tv_appt_list_name, R.id.tv_list_time };
		getLoaderManager().initLoader(1, null, this);
		mApptAdapter = new SimpleCursorAdapter(this, R.layout.appt_list_item, null, from, to, 0);
		if (mApptList != null) {
			mApptList.setAdapter(mApptAdapter);
		}
		mApptList.setOnItemClickListener(this);
		
		bAddJob.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// start add job activity.
				Intent intent = new Intent(v.getContext(), AddJobActivity.class);
				startActivity(intent);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		String select = "((" + ApptColumns.NAME + " NOTNULL) AND (" + ApptColumns.PHONE + " NOTNULL) AND (" + ApptColumns.DATE + " != '' ) AND (" + ApptColumns.FROM + " != '' ) AND ("
				+ ApptColumns.TO + " != '' ) AND (" + ApptColumns.LOCATION + " != '' ))";
		CursorLoader cursorLoader = new CursorLoader(this, ApptContentProvider.CONTENT_URI, ApptContentProvider.APPTS_PROJECTION, select, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (mApptAdapter != null && data != null) {
			mApptAdapter.swapCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mApptAdapter.swapCursor(null);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
		// TODO Auto-generated method stub
		
		Log.d("ITEMCLICK", "YES");
		if (v.getId() == R.id.ll_list_name) {
			Log.d("NAMEITEMCLICK", "YES");
		}
		else if (v.getId() == R.id.ll_list_travel) {
			Log.d("TRAVELITEMCLICK", "YES");
			Intent i = new Intent(this, DirectionsActivity.class);
			startActivity(i);
		}
		
	}

	public void deleteTestData() {
		// deletes everything! Because there is no "where" filter.
		getContentResolver().delete(ApptContentProvider.CONTENT_URI, null, null);
	}

	public void createTestData() {
		ContentValues values = new ContentValues();
		values.put(ApptColumns.NAME, "Jessica Tran");
		values.put(ApptColumns.PHONE, "858-335-9258");
		values.put(ApptColumns.DATE, "Monday, 10/19");
		values.put(ApptColumns.FROM, "3:30pm");
		values.put(ApptColumns.TO, "5:00pm");
		values.put(ApptColumns.LOCATION, "2226 Durant Ave, Berkeley CA");
		getContentResolver().insert(ApptContentProvider.CONTENT_URI, values);
		values.put(ApptColumns.NAME, "Jessica Tran");
		values.put(ApptColumns.PHONE, "858-335-9258");
		values.put(ApptColumns.DATE, "Monday, 10/19");
		values.put(ApptColumns.FROM, "3:30pm");
		values.put(ApptColumns.TO, "5:00pm");
		values.put(ApptColumns.LOCATION, "2226 Durant Ave, Berkeley CA");
		getContentResolver().insert(ApptContentProvider.CONTENT_URI, values);
		values.put(ApptColumns.NAME, "Jessica Tran");
		values.put(ApptColumns.PHONE, "858-335-9258");
		values.put(ApptColumns.DATE, "Monday, 10/19");
		values.put(ApptColumns.FROM, "3:30pm");
		values.put(ApptColumns.TO, "5:00pm");
		values.put(ApptColumns.LOCATION, "2226 Durant Ave, Berkeley CA");
		getContentResolver().insert(ApptContentProvider.CONTENT_URI, values);
		values.put(ApptColumns.NAME, "Jessica Tran");
		values.put(ApptColumns.PHONE, "858-335-9258");
		values.put(ApptColumns.DATE, "Monday, 10/19");
		values.put(ApptColumns.FROM, "3:30pm");
		values.put(ApptColumns.TO, "5:00pm");
		values.put(ApptColumns.LOCATION, "2226 Durant Ave, Berkeley CA");
		getContentResolver().insert(ApptContentProvider.CONTENT_URI, values);
		values.put(ApptColumns.NAME, "Jessica Tran");
		values.put(ApptColumns.PHONE, "858-335-9258");
		values.put(ApptColumns.DATE, "Monday, 10/19");
		values.put(ApptColumns.FROM, "3:30pm");
		values.put(ApptColumns.TO, "5:00pm");
		values.put(ApptColumns.LOCATION, "2226 Durant Ave, Berkeley CA");
		getContentResolver().insert(ApptContentProvider.CONTENT_URI, values);
	}

}
