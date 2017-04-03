package org.ventapp.vent.posts;

import android.support.annotation.NonNull;

import org.ventapp.vent.data.Post;
import org.ventapp.vent.data.source.PostsRepository;
import org.ventapp.vent.util.schedulers.BaseSchedulerProvider;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

public class PostsPresenter implements PostsContract.Presenter {

    private final PostsRepository mPostsRepository;
    private final PostsContract.View mPostsView;
    private final BaseSchedulerProvider mSchedulerProvider;
    private final CompositeSubscription mSubscriptions;
    private boolean mFirstLoad = true;

    public PostsPresenter(@NonNull PostsRepository postsRepository, @NonNull PostsContract.View postsView, @NonNull BaseSchedulerProvider schedulerProvider) {
        mPostsRepository = checkNotNull(postsRepository, "postsRepository cannot be null");
        mPostsView = checkNotNull(postsView, "postsView cannot be null");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mPostsView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadPosts(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void loadPosts(boolean forceUpdate) {
        loadPosts(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    private void loadPosts(final boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mPostsView.setLoadingIndicator(true);
        }

        if (forceUpdate) {
            mPostsRepository.refreshPosts();
        }

//        EspressoIdlingResource.increment();

        mSubscriptions.clear();
        Subscription subscription = mPostsRepository
                .getPosts()
                .flatMap(new Func1<List<Post>, Observable<Post>>() {
                    @Override
                    public Observable<Post> call(List<Post> posts) {
                        return Observable.from(posts);
                    }
                })
                .toList()
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(
                        // onNext
                        this::processPosts,
                        // onError
                        throwable -> mPostsView.showLoadingPostsError(),
                        // onCompleted
                        () -> mPostsView.setLoadingIndicator(false));
        mSubscriptions.add(subscription);
    }

    private void processPosts(@NonNull List<Post> posts) {
        if (posts.isEmpty()) {
            processEmptyPosts();
        } else {
            mPostsView.showPosts(posts);
        }
    }

    private void processEmptyPosts() {
        mPostsView.showNoPosts();
    }
}
