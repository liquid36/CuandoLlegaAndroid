package com.samsoft.cuandollega;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sam on 24/09/14.
 */

public class ExpandAnimation {

    public static void expand(View summary,int width, int time) {
        //set Visible
        summary.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        summary.measure(widthSpec, width);
        ValueAnimator mAnimator = slideAnimator(0, width, summary);
        mAnimator.setDuration(time);
        mAnimator.start();
    }

    public static ValueAnimator slideAnimator(int start, int end, final View summary) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = summary.getLayoutParams();
                layoutParams.height = value;
                summary.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

}
