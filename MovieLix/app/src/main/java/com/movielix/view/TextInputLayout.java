package com.movielix.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom TextInputLayout
 */
public class TextInputLayout extends com.google.android.material.textfield.TextInputLayout
{
    public TextInputLayout(Context context) {
        super(context);
    }

    public TextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setErrorEnabled(boolean enabled) {
        super.setErrorEnabled(enabled);
        if (enabled) {
            return;
        }
        if (getChildCount() > 1) {
            View view = getChildAt(1);
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }
}
