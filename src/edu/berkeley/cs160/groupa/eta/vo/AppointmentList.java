package edu.berkeley.cs160.groupa.eta.vo;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class AppointmentList extends ArrayList<Appointment> implements Parcelable {
	
	private static final long serialVersionUID = 1L;

	public AppointmentList() {}
	
	@SuppressWarnings("unused")
    public AppointmentList(Parcel in) {
        this();
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
        this.clear();

        // First we have to read the list size
        int size = in.readInt();

        for (int i = 0; i < size; i++) {
            Appointment appt = new Appointment();
            appt.setName(in.readString());
            appt.setPhone(in.readString());
            appt.setDate(in.readString());
            appt.setTimeFrom(in.readString());
            appt.setTimeTo(in.readString());
            appt.setLocation(in.readString());
            appt.setNotes(in.readString());
            this.add(appt);
        }
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		int size = this.size();
		
		dest.writeInt(size);
		
		for (int i = 0; i < size; i++) {
			Appointment appt = this.get(i);
			
			dest.writeString(appt.name);
			dest.writeString(appt.phone);
			dest.writeString(appt.date);
			dest.writeString(appt.timeFrom);
			dest.writeString(appt.timeTo);
			dest.writeString(appt.location);
			dest.writeString(appt.notes);
		}
	}
	
    public final Parcelable.Creator<AppointmentList> CREATOR = new Parcelable.Creator<AppointmentList>() {
        public AppointmentList createFromParcel(Parcel in) {
            return new AppointmentList(in);
        }

        public AppointmentList[] newArray(int size) {
            return new AppointmentList[size];
        }
    };

}
