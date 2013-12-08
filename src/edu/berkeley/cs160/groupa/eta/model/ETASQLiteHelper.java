package edu.berkeley.cs160.groupa.eta.model;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ETASQLiteHelper extends SQLiteOpenHelper {
        
        public static final String DATABASE_NAME = "eta";
        private static final int DATABASE_VERSION = 8;

        public static final String APPT_TABLE = "appt";
//        public static final String COPY_APPT_TABLE = "copy_appt";
        
        private static ETASQLiteHelper instance;
        
        
        public interface ApptColumns extends BaseColumns {
                public static final String NAME = "name";
                public static final String PHONE = "phone";
                public static final String DATE = "date";
                public static final String FROM = "timeFrom"; //don't use from and to! Those are like sql key words!
                public static final String TO = "timeTo";
                public static final String LOCATION = "location";
                public static final String NOTES = "notes";
                public static final String AM_PM = "am_pm";
                public static final String TWELVE = "twelve";
        }
        
        private static final String create_appt_table = "CREATE TABLE " + APPT_TABLE + " (  "
                                                        + ApptColumns._ID + " integer primary key autoincrement, "
                                                        + ApptColumns.NAME + " text not null, "
                                                        + ApptColumns.PHONE + " text not null, " 
                                                        + ApptColumns.DATE + " text not null, "
                                                        + ApptColumns.FROM + " text not null, "
                                                        + ApptColumns.TO + " text not null, "
                                                        + ApptColumns.LOCATION + " text not null, "
                                                        + ApptColumns.NOTES + " text not null, "
                                                        + ApptColumns.AM_PM + " text not null, "
                                                        + ApptColumns.TWELVE + " text not null"
                                                        + "); ";
        
//        private static final String create_copy_appt_table = "CREATE TABLE " + COPY_APPT_TABLE + " (  "
//        		+ ApptColumns._ID + " integer primary key autoincrement, "
//        		+ ApptColumns.NAME + " text not null, "
//        		+ ApptColumns.PHONE + " text not null, " 
//        		+ ApptColumns.DATE + " text not null, "
//        		+ ApptColumns.FROM + " text not null, "
//        		+ ApptColumns.TO + " text not null, "
//        		+ ApptColumns.LOCATION + " text not null, "
//        		+ ApptColumns.NOTES + " text not null, "
//        		+ ApptColumns.AM_PM + " text not null, "
//        		+ ApptColumns.TWELVE + " text not null"
//        		+ "); ";
        
        
        public ETASQLiteHelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
        //singleton method.
        public static ETASQLiteHelper getInstance(Context context) {
                if (instance == null) {
                	instance = new ETASQLiteHelper(context);
                }
                return instance;
        }
        
        // Method called during creation of the db.
        @Override
        public void onCreate(SQLiteDatabase db) {
                db.execSQL(create_appt_table);
//                db.execSQL(create_copy_appt_table);
        }

        // Method is called during an upgrade of the db.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE appt");
                db.execSQL(create_appt_table);
//                db.execSQL(create_copy_appt_table);
        }

}