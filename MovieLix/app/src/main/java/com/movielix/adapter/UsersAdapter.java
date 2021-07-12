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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.movielix.UserActivity;
import com.movielix.R;
import com.movielix.bean.User;
import com.movielix.constants.Constants;
import com.movielix.firestore.FirestoreConnector;
import com.movielix.util.Tuple;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * RecyclerView adapter to display a list of users.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder> {

    private final Context mContext;
    private final List<Tuple<User, Boolean>> mUsers;

    public UsersAdapter(final List<Tuple<User, Boolean>> users, final Context context) {
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
        private boolean mFollowing;

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
        void bindFriendItem(final Tuple<User, Boolean> userWrapper) {
            mUser = userWrapper.x();
            mFollowing = userWrapper.y();

            Picasso.get()
                    .load(mUser.getPhotoUrl())
                    .error(R.drawable.ic_default_profile_pic)
                    .into(mProfilePic);

            mName.setText(mUser.getName());
            mNumReviews.setText(Integer.toString(mUser.getNumReviews()));

            updateButton();

            if (mFollowing) {
                mFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirestoreConnector.newInstance().unfollow(
                                Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), mUser.getId());

                        mFollowing = !mFollowing;
                        updateButton();
                    }
                });

            } else {
                mFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirestoreConnector.newInstance().follow(
                                  Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), mUser.getId());

                        mFollowing = !mFollowing;
                        updateButton();
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, UserActivity.class);

            intent.putExtra(Constants.USER_ID, mUser.getId());
            intent.putExtra(Constants.USER_NAME, mUser.getName());
            intent.putExtra(Constants.USER_PROFILE_PIC, mUser.getPhotoUrl());
            intent.putExtra(Constants.USER_NUM_REVIEWS, mUser.getNumReviews());
            intent.putExtra(Constants.USER_FOLLOWING, mFollowing);

            mContext.startActivity(intent);
        }

        private void updateButton() {
            if (mFollowing) {
                mFollow.setBackground(
                        ContextCompat.getDrawable(mContext, R.drawable.rounded_button_fill));
                mFollow.setTextColor(
                        mContext.getResources().getColor(android.R.color.black, mContext.getTheme()));
                mFollow.setText(
                        mContext.getResources().getText(R.string.friend_unfollow));

            } else {
                mFollow.setBackground(
                        ContextCompat.getDrawable(mContext, R.drawable.rounded_button_border_transparent));
                mFollow.setTextColor(
                        mContext.getResources().getColor(R.color.colorAccent, mContext.getTheme()));
                mFollow.setText(
                        mContext.getResources().getText(R.string.friend_follow));
            }
        }
    }
}
