package com.movielix.bean;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * User class.
 */

public class User {

    private int mId;

    private String mEmail;
    private String mName;

    private List<User> mFriends;

    private User() {}

    /**
     * Builder class for constructing a User.
     */
    public static class Builder {

        private int id;

        private String email;
        private String name;

        private List<User> friends;

        public Builder(int id) {
            this.id = id;
        }

        public Builder withEmail(final String email) {
            this.email = email;

            return this;
        }

        public Builder named(final String name) {
            this.name = name;

            return this;
        }

        public Builder withFriends(final List<User> friends) {
            this.friends = friends;

            return this;
        }

        public User build() {
            User user = new User();

            user.mId = this.id;
            user.mEmail = this.email;
            user.mName = this.name;
            user.mFriends = this.friends;

            return user;
        }
    }
}
