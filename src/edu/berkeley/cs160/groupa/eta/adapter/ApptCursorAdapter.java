package edu.berkeley.cs160.groupa.eta.adapter;

import edu.berkeley.cs160.groupa.eta.DirectionsActivity;
import edu.berkeley.cs160.groupa.eta.JobDetailsActivity;
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
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ApptCursorAdapter extends CursorAdapter {

    LayoutInflater mInflater;

    public ApptCursorAdapter(Context context, Cursor c) {
        // that constructor should be used with loaders.
        super(context, c, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    	//set start time
        TextView aptStartTime = (TextView)view.findViewById(R.id.tv_list_time);
        aptStartTime.setText(cursor.getString(cursor.getColumnIndex(ApptColumns.FROM)));

        //set apt name
        TextView aptName = (TextView)view.findViewById(R.id.tv_appt_list_name);
        aptName.setText(cursor.getString(cursor.getColumnIndex(ApptColumns.NAME)));
        
        LinearLayout llName = (LinearLayout) view.findViewById(R.id.ll_list_name);
        LinearLayout llTravel = (LinearLayout) view.findViewById(R.id.ll_list_travel);
        llName.setOnClickListener(new NameOnClickListener(cursor, cursor.getPosition()));
        
		llTravel.setOnClickListener(new TravelOnClickListener(cursor, cursor.getPosition()));

//        int position = cursor.getPosition(); // that should be the same position
//        if (mSelectedPosition == position) {
//           view.setBackgroundColor(Color.RED);
//        } else {
//           view.setBackgroundColor(Color.WHITE);
//        }
        

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.home_appt_list_item, parent, false);
        // edit: no need to call bindView here. That's done automatically
        return v;
    }
    
    
    //custom onclicklistener that handles cursor
    public class TravelOnClickListener implements OnClickListener {
    	
    	Cursor c;
    	int pos;
    	
    	public TravelOnClickListener(Cursor c, int pos) {
    		this.c = c;
    		this.pos = pos;
    	}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			c.moveToPosition(pos);
			String location = c.getString(c.getColumnIndex(ApptColumns.LOCATION));
			Intent i = new Intent(v.getContext(), DirectionsActivity.class);
			i.putExtra("location", location);
			v.getContext().startActivity(i);
		}
    	
    }
    
    //custom onclicklistener that handles cursor
    public class NameOnClickListener implements OnClickListener {
    	
    	Cursor c;
    	int pos;
    	
    	public NameOnClickListener(Cursor c, int pos) {
    		this.c = c;
    		this.pos = pos;
    	}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			c.moveToPosition(pos);
			String name = c.getString(c.getColumnIndex(ApptColumns.NAME));
			String phone = c.getString(c.getColumnIndex(ApptColumns.PHONE));
			String date = c.getString(c.getColumnIndex(ApptColumns.DATE));
			String from = c.getString(c.getColumnIndex(ApptColumns.FROM));
			String to = c.getString(c.getColumnIndex(ApptColumns.TO));
			String location = c.getString(c.getColumnIndex(ApptColumns.LOCATION));
			String notes = c.getString(c.getColumnIndex(ApptColumns.NOTES));
			Intent i = new Intent(v.getContext(), JobDetailsActivity.class);
			i.putExtra("name", name);
			i.putExtra("phone", phone);
			i.putExtra("date", date);
			i.putExtra("from", from);
			i.putExtra("to", to);
			i.putExtra("location", location);
			i.putExtra("notes", notes);
			i.putExtra("position", pos);
			v.getContext().startActivity(i);
		}
    	
    }

}
