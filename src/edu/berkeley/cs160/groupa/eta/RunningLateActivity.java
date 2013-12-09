package edu.berkeley.cs160.groupa.eta;

import java.util.ArrayList;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import edu.berkeley.cs160.groupa.eta.adapter.ApptCursorAdapter;
import edu.berkeley.cs160.groupa.eta.adapter.LateApptCursorAdapter;
import edu.berkeley.cs160.groupa.eta.model.ApptContentProvider;
import edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper;
import edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper.ApptColumns;
import edu.berkeley.cs160.groupa.eta.vo.Appointment;
import edu.berkeley.cs160.groupa.eta.vo.AppointmentList;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RunningLateActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private SQLiteDatabase mDb;
	LateApptCursorAdapter mLateApptAdapter;
	ListView mApptList;

	Button bCancel;
	Button bDone;

	Spinner spinLate;
	int hoursLate = 0;
	int minsLate = 0;
	
	String pattern = "hh:mm aa";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar -> Must be before adding content.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.running_late);

		mDb = ETASQLiteHelper.getInstance(this).getWritableDatabase();

		bCancel = (Button) findViewById(R.id.b_late_cancel);
		bDone = (Button) findViewById(R.id.b_late_done);
		mApptList = (ListView) findViewById(R.id.lv_late_appts);
		spinLate = (Spinner) findViewById(R.id.spinner_late);

		// set up spinner
		ArrayAdapter<CharSequence> lateByAdapter = ArrayAdapter
				.createFromResource(this, R.array.late_times,
						R.layout.spinner_item);
		lateByAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinLate.setAdapter(lateByAdapter);
		spinLate.setSelection(4);

		// just need to set up appointments in list.
		getLoaderManager().initLoader(1, null, this);
		mLateApptAdapter = new LateApptCursorAdapter(this, null);
		if (mApptList != null) {
			mApptList.setAdapter(mLateApptAdapter);
		}

		// set click listeners
		bCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		bDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// check to see which ones are checked
				AppointmentList listToText = mLateApptAdapter.getApptsToText();
				String lateBy = ((String) spinLate.getSelectedItem());
				for (int i = 0; i < listToText.size(); i++) {
					Appointment appt = listToText.get(i);
					String name = appt.getName();
					String phone = appt.getPhone();
					// create message and text that number.
					String message = "Hello " + name
							+ "! Sorry, but I will be late by " + lateBy + ".";
					try {
						SmsManager smsManager = SmsManager.getDefault();
						smsManager.sendTextMessage(phone, null, message, null, null);
						Toast.makeText(getApplicationContext(), "SMS Sent!",
								Toast.LENGTH_LONG).show();
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(),
								"SMS failed, please try again later!",
								Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				}
				
				//update schedule
				mDb.execSQL("DELETE FROM appt");
				// copy apptlists to copy table
				mDb.execSQL("INSERT INTO " + ETASQLiteHelper.APPT_TABLE
						+ " SELECT * FROM " + ETASQLiteHelper.COPY_APPT_TABLE);
				Intent i = new Intent(v.getContext(), HomeActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}
		});
		
		spinLate.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		        // your code here
		    	updatePreviewTable();
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
		String select = "((" + ApptColumns.NAME + " NOTNULL) AND (" + ApptColumns.PHONE + " NOTNULL) AND (" + ApptColumns.DATE + " != '' ) AND (" + ApptColumns.FROM + " != '' ) AND ("
				+ ApptColumns.TO + " != '' ) AND (" + ApptColumns.LOCATION + " != '' ) AND (" + ApptColumns.AM_PM + " != '' ) AND (" + ApptColumns.TWELVE + " != '' ))";
		String orderBy = ApptColumns.AM_PM + ", " + ApptColumns.TWELVE + ", " + ApptColumns.FROM;
		
		updatePreviewTable();

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		CursorLoader cursorLoader = new CursorLoader(this,
				ApptContentProvider.COPY_CONTENT_URI,
				ApptContentProvider.APPTS_PROJECTION, select, null, orderBy);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		if (mLateApptAdapter != null && data != null) {
			mLateApptAdapter.swapCursor(data);
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		mLateApptAdapter.swapCursor(null);
	}
	
	private String[] getSpinnerValues() {
		String spinVal = ((String) spinLate.getSelectedItem());
		return spinVal.split(",");
	}
	
	private void updatePreviewTable() {
		// clear copy apptLists first
		mDb.execSQL("DELETE FROM copy_appt");
		// copy apptlists to copy table
		mDb.execSQL("INSERT INTO " + ETASQLiteHelper.COPY_APPT_TABLE
				+ " SELECT * FROM " + ETASQLiteHelper.APPT_TABLE);
		
		//take spinner value and update all entries in table.
		String[] spinVals = getSpinnerValues();
		for (String time : spinVals) {
			//strip away the suffix
			if (time.contains("hr")) {
				hoursLate = Integer.parseInt(time.replaceAll("[^0-9]", "")); //replace all nondigits to blanks.
			}
			else {
				minsLate = Integer.parseInt(time.replaceAll("[^0-9]", ""));
			}
		}
		
		String select = "((" + ApptColumns.NAME + " NOTNULL) AND (" + ApptColumns.PHONE + " NOTNULL) AND (" + ApptColumns.DATE + " != '' ) AND (" + ApptColumns.FROM + " != '' ) AND ("
				+ ApptColumns.TO + " != '' ) AND (" + ApptColumns.LOCATION + " != '' ) AND (" + ApptColumns.AM_PM + " != '' ) AND (" + ApptColumns.TWELVE + " != '' ))";
		String orderBy = ApptColumns.AM_PM + ", " + ApptColumns.TWELVE + ", " + ApptColumns.FROM;
		Cursor updateCursor = getContentResolver().query(ApptContentProvider.COPY_CONTENT_URI, ApptContentProvider.APPTS_PROJECTION, select, null, orderBy);
		String timeFrom;
		String timeTo;
		LocalTime ltFrom;
		LocalTime ltTo;
		while(updateCursor.moveToNext()) {
			//retrieve info from db and convert to joda time
			String id = updateCursor.getString(updateCursor.getColumnIndex(ApptColumns._ID));
			timeFrom = updateCursor.getString(updateCursor.getColumnIndex(ApptColumns.FROM));
			timeTo = updateCursor.getString(updateCursor.getColumnIndex(ApptColumns.TO));
			ltFrom = LocalTime.parse(timeFrom, DateTimeFormat.forPattern(pattern));
			ltTo = LocalTime.parse(timeTo, DateTimeFormat.forPattern(pattern));
			
			//add the late time.
			ltFrom = ltFrom.plusHours(hoursLate);
			ltFrom = ltFrom.plusMinutes(minsLate);
			ltTo = ltTo.plusHours(hoursLate);
			ltTo = ltTo.plusMinutes(minsLate);
			
			//readd to database
			ContentValues newTimes = new ContentValues();
			newTimes.put(ApptColumns.FROM, ltFrom.toString(pattern));
			newTimes.put(ApptColumns.TO, ltTo.toString(pattern));
			getContentResolver().update(ApptContentProvider.COPY_CONTENT_URI, newTimes, "_ID = ?", new String[]{id});
		}
		updateCursor.close();
		resetTimeLate();
	}
	
	private void resetTimeLate() {
		hoursLate = 0;
		minsLate = 0;
	}
}
