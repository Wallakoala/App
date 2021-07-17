package com.movielix.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.movielix.MovieActivity;
import com.movielix.R;
import com.movielix.UserActivity;
import com.movielix.bean.User;
import com.movielix.constants.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * RecyclerView adapter to display users suggestions.
 */
public class UsersSuggestionAdapter extends RecyclerView.Adapter<UsersSuggestionAdapter.UserHolder> {

    private final Context mContext;
    private final List<User> mUsers;
    private final String mSearch;

    public UsersSuggestionAdapter(Context context, List<User> users, String search) {
        this.mContext = context;
        this.mUsers = users;
        this.mSearch = search;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_suggestion_item
                        , parent
                        , false);

        return new UserHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.bindUserItem(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    /**
     * Holder responsible to set all the attributes of this specific user.
     */
    class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private User mUser;

        private TextView mName;
        private CircleImageView mProfilePic;

        UserHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.user_suggestion_name);
            mProfilePic = itemView.findViewById(R.id.user_suggestion_profile_pic);

            itemView.setOnClickListener(this);
        }

        void bindUserItem(User user) {
            mUser = user;

            int colorAccent = mContext.getResources().getColor(R.color.colorAccent, mContext.getTheme());
            String highlightColor = String.format("%X", colorAccent).substring(2);

            String highlightedName = user.getName().replaceAll(
                    "(?i)" + Pattern.quote(mSearch)
                    , String.format("<font color=\"#%s\">" + mSearch + "</font>", highlightColor));

            mName.setText(Html.fromHtml(highlightedName));

            Picasso.get()
                   .load((mUser.getPhotoUrl().isEmpty()) ? null : mUser.getPhotoUrl())
                   .error(R.drawable.ic_default_profile_pic)
                   .into(mProfilePic);
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
