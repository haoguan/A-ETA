package edu.berkeley.cs160.groupa.eta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

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
	TextView tvPhone;
	TextView tvTimeFrom;
	TextView tvTimeTo;
	TextView tvDate;
	TextView tvLocation;
	TextView tvNotes;
	
	//ui elements
	Button bEdit;
	Button bCall;
	Button bCancel;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar -> Must be before adding content.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.job_details);

		// set all fields
		Intent in = getIntent();
		name = in.getExtras().getString("name");
		phone = in.getExtras().getString("phone");
		timeFrom = in.getExtras().getString("from");
		timeTo = in.getExtras().getString("to");
		date = in.getExtras().getString("date");
		location = in.getExtras().getString("location");
		notes = in.getExtras().getString("notes");
		cursorPos = in.getExtras().getInt("position"); //used to update fields
		
		tvName = (TextView) findViewById(R.id.tv_check_job_name);
		tvPhone = (TextView) findViewById(R.id.tv_check_job_phone);
		tvTimeFrom = (TextView) findViewById(R.id.tv_check_job_time_from);
		tvTimeTo = (TextView) findViewById(R.id.tv_check_job_time_to);
		tvDate = (TextView) findViewById(R.id.tv_check_job_date);
		tvLocation = (TextView) findViewById(R.id.tv_check_job_map_location);
		tvNotes = (TextView) findViewById(R.id.tv_check_job_notes);
		bCancel = (Button) findViewById(R.id.b_check_job_cancel);
		bEdit = (Button) findViewById(R.id.b_check_job_edit);
		
		tvName.setText(name);
		tvPhone.setText("Call " + phone);
		tvTimeFrom.setText(timeFrom);
		tvTimeTo.setText(timeTo);
		tvDate.setText(date);
		tvLocation.setText(location);
		tvNotes.setText(notes);
		
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
		
	}
}
