package org.ventapp.vent.data.source.local;

import android.provider.BaseColumns;

public class PostsPersistenceContract {
    
    private PostsPersistenceContract() {}
    
    public static abstract class PostEntry implements BaseColumns {
        public static final String TABLE_NAME = "post";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_BODY = "body";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }
}
