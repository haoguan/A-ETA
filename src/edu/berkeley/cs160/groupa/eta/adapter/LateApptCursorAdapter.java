package edu.berkeley.cs160.groupa.eta.adapter;

import edu.berkeley.cs160.groupa.eta.DirectionsActivity;
import edu.berkeley.cs160.groupa.eta.R;
import edu.berkeley.cs160.groupa.eta.model.ApptContentProvider;
import edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper.ApptColumns;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LateApptCursorAdapter extends CursorAdapter {

    LayoutInflater mInflater;
    CheckBox cbNotify;

    public LateApptCursorAdapter(Context context, Cursor c) {
        // that constructor should be used with loaders.
        super(context, c, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    	
		cbNotify = (CheckBox) view.findViewById(R.id.cb_notify);
    	//set start time
        TextView aptStartTime = (TextView)view.findViewById(R.id.tv_late_list_time);
        aptStartTime.setText(cursor.getString(cursor.getColumnIndex(ApptColumns.FROM)));

        //set apt name
        TextView aptName = (TextView)view.findViewById(R.id.tv_late_appt_list_name);
        aptName.setText(cursor.getString(cursor.getColumnIndex(ApptColumns.NAME)));
        
        LinearLayout llName = (LinearLayout) view.findViewById(R.id.ll_late_list_name);
//        LinearLayout llTravel = (LinearLayout) view.findViewById(R.id.ll_list_travel);
        llName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cbNotify.isChecked()) {
					cbNotify.setChecked(true);
				}
				else {
					cbNotify.setChecked(false);
				}
			}
		});
        
//		llTravel.setOnClickListener(new TravelOnClickListener(cursor, cursor.getPosition()));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.late_appt_list_item, parent, false);
        // edit: no need to call bindView here. That's done automatically
        return v;
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
