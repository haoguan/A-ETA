package edu.berkeley.cs160.groupa.eta;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class DirectionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directions);
    }
}
