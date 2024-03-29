package com.movielix;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.movielix.adapter.UsersAdapter;
import com.movielix.bean.User;
import com.movielix.firestore.FirestoreConnector;
import com.movielix.firestore.IFirestoreListener;
import com.movielix.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyFriendsActivity extends AppCompatActivity {

    private enum RefreshType {
        SWIPE,
        DEFAULT
    }

    private ProgressBar mProgressBar;
    private TextView mMessageTextview;
    private RecyclerView mRecyclerView;
    private View mContainer;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_friends);

        mProgressBar = findViewById(R.id.my_friends_progress_bar);
        mMessageTextview = findViewById(R.id.my_friends_message_textview);
        mRecyclerView = findViewById(R.id.my_friends_recyclerview);
        mContainer = findViewById(R.id.my_friends_container);
        findViewById(R.id.my_friends_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.my_friends_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFriends(RefreshType.SWIPE);
            }
        });

        initializeFAB();
        hideMessage();

        getFriends(RefreshType.DEFAULT);
    }

    /**
     * Initializes the floating action button.
     */
    private void initializeFAB() {
        findViewById(R.id.add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyFriendsActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initializes the RecyclerView
     */
    private void initializeRecyclerView(final List<Tuple<User, Boolean>> users) {
        UsersAdapter friendsAdapter = new UsersAdapter(users, this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(friendsAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Method to retrieve the user's friends and show the RecyclerView.
     */
    private void getFriends(final RefreshType refreshType) {
        mSwipeRefreshLayout.setEnabled(false);
        mMessageTextview.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        FirestoreConnector.newInstance()
                .getFriends(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), new IFirestoreListener<User>() {
            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(User item) {}

            @Override
            public void onSuccess(final List<User> users) {
                if (refreshType == RefreshType.DEFAULT) {
                    hideProgressBar();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (users.isEmpty()) {
                    showMessage(getResources().getString(R.string.no_friends));
                } else {
                    List<Tuple<User, Boolean>> usersWrapper = new ArrayList<>();
                    for (User user: users) {
                        usersWrapper.add(new Tuple<>(user, true));
                    }

                    initializeRecyclerView(usersWrapper);
                }

                mSwipeRefreshLayout.setEnabled(true);
            }

            @Override
            public void onError() {
                hideProgressBar();
                hideMessage();

                Snackbar.make(mContainer, R.string.something_went_wrong, Snackbar.LENGTH_LONG).setAction("Reintentar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressBar();
                        getFriends(refreshType);
                    }
                }).show();

                mSwipeRefreshLayout.setEnabled(true);
            }
        });
    }

    private void showMessage(String message) {
        mMessageTextview.setText(message);
        mMessageTextview.setVisibility(View.VISIBLE);
    }

    private void hideMessage() {
        mMessageTextview.setVisibility(View.GONE);
    }

    /**
     * Shows the progress bar with an animation.
     */
    private void showProgressBar() {
        YoYo.with(Techniques.ZoomIn).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }).playOn(mProgressBar);
    }

    /**
     * Hides the progress bar with an animation.
     */
    private void hideProgressBar() {
        YoYo.with(Techniques.ZoomOut).playOn(mProgressBar);
    }
}
