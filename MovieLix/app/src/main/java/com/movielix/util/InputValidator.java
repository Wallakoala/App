package com.movielix.util;

import androidx.annotation.NonNull;

public class InputValidator {

    private static final int MIN_PASSWORD_LENGTH = 6;

    /**
     * Validates a person name.
     *
     * @param name: name to be validated.
     * @return true if the name is correct.
     */
    public static boolean isValidName(@NonNull final String name) {
        if (name.isEmpty()) {
            return false;
        }

        final char[] chars = name.toCharArray();

        for (char c : chars) {
            if (!Character.isLetter(c) && !Character.isSpaceChar(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates an email address.
     *
     * @param email: email address to be validated.
     * @return true if the email address is correcet.
     */
    public static boolean isValidEmail(@NonNull final String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validates the password.
     *
     * @param password: password to be validated.
     * @return true if the password is correct.
     */
    public static boolean isValidPassword(@NonNull final String password) {
        return (!password.trim().isEmpty() &&
                !(password.trim().length() < MIN_PASSWORD_LENGTH) &&
                !(password.toUpperCase().contains("SELECT")) && !(password.toUpperCase().contains("DROP")) &&
                !(password.toUpperCase().contains("DELETE")) && !(password.toUpperCase().contains("UPDATE")) &&
                !(password.contains("*")) && !(password.contains("/")) && !(password.contains("\\")) &&
                !(password.contains("=")) && !(password.contains("|")) && !(password.contains("&")) &&
                !(password.contains("'")) && !(password.contains("!")) && !(password.contains(";")));
    }
}
