package com.movielix.validator;

import android.app.Activity;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import com.movielix.R;
import com.movielix.view.TextInputLayout;

public class EmailValidator implements Validator {

    private Activity activity;

    private AppCompatEditText editText;
    private TextInputLayout textInputLayout;

    public EmailValidator(@NonNull Activity activity, @NonNull AppCompatEditText editText, @NonNull TextInputLayout textInputLayout) {
        this.activity = activity;
        this.editText = editText;
        this.textInputLayout = textInputLayout;
    }

    /**
     * Checks whether the password is valid or not, and updates the UI accordingly.
     *
     * @return true if the password is correct.
     */
    @Override
    public boolean validate() {
        Editable email = editText.getText();

        if ((email == null) || !InputValidator.isValidEmail(email.toString())) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(activity.getResources().getString(R.string.wrong_email));

            requestFocus(activity, editText);

            return false;

        } else {
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(Activity activity, @NonNull View view) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
