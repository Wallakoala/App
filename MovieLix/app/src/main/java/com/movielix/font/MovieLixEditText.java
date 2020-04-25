package com.movielix.font;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;

import com.movielix.R;

/**
 * EditText con la fuente actualizada.
 * Created by Daniel Mancebo Aldea on 14/10/2016.
 */
public class MovieLixEditText extends AppCompatEditText {

    public MovieLixEditText(Context context)
    {
        super(context);
    }

    public MovieLixEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public MovieLixEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.MovieLixEditText);
        String customFont = a.getString(R.styleable.MovieLixEditText_customFontEd);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public void setCustomFont(Context ctx, String asset) {
        setTypeface(TypeFace.getTypeFace(ctx, asset));
    }
}
