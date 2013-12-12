package edu.berkeley.cs160.groupa.eta.adapter;

import edu.berkeley.cs160.groupa.eta.DirectionsActivity;
import edu.berkeley.cs160.groupa.eta.R;
import edu.berkeley.cs160.groupa.eta.model.ApptContentProvider;
import edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper.ApptColumns;
import edu.berkeley.cs160.groupa.eta.vo.Appointment;
import edu.berkeley.cs160.groupa.eta.vo.AppointmentList;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LateApptCursorAdapter extends CursorAdapter {

    LayoutInflater mInflater;
    CheckBox cbNotify;
    
    //list of people to text
    AppointmentList apptsToText;

    public LateApptCursorAdapter(Context context, Cursor c) {
        // that constructor should be used with loaders.
        super(context, c, 0);
        mInflater = LayoutInflater.from(context);
        apptsToText = new AppointmentList();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    	
    	//set start time
        TextView aptStartTime = (TextView)view.findViewById(R.id.tv_late_list_time);
        aptStartTime.setText(cursor.getString(cursor.getColumnIndex(ApptColumns.FROM)));

        //set apt name
        TextView aptName = (TextView)view.findViewById(R.id.tv_late_appt_list_name);
        aptName.setText(cursor.getString(cursor.getColumnIndex(ApptColumns.NAME)));
        
//        LinearLayout llName = (LinearLayout) view.findViewById(R.id.ll_late_list_name);
//        LinearLayout llTravel = (LinearLayout) view.findViewById(R.id.ll_list_travel);
//        llName.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (cbNotify.isChecked()) {
//					cbNotify.setChecked(true);
//				}
//				else {
//					cbNotify.setChecked(false);
//				}
//			}
//		});
        
//		llTravel.setOnClickListener(new TravelOnClickListener(cursor, cursor.getPosition()));
    }
    
    

    public AppointmentList getApptsToText() {
		return apptsToText;
	}

	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.late_appt_list_item, parent, false);
        // edit: no need to call bindView here. That's done automatically
		cbNotify = (CheckBox) v.findViewById(R.id.cb_notify);
		Appointment appt = new Appointment();
		appt.setName(cursor.getString(cursor.getColumnIndex(ApptColumns.NAME)));
		appt.setPhone(cursor.getString(cursor.getColumnIndex(ApptColumns.PHONE)));
        cbNotify.setOnCheckedChangeListener(new CheckBoxListener(appt)); //HOLY SHIT. LISTENERS NEED TO BE CREATED IN NEW VIEW, NOT BIND VIEW LOL.
        cbNotify.setChecked(true);
        return v;
    }
    
    public class CheckBoxListener implements OnCheckedChangeListener {
    	
//    	Cursor c;
//    	int pos;
    	Appointment appt;
    	
    	public CheckBoxListener(Appointment appt) {
//    		this.c = c;
//    		this.pos = pos;
    		this.appt = appt;
    	}
    	
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				//add appt to list to text.
//				c.moveToPosition(pos);
//				String name = c.getString(c.getColumnIndex(ApptColumns.NAME));
//				String phone = c.getString(c.getColumnIndex(ApptColumns.PHONE));
//				appt.setName(name);
//				appt.setPhone(phone);
//				System.out.println("APPT 1: " + appt.getName() + ", " + appt.getPhone());
				apptsToText.add(appt);
				System.out.println("1.SIZE OF LIST: " + apptsToText.size());
			}
			else {
				//remove appt from list to text.
//				apptsToText.add(appt);
//				System.out.println("2.SIZE OF LIST: " + apptsToText.size());
				apptsToText.remove(appt);
				System.out.println("2.SIZE OF LIST: " + apptsToText.size());
			}
		}
    	
    	
    }
    //custom onclicklistener that handles cursor
//    public class TravelOnClickListener implements OnClickListener {
//    	
//    	Cursor c;
//    	int pos;
//    	
//    	public TravelOnClickListener(Cursor c, int pos) {
//    		this.c = c;
//    		this.pos = pos;
//    	}
//
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			c.moveToPosition(pos);
//			String location = c.getString(c.getColumnIndex(ApptColumns.LOCATION));
//			Intent i = new Intent(v.getContext(), DirectionsActivity.class);
//			i.putExtra("location", location);
//			v.getContext().startActivity(i);
//		}
//    	
//    }

}
