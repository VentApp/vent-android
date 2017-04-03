package org.ventapp.vent.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ventapp.vent.data.Post;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

public class PostsRepository implements PostsDataSource {

    @Nullable
    private static PostsRepository INSTANCE = null;

    @NonNull
    private final PostsDataSource mPostsRemoteDataSource;

    @NonNull
    private final PostsDataSource mPostsLocalDataSource;

    @Nullable
    private boolean mCacheIsDirty;
    private Map<String, Post> mCachedPosts;

    private PostsRepository(@NonNull PostsDataSource postsRemoteDataSource, @NonNull PostsDataSource postsLocalDataSource) {
        mPostsRemoteDataSource = postsRemoteDataSource;
        mPostsLocalDataSource = postsLocalDataSource;
    }

    public static PostsRepository getInstance(@NonNull PostsDataSource postsRemoteDataSource, @NonNull PostsDataSource postsLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new PostsRepository(postsRemoteDataSource, postsLocalDataSource);
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<Post>> getPosts() {
        if (mCachedPosts != null && !mCacheIsDirty) {
            return Observable.from(mCachedPosts.values()).toList();
        } else if (mCachedPosts == null) {
            mCachedPosts = new LinkedHashMap<>();
        }

        Observable<List<Post>> remotePosts = getAndSaveRemotePosts();

        if (mCacheIsDirty) {
            return remotePosts;
        } else {
            Observable<List<Post>> localPosts = getAndCacheLocalPosts();
            return Observable.concat(localPosts, remotePosts)
                    .filter(posts -> !posts.isEmpty())
                    .first();
        }
    }

    private Observable<List<Post>> getAndCacheLocalPosts() {
        return mPostsLocalDataSource.getPosts()
                .flatMap(new Func1<List<Post>, Observable<List<Post>>>() {
                    @Override
                    public Observable<List<Post>> call(List<Post> posts) {
                        return Observable.from(posts)
                                .doOnNext(post -> mCachedPosts.put(post.getId(), post))
                                .toList();
                    }
                });
    }

    private Observable<List<Post>> getAndSaveRemotePosts() {
        return mPostsRemoteDataSource
                .getPosts()
                .flatMap(new Func1<List<Post>, Observable<List<Post>>>() {
                    @Override
                    public Observable<List<Post>> call(List<Post> posts) {
                        return Observable.from(posts).doOnNext(post -> {
                            mPostsLocalDataSource.savePost(post);
                            mCachedPosts.put(post.getId(), post);
                        }).toList();
                    }
                })
                .doOnCompleted(() -> mCacheIsDirty = false);
    }

    @Override
    public Observable<Post> getPost(@NonNull String postId) {
        checkNotNull(postId);

        final Post cachedPost = getPostWithId(postId);
        if (cachedPost != null) {
            return Observable.just(cachedPost);
        }

        if (mCachedPosts == null) {
            mCachedPosts = new LinkedHashMap<>();
        }

        Observable<Post> localPost = getPostWithIdFromLocalRepository(postId);
        Observable<Post> remotePost = mPostsRemoteDataSource
                .getPost(postId)
                .doOnNext(post -> {
                    mPostsLocalDataSource.savePost(post);
                    mCachedPosts.put(post.getId(), post);
                });

        return Observable.concat(localPost, remotePost).first()
                .map(post -> {
                    if (post == null) {
                        throw new NoSuchElementException("No task found with postId " + postId);
                    }
                    return post;
                });
    }

    @Override
    public void savePost(@NonNull Post post) {
        checkNotNull(post);
        mPostsRemoteDataSource.savePost(post);
        mPostsLocalDataSource.savePost(post);

        if (mCachedPosts == null) {
            mCachedPosts = new LinkedHashMap<>();
        }
        mCachedPosts.put(post.getId(), post);
    }

    @Override
    public void refreshPosts() {
        mCacheIsDirty = true;
    }

    @Nullable
    private Post getPostWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedPosts == null || mCachedPosts.isEmpty()) {
            return null;
        } else {
            return mCachedPosts.get(id);
        }
    }

    @NonNull
    Observable<Post> getPostWithIdFromLocalRepository(@NonNull final String postId) {
        return mPostsLocalDataSource
                .getPost(postId)
                .doOnNext(post -> mCachedPosts.put(postId, post))
                .first();
    }
}
