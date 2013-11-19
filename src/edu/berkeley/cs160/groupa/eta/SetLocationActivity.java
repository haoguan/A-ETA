package edu.berkeley.cs160.groupa.eta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import edu.berkeley.cs160.groupa.eta.adapter.*;

public class SetLocationActivity extends Activity implements OnItemClickListener{

	Button bCancel;
	Button bSelect;
	private AutoCompleteTextView autoCompView;
	private LocationAutoCompleteAdapter adapter;
	
	private ListView locationList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location);
		
		bCancel = (Button) findViewById(R.id.b_location_cancel);
		bSelect = (Button) findViewById(R.id.b_location_select);
		
		adapter = new LocationAutoCompleteAdapter(this, R.layout.location_search_item);

		autoCompView = (AutoCompleteTextView) findViewById(R.id.actv_location_search);
		autoCompView.addTextChangedListener(filterTextWatcher);
		
		locationList = (ListView) findViewById(R.id.lv_locations);
		locationList.setAdapter(adapter);
		locationList.setOnItemClickListener(this);
		
		bCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		bSelect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String location = autoCompView.getText().toString();
				Intent i = new Intent();
				i.putExtra("location", location);
				setResult(RESULT_OK, i);	
				finish();
			}
		});
		
		//set autocompletetextview with curLocation
		Intent i = getIntent();
		if (i.getExtras() != null) {
			String curLocation = i.getExtras().getString("curLocation");
			autoCompView.setText(curLocation);
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> g, View v, int pos, long id) {
		// TODO Auto-generated method stub
		String locationText = ((TextView) v.findViewById(R.id.tv_location_in_list)).getText().toString();
		Log.v("TEXT", locationText);
		//set edit text to text.
		autoCompView.setText(locationText);
		autoCompView.setSelection(autoCompView.getText().length());
		autoCompView.selectAll(); //highlights all the text in field.
	}
	
	private TextWatcher filterTextWatcher = new TextWatcher() {

	    public void afterTextChanged(Editable s) {
	    }

	    public void beforeTextChanged(CharSequence s, int start, int count,
	            int after) {
	    }

	    public void onTextChanged(CharSequence s, int start, int before,
	            int count) {
	        adapter.getFilter().filter(s);
	    }

	};
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    autoCompView.removeTextChangedListener(filterTextWatcher);
	}

}
