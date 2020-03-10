package ca.andrewmcneill.chroniclelist.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    /*
        DB Params
     */

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "chronicleList";

    // DB Schema
    public static final String TABLE_NAME = "bookCollections";

    //TODO: Make Rest of DB Schema
    
    /*
        Methods
     */

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
