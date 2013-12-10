package edu.berkeley.cs160.groupa.eta;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class JobDetailsActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar -> Must be before adding content.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.job_details);
	}
}
