package com.movielix.bean;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User class.
 */
public class User {

    private int mId;

    private String mEmail;
    private String mName;

    private List<Integer> mFriends;
    private List<Integer> mReviews;

    private User() {}

    /**
     * Builder class for constructing a User.
     */
    public static class Builder {

        private int id;

        private String email;
        private String name;

        private List<Integer> friends = new ArrayList<>();
        private List<Integer> reviews = new ArrayList<>();

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

        public Builder withFriends(final List<Integer> friends) {
            this.friends = friends;

            return this;
        }

        public Builder hasReviewed(final List<Integer> reviews) {
            this.reviews = reviews;

            return this;
        }

        public User build() {
            User user = new User();

            user.mId = this.id;
            user.mEmail = this.email;
            user.mName = this.name;
            user.mFriends = this.friends;
            user.mReviews = this.reviews;

            return user;
        }
    }

    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("name", mName);
        map.put("name", mName);
        map.put("name", mName);
        map.put("name", mName);

        return map;
    }
}
