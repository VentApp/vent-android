package org.ventapp.vent.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import org.ventapp.vent.data.Post;
import org.ventapp.vent.data.source.PostsDataSource;
import org.ventapp.vent.util.schedulers.BaseSchedulerProvider;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.ventapp.vent.data.source.local.PostsPersistenceContract.*;

public class PostsLocalDataSource implements PostsDataSource {

    @Nullable
    private static PostsLocalDataSource INSTANCE;

    @NonNull
    private final BriteDatabase mDatabaseHelper;

    @NonNull
    private Func1<Cursor, Post> mPostMapperFunction;

    private PostsLocalDataSource(@NonNull Context context, @NonNull BaseSchedulerProvider schedulerProvider) {
        checkNotNull(context, "context cannot be null");
        checkNotNull(schedulerProvider, "schedulerProvider cannot be null");
        PostsDbHelper dbHelper = new PostsDbHelper(context);
        SqlBrite sqlBrite = SqlBrite.create();
        mDatabaseHelper = sqlBrite.wrapDatabaseHelper(dbHelper, schedulerProvider.io());
        mPostMapperFunction = this::getPost;
    }

    private Post getPost(@NonNull Cursor c) {
        String itemId = c.getString(c.getColumnIndexOrThrow(PostEntry.COLUMN_NAME_ENTRY_ID));
        String body = c.getString(c.getColumnIndexOrThrow(PostEntry.COLUMN_NAME_BODY));
        String createdAt = c.getString(c.getColumnIndexOrThrow(PostEntry.COLUMN_NAME_CREATED_AT));
        return new Post(body, createdAt, itemId);
    }

    public static PostsLocalDataSource getInstance(@NonNull Context context, @NonNull BaseSchedulerProvider schedulerProvider) {
        if (INSTANCE == null) {
            INSTANCE = new PostsLocalDataSource(context, schedulerProvider);
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<Post>> getPosts() {
        String[] projection = {
                PostEntry.COLUMN_NAME_ENTRY_ID,
                PostEntry.COLUMN_NAME_BODY,
                PostEntry.COLUMN_NAME_CREATED_AT
        };
        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), PostEntry.TABLE_NAME);
        return mDatabaseHelper.createQuery(PostEntry.TABLE_NAME, sql).mapToList(mPostMapperFunction);
    }

    @Override
    public Observable<Post> getPost(@NonNull String postId) {
        String[] projection = {
                PostEntry.COLUMN_NAME_ENTRY_ID,
                PostEntry.COLUMN_NAME_BODY,
                PostEntry.COLUMN_NAME_CREATED_AT
        };
        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?", TextUtils.join(",", projection), PostEntry.TABLE_NAME, PostEntry.COLUMN_NAME_ENTRY_ID);
        return mDatabaseHelper.createQuery(PostEntry.TABLE_NAME, sql, postId).mapToOneOrDefault(mPostMapperFunction, null);
    }

    @Override
    public void savePost(@NonNull Post post) {
        checkNotNull(post);
        ContentValues values = new ContentValues();
        values.put(PostEntry.COLUMN_NAME_ENTRY_ID, post.getId());
        values.put(PostEntry.COLUMN_NAME_BODY, post.getBody());
        values.put(PostEntry.COLUMN_NAME_CREATED_AT, post.getCreatedAt());
        mDatabaseHelper.insert(PostEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void refreshPosts() {

    }
}
