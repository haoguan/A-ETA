package edu.berkeley.cs160.groupa.eta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import edu.berkeley.cs160.groupa.eta.fragment.AddJobConflictsFragment;
import edu.berkeley.cs160.groupa.eta.fragment.DatePickerFragment;
import edu.berkeley.cs160.groupa.eta.fragment.TimePickerFragment;
import edu.berkeley.cs160.groupa.eta.model.ApptContentProvider;
import edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper.ApptColumns;
import edu.berkeley.cs160.groupa.eta.vo.Appointment;
import edu.berkeley.cs160.groupa.eta.vo.AppointmentList;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddJobActivity extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	Button bCancel;
	Button bContinue;
	EditText etName;
	EditText etPhone;
	EditText etDate;
	EditText etTimeFrom;
	EditText etTimeTo;
	EditText etLocation;
	EditText etNotes;
	boolean setFromTime = false;

	public final static int GET_LOCATION = 0x01;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar -> Must be before adding content.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_job);

		bCancel = (Button) findViewById(R.id.b_add_job_cancel);
		bContinue = (Button) findViewById(R.id.b_add_job_continue);

		etName = (EditText) findViewById(R.id.et_add_job_name);
		etPhone = (EditText) findViewById(R.id.et_add_job_phone);
		etDate = (EditText) findViewById(R.id.et_add_job_date);
		etTimeFrom = (EditText) findViewById(R.id.et_add_job_time_from);
		etTimeTo = (EditText) findViewById(R.id.et_add_job_time_to);
		etLocation = (EditText) findViewById(R.id.et_add_job_location);
		etNotes = (EditText) findViewById(R.id.et_add_job_notes);

		bCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish(); // goes back to previous page
			}
		});

		bContinue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ContentValues values = new ContentValues();
				String name = etName.getText().toString();
				String phone = etPhone.getText().toString();
				String date = etDate.getText().toString();
				String from = etTimeFrom.getText().toString();
				String to = etTimeTo.getText().toString();
				String location = etLocation.getText().toString();
				String notes = etNotes.getText().toString();
				
				String[] fieldNames = {"name", "phone", "date", "start time", "end time", "location"};
				String[] fieldValues = {name, phone, date, from, to, location};
				values.put(ApptColumns.NAME, name);
				values.put(ApptColumns.PHONE, phone);
				values.put(ApptColumns.DATE, date);
				values.put(ApptColumns.FROM, from);
				values.put(ApptColumns.TO, to);
				values.put(ApptColumns.LOCATION, location);
				values.put(ApptColumns.NOTES, notes);
				
				//check if am or pm
				if (from.contains("AM")) {
					values.put(ApptColumns.AM_PM, "AM");
				}
				else {
					values.put(ApptColumns.AM_PM, "PM");
				}
				
				//check if twelve
//				if (from.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")){
				if (from.matches("^(12:[0-5][0-9] [A|P][M]$)")){
					values.put(ApptColumns.TWELVE, "A"); //will get sorted before others.
				}
				else {
					values.put(ApptColumns.TWELVE, "B");
				}
				for (int i=0; i < fieldNames.length; i++) {
					String value = fieldValues[i];
					System.out.println("value: " + value);
					if (value.equals(null) || value.equals("")) {
						System.out.println("test2");
						Toast toast = Toast.makeText(getApplicationContext(), "Missing " + fieldNames[i], Toast.LENGTH_SHORT);
						toast.show();
						return;
					}
				}
				
				//there is conflict, display dialog.
				AppointmentList conflicts = getApptConflicts(from, to);
				if (conflicts.size() > 0) {
//					Toast toast = Toast.makeText(getApplicationContext(), "Conflict!", Toast.LENGTH_SHORT);
//					toast.show();
					DialogFragment conflictsFragment = new AddJobConflictsFragment();
					//pass conflicts list as parcelable.
					Bundle b = new Bundle();
					b.putParcelable("conflictAppts", conflicts);
					conflictsFragment.setArguments(b);
					conflictsFragment.show(getFragmentManager(), "conflicts");
					return;
				}
				
				getContentResolver().insert(ApptContentProvider.CONTENT_URI, values);
				finish();
			}

		});

		etDate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					DialogFragment newFragment = new DatePickerFragment();
					Bundle args = new Bundle();
					args.putString("type", "addJob");
					newFragment.setArguments(args);
					newFragment.show(getFragmentManager(), "datePicker");
					v.clearFocus();
				}
			}
		});

		etTimeFrom.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					setFromTime = true;
					DialogFragment newFragment = new TimePickerFragment();
					Bundle args = new Bundle();
					args.putString("type", "addJob");
					newFragment.setArguments(args);
					newFragment.show(getFragmentManager(), "fromTimePicker");
					v.clearFocus();
				}
			}
		});

		etTimeTo.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					setFromTime = false;
					DialogFragment newFragment = new TimePickerFragment();
					Bundle args = new Bundle();
					args.putString("type", "addJob");
					newFragment.setArguments(args);
					newFragment.show(getFragmentManager(), "fromTimePicker");
					v.clearFocus();
				}
			}
		});

		etLocation.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					Intent i = new Intent(v.getContext(), SetLocationActivity.class);
					String curLocation = etLocation.getText().toString();
					if (!curLocation.equals("")) {
						i.putExtra("curLocation", curLocation);
					}
					startActivityForResult(i, GET_LOCATION);
					v.clearFocus();
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		// handle return from the SetLocationActivity.
		if (requestCode == GET_LOCATION) {
			if (resultCode == RESULT_OK) {
				String location = data.getStringExtra("location");
				etLocation.setText(location);
			}
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEEEEE, MM/dd/yy");
		etDate.setText(dateFormat.format(cal.getTime()));
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		if (setFromTime) {
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
			etTimeFrom.setText(timeFormat.format(cal.getTime()));
		} else {
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
			etTimeTo.setText(timeFormat.format(cal.getTime()));
		}
	}
	
	//returns a list of conflicting appointments.
	private AppointmentList getApptConflicts(String from, String to) {
		AppointmentList conflicts = new AppointmentList();
		//check conflicts
		String select = "((" + ApptColumns.NAME + " NOTNULL) AND (" + ApptColumns.PHONE + " NOTNULL) AND (" + ApptColumns.DATE + " != '' ) AND (" + ApptColumns.FROM + " != '' ) AND ("
				+ ApptColumns.TO + " != '' ) AND (" + ApptColumns.LOCATION + " != '' ) AND (" + ApptColumns.AM_PM + " != '' ) AND (" + ApptColumns.TWELVE + " != '' ))";
		String orderBy = ApptColumns.AM_PM + ", " + ApptColumns.TWELVE + ", " + ApptColumns.FROM;
		Cursor checkCursor = getContentResolver().query(ApptContentProvider.CONTENT_URI, ApptContentProvider.APPTS_PROJECTION, select, null, orderBy);
		
		//create my LocalTime objects for requested from and to times.
		String pattern = "hh:mm aa";
		LocalTime fromTime = LocalTime.parse(from, DateTimeFormat.forPattern(pattern)); 
		LocalTime toTime = LocalTime.parse(to, DateTimeFormat.forPattern(pattern));
		
		//loop through all appts and check if start and end time of requested appt is in between
		//assumes cursor are BEFORE first entry. Should be since database queries do that.
		LocalTime apptFromTime;
		LocalTime apptToTime;
		while (checkCursor.moveToNext()) {
			String apptFrom = checkCursor.getString(checkCursor.getColumnIndex(ApptColumns.FROM));
			String apptTo = checkCursor.getString(checkCursor.getColumnIndex(ApptColumns.TO));
			apptFromTime = LocalTime.parse(apptFrom, DateTimeFormat.forPattern(pattern));
			apptToTime = LocalTime.parse(apptTo, DateTimeFormat.forPattern(pattern));
			
			if (isOverlapping(fromTime, toTime, apptFromTime, apptToTime)) {
				//add to conflicts list
				Appointment conflictAppt = new Appointment();
				conflictAppt.setName(checkCursor.getString(checkCursor.getColumnIndex(ApptColumns.NAME)));
				conflictAppt.setTimeFrom(apptFrom);
				conflictAppt.setTimeTo(apptTo);
				conflicts.add(conflictAppt);
			}
		}
		return conflicts;
	}
	
	//time overlap method!
	public static boolean isOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
	    return !start1.isAfter(end2) && !start2.isAfter(end1);
	}

}
