package edu.berkeley.cs160.groupa.eta;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.berkeley.cs160.groupa.eta.fragment.DatePickerFragment;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddJobActivity extends Activity implements
		DatePickerDialog.OnDateSetListener {

	Button bCancel;
	Button bContinue;
	EditText etName;
	EditText etPhone;
	EditText etDate;
	EditText etTimeFrom;
	EditText etTimeTo;
	EditText etLocation;

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

			}

		});

		etDate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					DialogFragment newFragment = new DatePickerFragment();
					newFragment.show(getFragmentManager(), "datePicker");
					v.clearFocus();
				}
			}
		});

		etLocation.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					Intent i = new Intent(v.getContext(),
							SetLocationActivity.class);
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
		etDate.setText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH) + ", " + (month + 1) + "/" + day + "/" + year);
	}

}
