package edu.berkeley.cs160.groupa.eta.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import edu.berkeley.cs160.groupa.eta.R;
import edu.berkeley.cs160.groupa.eta.vo.Appointment;
import edu.berkeley.cs160.groupa.eta.vo.AppointmentList;

public class AddJobConflictsFragment extends DialogFragment {
	
	Button okButton;
	ScrollView svConflicts;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout to use as dialog or embedded fragment
		View v = inflater.inflate(R.layout.add_job_conflicts, container, false);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		okButton = (Button) v.findViewById(R.id.b_conflicts_ok);
		svConflicts = (ScrollView) v.findViewById(R.id.sv_conflict_appts);
		
		//set conflicts body -- only guaranteed to have name, timeFrom, timeTo (could change if needed)
		AppointmentList conflictsList = getArguments().getParcelable("conflictAppts");	
		svConflicts.removeAllViews(); //clear the test data.
		//scrollview can only hold one child apparently...
		LinearLayout body = new LinearLayout(v.getContext());
		body.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		body.setLayoutParams(bodyParams);
		
		for (int i = 0; i < conflictsList.size(); i++) {
			Appointment appt = conflictsList.get(i);
			LinearLayout innerBody = new LinearLayout(v.getContext());
			innerBody.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			nameParams.setMargins(25, 10, 0, 0);
			timeParams.setMargins(0, 10, 0, 0);
			
			//add views
			TextView name = new TextView(v.getContext());
			System.out.println("APPT NAME: " + appt.getName());
			name.setText(appt.getName());
//			name.setTextAppearance(v.getContext(), R.style.conflicts_dialog_appt_name);
			name.setTextSize(18);
			name.setTextColor(getResources().getColor(R.color.etaWhite));
//			name.setGravity(Gravity.LEFT);
			name.setPadding(25, 0, 0, 0);
			name.setTypeface(null, Typeface.BOLD);
			
			TextView time = new TextView(v.getContext());
			System.out.println("APPT TIME: " + appt.getTimeFrom() + " to " + appt.getTimeTo());
			time.setText(appt.getTimeFrom() + " to " + appt.getTimeTo());
//			time.setTextAppearance(v.getContext(), R.style.conflicts_dialog_appt_time);
			time.setTextSize(18);
			time.setTextColor(getResources().getColor(R.color.etaWhite));
			time.setGravity(Gravity.CENTER);
			time.setTypeface(null, Typeface.BOLD);
			
			innerBody.addView(name, nameParams);
			innerBody.addView(time, timeParams);
			
			body.addView(innerBody);
		}
		svConflicts.addView(body);

		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDialog().dismiss();
			}
		});
		return v;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		return dialog;
	}

}
