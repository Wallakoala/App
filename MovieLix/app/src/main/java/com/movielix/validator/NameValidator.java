package com.movielix.validator;

import android.app.Activity;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import com.movielix.R;
import com.movielix.view.TextInputLayout;

public class NameValidator implements Validator {

    private final Activity activity;

    private final AppCompatEditText editText;
    private final TextInputLayout textInputLayout;

    public NameValidator(@NonNull Activity activity, @NonNull AppCompatEditText editText, @NonNull TextInputLayout textInputLayout) {
        this.activity = activity;
        this.editText = editText;
        this.textInputLayout = textInputLayout;
    }

    /**
     * Checks whether the name is valid or not, and updates the UI accordingly.
     *
     * @return true if the name is correct.
     */
    @Override
    public boolean validate() {
        Editable name = editText.getText();

        if ((name == null) || !InputValidator.isValidName(name.toString())) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(activity.getResources().getString(R.string.wrong_name));

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
