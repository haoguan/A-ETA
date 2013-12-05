package edu.berkeley.cs160.groupa.eta.model;

import edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper.ApptColumns;
import static edu.berkeley.cs160.groupa.eta.model.ETASQLiteHelper.*;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ApptContentProvider extends ContentProvider {

	// db
	private SQLiteDatabase database;

	// used for URI matching
	public final static int APPTS = 10;
	public final static int APPTS_ID = 20;
	
	private final static String AUTHORITY = "edu.berkeley.cs160.groupa.eta.model"; 
	private final static String BASE_PATH = "apptlists";
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/apps";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/app";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, APPTS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", APPTS_ID);
	}
	
	public static final String[] APPTS_PROJECTION = new String[] {
		ApptColumns._ID,
		ApptColumns.NAME,
		ApptColumns.PHONE,
		ApptColumns.DATE,
		ApptColumns.FROM,
		ApptColumns.TO,
		ApptColumns.LOCATION,
		ApptColumns.NOTES,
		ApptColumns.AM_PM,
		ApptColumns.TWELVE
	};
	
	@Override
	public boolean onCreate() {
		database = ETASQLiteHelper.getInstance(getContext().getApplicationContext()).getWritableDatabase();
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, 
			String[] selectionArgs, String sortOrder) {
		//Using SQLiteBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		//set table
		queryBuilder.setTables(APPT_TABLE);
		
		//determine if query is for a single entry or all entries.
		int uriType = sURIMatcher.match(uri);
		switch(uriType) {
		case APPTS:
			// no filter
			break;
		case APPTS_ID:
			//Adding the ID to the original query
			queryBuilder.appendWhere(ApptColumns._ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		Cursor cursor = queryBuilder.query(database, projection, selection, 
				selectionArgs, null, null, sortOrder);
		
		//Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}
	
	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		long id = 0;
		
		switch(uriType) {
		case APPTS:
			id = database.insert(APPT_TABLE, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}
	

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		int rowsDeleted = 0;
		switch(uriType) {
		case APPTS:
			rowsDeleted = database.delete(APPT_TABLE, selection, selectionArgs);
			break;
		case APPTS_ID:
			String id = uri.getLastPathSegment();
			//depends on selection param, need to alter SQL command.
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = database.delete(APPT_TABLE, 
					ApptColumns._ID + "=" + id, null);
			} else {
				rowsDeleted = database.delete(APPT_TABLE, 
					ApptColumns._ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}


	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		int rowsUpdated = 0;
		switch(uriType) {
		case APPTS:
			rowsUpdated = database.update(APPT_TABLE, values, 
					selection, selectionArgs);
			break;
		case APPTS_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = database.update(APPT_TABLE, values, 
						ApptColumns._ID + "=" + id, null);
			} else {
				rowsUpdated = database.update(APPT_TABLE, values, 
						ApptColumns._ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
}
