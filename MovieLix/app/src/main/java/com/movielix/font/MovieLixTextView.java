package com.movielix.font;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.movielix.R;

/**
 * Custom TextView with our font.
 */
public class MovieLixTextView extends AppCompatTextView
{
    public MovieLixTextView(Context context)
    {
        super(context);
    }

    public MovieLixTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public MovieLixTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs)
    {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.MovieLixTextView);
        String customFont = a.getString(R.styleable.MovieLixTextView_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset)
    {
        setTypeface(TypeFace.getTypeFace(ctx, asset));

        return true;
    }
}
