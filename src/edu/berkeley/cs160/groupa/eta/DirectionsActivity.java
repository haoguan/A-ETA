package edu.berkeley.cs160.groupa.eta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class DirectionsActivity extends Activity {
	
	Button bHome;
	Button bRunningLate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directions);
        
        bHome = (Button) findViewById(R.id.b_directions_home);
        bRunningLate = (Button) findViewById(R.id.b_directions_late);
        
        bHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish(); //no need to come back.
				Intent i = new Intent(v.getContext(), HomeActivity.class);
				startActivity(i);
			}
		});
        
        
    }
}
