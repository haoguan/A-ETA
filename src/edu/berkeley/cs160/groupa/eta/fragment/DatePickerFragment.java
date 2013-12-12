package edu.berkeley.cs160.groupa.eta.fragment;

import java.util.Calendar;

import edu.berkeley.cs160.groupa.eta.AddJobActivity;
import edu.berkeley.cs160.groupa.eta.EditJobActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

public class DatePickerFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		String type = getArguments().getString("type");
		if (type.equals("addJob")) {
			return new DatePickerDialog(getActivity(), (AddJobActivity)getActivity(), year, month, day);
		}
		else {
			return new DatePickerDialog(getActivity(), (EditJobActivity)getActivity(), year, month, day);
		}
//		return new DatePickerDialog(getActivity(), (AddJobActivity)getActivity(), year, month, day);
	}
}
