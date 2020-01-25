package com.movielix.validator;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import com.movielix.view.TextInputLayout;

public interface Validator {

    boolean validate(@NonNull Activity activity, @NonNull AppCompatEditText editText, @NonNull TextInputLayout textInputLayout);
}
