package org.ventapp.vent.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PostsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Posts.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PostsPersistenceContract.PostEntry.TABLE_NAME + " (" +
                PostsPersistenceContract.PostEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                PostsPersistenceContract.PostEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                PostsPersistenceContract.PostEntry.COLUMN_NAME_BODY + TEXT_TYPE + COMMA_SEP +
                PostsPersistenceContract.PostEntry.COLUMN_NAME_CREATED_AT + TEXT_TYPE +
            " )";

    public PostsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
