package edu.berkeley.cs160.groupa.eta.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class Appointment implements Parcelable{
	
	String name;
	String phone;
	String date;
	String timeFrom;
	String timeTo;
	String location;
	String notes;
	
	public Appointment() {
		
	}
	
	public Appointment(Parcel in) {
		String[] data = new String[7];
		
		in.readStringArray(data);
		this.name = data[0];
		this.phone = data[1];
		this.date = data[2];
		this.timeFrom = data[3];
		this.timeTo = data[4];
		this.location = data[5];
		this.notes = data[6];
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}

	public String getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeStringArray(new String[] {
			this.name,
			this.phone,
			this.date,
			this.timeFrom,
			this.timeTo,
			this.location,
			this.notes
		});
	}
	
	public static final Parcelable.Creator<Appointment> CREATOR = new Parcelable.Creator<Appointment>() {

		@Override
		public Appointment createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Appointment(source);
		}

		@Override
		public Appointment[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Appointment[size];
		}
		
	};
}
