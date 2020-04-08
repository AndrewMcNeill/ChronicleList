package ca.andrewmcneill.chroniclelist.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ca.andrewmcneill.chroniclelist.beans.Book;

public class DBHelper extends SQLiteOpenHelper {

    /*
        DB Params
     */

    public static final int DB_VERSION = 2;
    public static final String DB_NAME = "chronicleList";

    // Schema DB
    /*
     * BOOK NAME
     * BOOK TITLE
     * BOOK AUTHOR
     * BOOK RATING
     * COVER URL
     * API ID
     *
     */
    public static final String TABLE_BOOKS = "bookCollections";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_USER_RATING = "user_rating";
    public static final String COLUMN_IMG_URL = "image";
    public static final String COLUMN_API_ID = "api_id";

    public static final String CREATE_BOOK_COLLECTIONS_TABLE = "CREATE TABLE " + TABLE_BOOKS + "(" +  COLUMN_API_ID + " TEXT PRIMARY KEY, "
            + COLUMN_TITLE + " TEXT, " + COLUMN_AUTHOR + " TEXT, " + COLUMN_RATING + " REAL, " + COLUMN_USER_RATING + " REAL, " + COLUMN_IMG_URL + " TEXT)";

    /*
        Methods
     */
    public void addBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_AUTHOR, book.getAuthor());
        values.put(COLUMN_RATING, book.getRating());
        values.put(COLUMN_USER_RATING, 0);
        values.put(COLUMN_IMG_URL, book.getCoverUrl());
        values.put(COLUMN_API_ID, book.getApiID());

        db.insert(TABLE_BOOKS, null, values);
    }

    public Book getBook(int api_id){
        SQLiteDatabase db  = this.getReadableDatabase();
        Book book = null;

        Cursor cursor = db.query(TABLE_BOOKS, new String[]{
                COLUMN_API_ID,
                COLUMN_TITLE,
                COLUMN_AUTHOR,
                COLUMN_RATING,
                COLUMN_IMG_URL }, COLUMN_API_ID + "= ?",
                new String[]{String.valueOf(api_id)}, null, null, null);

        if(cursor.moveToFirst()){
            // String apiID, String title, String author, double rating, String coverUrl

            book = new Book(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(5)
            );
        }
        cursor.close();
        return book;
    }

    public float getUserRating(String api_id) {
        float rating = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, new String[]{COLUMN_USER_RATING}, COLUMN_API_ID + "= ?",
                new String[]{String.valueOf(api_id)}, null, null, null);

        if (cursor.moveToFirst()) {
            rating = cursor.getFloat(0);
        }
        cursor.close();
        return rating;
    }

    public ArrayList<Book> getAllBooks() {
        ArrayList<Book> allBooks = new ArrayList<>();
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKS,null);
        while (cursor.moveToNext()) {
            allBooks.add(
                new Book(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(5)
            ));
        }
        cursor.close();
        return allBooks;
    }


    public int updateBookRating(Book book, float rating) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_USER_RATING, rating); // change to use user rating instead of goodreads rating
        return db.update(TABLE_BOOKS, v, COLUMN_API_ID + "=?",
                new String[]{String.valueOf( book.getApiID() )});
    }


    public void deleteBook(String api_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKS, COLUMN_API_ID + " = ?",
                new String[]{ String.valueOf(api_id) });
    }


    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_BOOK_COLLECTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
