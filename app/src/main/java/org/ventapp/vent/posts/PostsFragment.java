package org.ventapp.vent.posts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ventapp.vent.R;
import org.ventapp.vent.data.Post;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class PostsFragment extends Fragment implements PostsContract.View {

    private PostsContract.Presenter mPresenter;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance() {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void setPresenter(@NonNull PostsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void setLoadingIndicator(boolean visible) {
        // TODO
    }

    @Override
    public void showPosts(List<Post> posts) {

    }

    @Override
    public void showNoPosts() {

    }

    @Override
    public void showLoadingPostsError() {

    }
}
