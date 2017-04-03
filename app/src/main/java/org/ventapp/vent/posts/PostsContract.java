package org.ventapp.vent.posts;

import org.ventapp.vent.BasePresenter;
import org.ventapp.vent.BaseView;
import org.ventapp.vent.data.Post;

import java.util.List;

public interface PostsContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean visible);

        void showPosts(List<Post> posts);

        void showNoPosts();

        void showLoadingPostsError();
    }

    interface Presenter extends BasePresenter {

        void loadPosts(boolean forceUpdate);
    }
}
