package com.example.phamn.diamondmine;

import android.media.Image;
import android.os.CountDownTimer;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class Effect {
    public void dropImage(ImageView iv, int duration, int fromY) {
        Animation anim = new TranslateAnimation(0, 0, fromY, 0);
        anim.setDuration(duration);
        iv.startAnimation(anim);
    }

    public void translateImage(ImageView iv1, ImageView iv2, int slideDirection, boolean comeBack) {
        Animation anim1;
        Animation anim2;
        if (slideDirection == 12) {
            anim1 = new TranslateAnimation(0, 68, 0, 0);
            anim1.setDuration(450);
            anim2 = new TranslateAnimation(0, -68, 0, 0);
            anim2.setDuration(450);
            if (comeBack) {
                anim1.setRepeatCount(1);
                anim1.setRepeatMode(2);
                anim2.setRepeatCount(1);
                anim2.setRepeatMode(2);
                iv1.startAnimation(anim1);
                iv2.startAnimation(anim2);
            } else {
                //anim1.setFillAfter(true);
                anim2.setFillAfter(true);
                iv2.startAnimation(anim2);
            }
        }
        if (slideDirection == 21) {
            anim1 = new TranslateAnimation(0, -68, 0, 0);
            anim1.setDuration(450);
            anim2 = new TranslateAnimation(0, 68, 0, 0);
            anim2.setDuration(450);
            if (comeBack) {
                anim1.setRepeatCount(1);
                anim1.setRepeatMode(2);
                anim2.setRepeatCount(1);
                anim2.setRepeatMode(2);
            } else {
                //anim1.setFillAfter(true);
                anim2.setFillAfter(true);
            }
            iv1.startAnimation(anim1);
            iv2.startAnimation(anim2);
        }
        if (slideDirection == 13) {
            anim1 = new TranslateAnimation(0, 0, 0, 68);
            anim1.setDuration(450);
            anim2 = new TranslateAnimation(0, 0, 0, -68);
            anim2.setDuration(450);
            if (comeBack) {
                anim1.setRepeatCount(1);
                anim1.setRepeatMode(2);
                anim2.setRepeatCount(1);
                anim2.setRepeatMode(2);
            } else {
                //anim1.setFillAfter(true);
                anim2.setFillAfter(true);
            }
            iv1.startAnimation(anim1);
            iv2.startAnimation(anim2);
        }
        if (slideDirection == 31) {
            anim1 = new TranslateAnimation(0, 0, 0, -68);
            anim1.setDuration(450);
            anim2 = new TranslateAnimation(0, 0, 0, 68);
            anim2.setDuration(450);
            if (comeBack) {
                anim1.setRepeatCount(1);
                anim1.setRepeatMode(2);
                anim2.setRepeatCount(1);
                anim2.setRepeatMode(2);
            } else {
                //anim1.setFillAfter(true);
                anim2.setFillAfter(true);
            }
            iv1.startAnimation(anim1);
            iv2.startAnimation(anim2);
        }
    }

    public void scaleImage(ImageView iv) {
        Animation anim = new ScaleAnimation(
                1f, 0.8f,      // Start and end values for the X axis scaling
                1f, 0.8f,    // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF,
                0.5f,         // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF,
                0.5f);        // Pivot point of Y scaling
        anim.setFillAfter(false); // Needed to keep the result of the animation
        anim.setRepeatMode(2);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(300);
        iv.startAnimation(anim);
    }

    public void alphaImage(ImageView iv) {
        Animation anim = new AlphaAnimation(1f, 0.2f);
        anim.setRepeatMode(2);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(500);
        iv.startAnimation(anim);
    }

    public void swapImage(final ImageView iv1, final ImageView iv2, final int image1, final int image2) {
        Animation anim1 = new ScaleAnimation(
                1f, 0.1f,      // Start and end values for the X axis scaling
                1f, 0.1f,    // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF,
                0.5f,         // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF,
                0.5f);        // Pivot point of Y scaling
        anim1.setFillAfter(true); // Needed to keep the result of the animation
        anim1.setRepeatMode(1);
        //anim1.setRepeatCount(1);
        anim1.setDuration(250);

        Animation anim2 = new ScaleAnimation(
                1f, 0.1f,      // Start and end values for the X axis scaling
                1f, 0.1f,    // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF,
                0.5f,         // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF,
                0.5f);        // Pivot point of Y scaling
        anim2.setFillAfter(true); // Needed to keep the result of the animation
        anim2.setRepeatMode(1);
        anim2.setDuration(250);

        iv1.startAnimation(anim1);
        iv2.startAnimation(anim2);

        CountDownTimer count = new CountDownTimer(300, 300) {
            @Override
            public void onTick(long l) {

            }
            @Override
            public void onFinish() {
                iv1.setImageResource(image2);
                iv2.setImageResource(image1);
                Animation anim3 = new ScaleAnimation(
                        0.1f, 1f,      // Start and end values for the X axis scaling
                        0.1f, 1f,    // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF,
                        0.5f,         // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF,
                        0.5f);        // Pivot point of Y scaling
                anim3.setFillAfter(true); // Needed to keep the result of the animation
                anim3.setRepeatMode(1);
                anim3.setDuration(250);

                Animation anim4 = new ScaleAnimation(
                        0.1f, 1f,      // Start and end values for the X axis scaling
                        0.1f, 1f,    // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF,
                        0.5f,         // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF,
                        0.5f);        // Pivot point of Y scaling
                anim4.setFillAfter(true); // Needed to keep the result of the animation
                anim4.setRepeatMode(1);
                anim4.setDuration(250);
                //anim4.setStartOffset(500);
                iv1.startAnimation(anim3);
                iv2.startAnimation(anim4);
            }
        };
        count.start();
    }
}
