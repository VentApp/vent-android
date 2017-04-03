package org.ventapp.vent.data.source;

import android.support.annotation.NonNull;

import org.ventapp.vent.data.Post;

import java.util.List;

import rx.Observable;

public interface PostsDataSource {

    Observable<List<Post>> getPosts();

    Observable<Post> getPost(@NonNull String postId);

    void savePost(@NonNull Post post);

    void refreshPosts();
}
