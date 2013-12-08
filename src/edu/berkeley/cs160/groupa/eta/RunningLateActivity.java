package edu.berkeley.cs160.groupa.eta;

import edu.berkeley.cs160.groupa.eta.adapter.ApptCursorAdapter;
import edu.berkeley.cs160.groupa.eta.adapter.LateApptCursorAdapter;
import edu.berkeley.cs160.groupa.eta.model.ApptContentProvider;
import edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper.ApptColumns;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;

public class RunningLateActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{
	
	LateApptCursorAdapter mApptAdapter;
	ListView mApptList;
	
	Button bCancel;
	Button bDone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar -> Must be before adding content.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.running_late);
		
		bCancel = (Button) findViewById(R.id.b_late_cancel);
		bDone = (Button) findViewById(R.id.b_late_done);
		mApptList = (ListView) findViewById(R.id.lv_late_appts);
		
		// just need to set up appointments in list.
		getLoaderManager().initLoader(1, null, this);
		mApptAdapter = new LateApptCursorAdapter(this, null);
		if (mApptList != null) {
			mApptList.setAdapter(mApptAdapter);
		}
		
		//set click listeners
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
				// TODO Auto-generated method stub
				
			}
		});
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
		// TODO Auto-generated method stub
		if (mApptAdapter != null && data != null) {
			mApptAdapter.swapCursor(data);
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		mApptAdapter.swapCursor(null);
	}
}
