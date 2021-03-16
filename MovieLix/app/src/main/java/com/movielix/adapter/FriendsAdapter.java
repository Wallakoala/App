package com.movielix.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.movielix.FriendActivity;
import com.movielix.R;
import com.movielix.bean.User;
import com.movielix.constants.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * RecyclerView adapter to display friends.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendHolder> {

    private final Context mContext;
    private final List<User> mFriends;

    public FriendsAdapter(final List<User> friends, final Context context) {
        mFriends = friends;
        mContext = context;
    }

    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_item
                        , parent
                        , false);

        return new FriendHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendHolder holder, int position) {
        holder.bindFriendItem(mFriends.get(position));
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    /**
     * Holder responsible to set all the attributes of this specific friend.
     */
    class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private User mFriend;

        private CircleImageView mProfilePic;
        private TextView mName;
        private Button mFollow;

        FriendHolder(@NonNull View itemView) {
            super(itemView);

            mProfilePic = itemView.findViewById(R.id.friend_profile_pic);
            mName = itemView.findViewById(R.id.friend_name);
            mFollow = itemView.findViewById(R.id.friend_add_button);

            itemView.setOnClickListener(this);
        }

        void bindFriendItem(final User friend) {
            Picasso.get()
                    .load(friend.getPhotoUrl())
                    .error(R.drawable.ic_default_profile_pic)
                    .into(mProfilePic);

            mName.setText(friend.getName());

            mFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // todo #31
                }
            });

            mFriend = friend;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, FriendActivity.class);
            intent.putExtra(Constants.FRIEND_ID, mFriend.getId());
            intent.putExtra(Constants.FRIEND_NAME, mFriend.getName());
            intent.putExtra(Constants.FRIEND_PROFILE_PIC, mFriend.getPhotoUrl());
            mContext.startActivity(intent);
        }
    }
}
