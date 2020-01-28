package com.movielix.behavior;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior
{
    private YoYo.YoYoString mSlideOutAnimation;

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs)
    {
        // This is mandatory if we're assigning the behavior straight from XML
        super();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull final CoordinatorLayout coordinatorLayout, @NonNull  final FloatingActionButton child, @NonNull final View directTargetChild, @NonNull final View target, final int nestedScrollAxes, int type)
    {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes, type);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull final FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type)
    {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {

            if (mSlideOutAnimation == null || !mSlideOutAnimation.isRunning())
            {
                mSlideOutAnimation = YoYo.with(Techniques.SlideOutDown)
                    .duration(175)
                    .interpolate(new AccelerateInterpolator())
                    .onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            child.setVisibility(View.INVISIBLE);
                        }
                    })
                    .playOn(child);
            }

        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            child.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInUp)
                    .duration(175)
                    .interpolate(new DecelerateInterpolator())
                    .playOn(child);
        }
    }
}
