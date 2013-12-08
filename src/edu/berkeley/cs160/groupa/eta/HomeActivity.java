package edu.berkeley.cs160.groupa.eta;

import edu.berkeley.cs160.groupa.eta.adapter.ApptCursorAdapter;
import edu.berkeley.cs160.groupa.eta.model.ApptContentProvider;
import edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper;
import edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper.ApptColumns;
import edu.berkeley.cs160.groupa.eta.widget.SwipeDismissListViewTouchListener;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
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
import android.widget.Toast;

public class HomeActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static HomeActivity instance;
	private SQLiteDatabase mDb;
	ListView mApptList;
	ApptCursorAdapter mApptAdapter;
	
	private Cursor mOrgCursor;

	//ui elements
	Button bAddJob;
	Button bRunningLate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		// Remove title bar -> Must be before adding content.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home);

		mDb = ETASQLiteHelper.getInstance(this).getWritableDatabase();

		bAddJob = (Button) findViewById(R.id.b_add_new_job);
		bRunningLate = (Button) findViewById(R.id.b_home_late);
		mApptList = (ListView) findViewById(R.id.lv_appts);

//		deleteTestData();
//		createTestData();

		// just need to set up appointments in list.
		getLoaderManager().initLoader(1, null, this);
		mApptAdapter = new ApptCursorAdapter(this, null);
		if (mApptList != null) {
			mApptList.setAdapter(mApptAdapter);
		}
		
		SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        mApptList,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                	//content provider query.
                                	String select = "((" + ApptColumns.NAME + " NOTNULL) AND (" + ApptColumns.PHONE + " NOTNULL) AND (" + ApptColumns.DATE + " != '' ) AND (" + ApptColumns.FROM + " != '' ) AND ("
                            				+ ApptColumns.TO + " != '' ) AND (" + ApptColumns.LOCATION + " != '' ) AND (" + ApptColumns.AM_PM + " != '' ) AND (" + ApptColumns.TWELVE + " != '' ))";
                                	String orderBy = ApptColumns.AM_PM + ", " + ApptColumns.TWELVE + ", " + ApptColumns.FROM;
                                	Cursor apptCursor = getContentResolver().query(ApptContentProvider.CONTENT_URI, ApptContentProvider.APPTS_PROJECTION,
                                			select, null, orderBy);
//                                	if (apptCursor.moveToFirst()) {
//                                		String[] columns = apptCursor.getColumnNames();
//                                		MatrixCursor newCursor = new MatrixCursor(columns, 1);
//                                		while (!apptCursor.isAfterLast()) {
//                                			// only add the ones NOT to be deleted
//                                			if (apptCursor.getPosition() != position) {
//	                                			MatrixCursor.RowBuilder b = newCursor.newRow();
//	                                			for (String col: columns) {
//	                                				switch (apptCursor.getType(apptCursor.getColumnIndex(col))) {
//	                                				case Cursor.FIELD_TYPE_INTEGER:
//	                                					b.add(apptCursor.getInt(apptCursor.getColumnIndex(col)));
//	                                					break;
//	                                				case Cursor.FIELD_TYPE_STRING:
//	                                					b.add(apptCursor.getString(apptCursor.getColumnIndex(col)));
//	                                				}
//	                                			}
//                                			}
//                                		}
//                                		mApptAdapter.swapCursor(newCursor);
//                                		//delete from original cursor
//                                		apptCursor.moveToPosition(position);
//                                		long id = apptCursor.getLong(apptCursor.getColumnIndex(ApptColumns._ID));
//                                    	getContentResolver().delete(ApptContentProvider.CONTENT_URI, "_ID = " + id, null);
////                                    	apptCursor.close();
//                                	}
                                	
                                	apptCursor.moveToPosition(position);
                                	long id = apptCursor.getLong(apptCursor.getColumnIndex(ApptColumns._ID));
                                	getContentResolver().delete(ApptContentProvider.CONTENT_URI, "_ID = " + id, null);
                                	apptCursor.close();
//                                    mApptAdapter.remove(mApptAdapter.getItem(position));
                                }
                                mApptAdapter.notifyDataSetChanged();
                            }
                        });
		
		mApptList.setOnTouchListener(touchListener);

		bAddJob.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// start add job activity.
				Intent intent = new Intent(v.getContext(), AddJobActivity.class);
				startActivity(intent);
			}

		});
		
		bRunningLate.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				// start add job activity.
				Intent intent = new Intent(v.getContext(), RunningLateActivity.class);
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
				+ ApptColumns.TO + " != '' ) AND (" + ApptColumns.LOCATION + " != '' ) AND (" + ApptColumns.AM_PM + " != '' ) AND (" + ApptColumns.TWELVE + " != '' ))";
		String orderBy = ApptColumns.AM_PM + ", " + ApptColumns.TWELVE + ", " + ApptColumns.FROM;
		CursorLoader cursorLoader = new CursorLoader(this, ApptContentProvider.CONTENT_URI, ApptContentProvider.APPTS_PROJECTION, select, null, orderBy);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//	    if (mOrgCursor != null)
//	        mOrgCursor.close();
//	    mOrgCursor = data;
//	    mApptAdapter.changeCursor(mOrgCursor);
	    
		if (mApptAdapter != null && data != null) {
			mApptAdapter.swapCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
//	    if (mOrgCursor != null) {
//	        mOrgCursor.close();
//	        mOrgCursor = null;
//	    }
//	    mApptAdapter.changeCursor(null);
		mApptAdapter.swapCursor(null);
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
