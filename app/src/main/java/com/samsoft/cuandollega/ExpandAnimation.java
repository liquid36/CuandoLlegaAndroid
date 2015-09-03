package com.samsoft.cuandollega;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sam on 24/09/14.
 */

public class ExpandAnimation {

    public static void expand(View summary,int width, int time) {
        //set Visible
        summary.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 11) {
            final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            summary.measure(widthSpec, width);
            ValueAnimator mAnimator = slideAnimator(0, width, summary);
            mAnimator.setDuration(time);
            mAnimator.start();
        }
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

    public static void Fade(View v, float a,float b) {
        if (Build.VERSION.SDK_INT >= 11) {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, "alpha", a, b);
            fadeOut.setDuration(2000);
            final AnimatorSet mAnimationSet = new AnimatorSet();
            mAnimationSet.play(fadeOut);
            mAnimationSet.start();
        } else {
            v.setAlpha(b);
        }
    }

    public static Integer strToInteger(String s,Integer n)
    {
        Integer  i = 0, j = 0;
        s = s.trim();
        Integer Valor = 0;
        while (j < n) {
            i = 0;
            while (!Character.isDigit(s.charAt(0))) s = s.substring(1);
            while (Character.isDigit(s.charAt(i))) i++;
            Valor =  Integer.parseInt(s.substring(0, i));
            j++;
            s = s.substring(i+1);
        }
        return Valor;
    }

}

