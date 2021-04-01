package com.movielix.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.movielix.UserActivity;
import com.movielix.R;
import com.movielix.bean.User;
import com.movielix.constants.Constants;
import com.movielix.firestore.FirestoreConnector;
import com.movielix.firestore.FirestoreListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * RecyclerView adapter to display a list of users.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder> {

    private final Context mContext;
    private final List<User> mUsers;

    public UsersAdapter(final List<User> users, final Context context) {
        mUsers = users;
        mContext = context;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item
                        , parent
                        , false);

        return new UserHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.bindFriendItem(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    /**
     * Holder responsible to set all the attributes of this specific friend.
     */
    class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private User mUser;

        private final CircleImageView mProfilePic;
        private final TextView mName;
        private final Button mFollow;
        private final TextView mNumReviews;

        UserHolder(@NonNull View itemView) {
            super(itemView);

            mProfilePic = itemView.findViewById(R.id.user_profile_pic);
            mName = itemView.findViewById(R.id.user_name);
            mFollow = itemView.findViewById(R.id.user_add_button);
            mNumReviews = itemView.findViewById(R.id.user_num_reviews);

            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void bindFriendItem(final User user) {
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .error(R.drawable.ic_default_profile_pic)
                    .into(mProfilePic);

            mName.setText(user.getName());
            mNumReviews.setText(Integer.toString(user.getNumReviews()));

            mFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirestoreConnector.newInstance().follow(
                              Objects.requireNonNull(FirebaseAuth.getInstance().getUid())
                            , user.getId()
                            , new FirestoreListener<User>() {
                                @Override
                                public void onSuccess() {
                                    // todo notify upper layer that it succeeded
                                }

                                @Override
                                public void onSuccess(User item) { }

                                @Override
                                public void onSuccess(List<User> items) { }

                                @Override
                                public void onError() {
                                    // todo notify upper layer that it failed
                                }
                            });
                }
            });

            mUser = user;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, UserActivity.class);
            intent.putExtra(Constants.USER_ID, mUser.getId());
            intent.putExtra(Constants.USER_NAME, mUser.getName());
            intent.putExtra(Constants.USER_PROFILE_PIC, mUser.getPhotoUrl());
            intent.putExtra(Constants.USER_NUM_REVIEWS, mUser.getNumReviews());
            mContext.startActivity(intent);
        }
    }
}
