package edu.berkeley.cs160.groupa.eta.adapter;

import edu.berkeley.cs160.groupa.eta.DirectionsActivity;
import edu.berkeley.cs160.groupa.eta.R;
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

//        int position = cursor.getPosition(); // that should be the same position
//        if (mSelectedPosition == position) {
//           view.setBackgroundColor(Color.RED);
//        } else {
//           view.setBackgroundColor(Color.WHITE);
//        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.appt_list_item, parent, false);
        // edit: no need to call bindView here. That's done automatically
        LinearLayout llName = (LinearLayout) v.findViewById(R.id.ll_list_name);
        LinearLayout llTravel = (LinearLayout) v.findViewById(R.id.ll_list_travel);
        llName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		llTravel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(v.getContext(), DirectionsActivity.class);
				v.getContext().startActivity(i);
			}
		});
        return v;
    }


}
