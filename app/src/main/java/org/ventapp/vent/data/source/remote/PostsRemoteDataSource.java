package org.ventapp.vent.data.source.remote;

import android.support.annotation.NonNull;

import org.ventapp.vent.data.Post;
import org.ventapp.vent.data.source.PostsDataSource;

import java.util.List;

import rx.Observable;

public class PostsRemoteDataSource implements PostsDataSource {

    @Override
    public Observable<List<Post>> getPosts() {
        return null;
    }

    @Override
    public void createPost(@NonNull Post post) {

    }

    @Override
    public void refreshPosts() {

    }
}
