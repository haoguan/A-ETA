package edu.berkeley.cs160.groupa.eta.fragment;

import java.util.Calendar;

import edu.berkeley.cs160.groupa.eta.AddJobActivity;
import edu.berkeley.cs160.groupa.eta.EditJobActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

public class TimePickerFragment extends DialogFragment{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of DatePickerDialog and return it
		String type = getArguments().getString("type");
		if (type.equals("addJob")) {
			return new TimePickerDialog(getActivity(), (AddJobActivity)getActivity(), hour, minute, false);
		}
		else {
			return new TimePickerDialog(getActivity(), (EditJobActivity)getActivity(), hour, minute, false);
		}
	}
}
