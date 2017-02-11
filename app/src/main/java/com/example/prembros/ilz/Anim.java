package com.example.prembros.ilz;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.daimajia.slider.library.SliderLayout;

/**
 * Created by Prem $ on 2/11/2017.
 */

public class Anim extends AppCompatActivity{

    SliderLayout sliderLayout;
    ImageButton fab;
    LinearLayout revealView;
    Button button1, button2;
    Animation alphaAnimation;
    float pixelDensity;
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pixelDensity = getResources().getDisplayMetrics().density;

        sliderLayout = (SliderLayout) findViewById(R.id.slider);
        fab = (ImageButton) findViewById(R.id.fab_add_to_cart);
        revealView = (LinearLayout) findViewById(R.id.item_added_notification);
        button1 = (Button) findViewById(R.id.button_view_cart);
        button2 = (Button) findViewById(R.id.button_back);

        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.button);
    }

    public void clickAnimation(View view) {

        /*
         MARGIN_RIGHT = 16;
         FAB_BUTTON_RADIUS = 28;
         */
        int x = sliderLayout.getRight();
        int y = sliderLayout.getBottom();
        x -= ((28 * pixelDensity) + (16 * pixelDensity));

        int hypotenuse = (int) Math.hypot(sliderLayout.getWidth(), sliderLayout.getHeight());

        if (flag) {

            fab.setBackgroundResource(R.drawable.rounded_cancel_button);
            fab.setImageResource(R.drawable.ic_close);

            FrameLayout.LayoutParams parameters = (FrameLayout.LayoutParams)
                    revealView.getLayoutParams();
            parameters.height = sliderLayout.getHeight();
            revealView.setLayoutParams(parameters);

            Animator anim;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(revealView, x, y, 0, hypotenuse);
                anim.setDuration(700);

                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        button1.setVisibility(View.VISIBLE);
                        button1.startAnimation(alphaAnimation);
                        button2.setVisibility(View.VISIBLE);
                        button2.startAnimation(alphaAnimation);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });

                revealView.setVisibility(View.VISIBLE);
                anim.start();
            }
            flag = false;
        } else {

            fab.setBackgroundResource(R.drawable.rounded_button);
            fab.setImageResource(R.drawable.ic_add_shopping_cart);

            Animator anim;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(revealView, x, y, hypotenuse, 0);
                anim.setDuration(400);

                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        revealView.setVisibility(View.GONE);
                        button1.setVisibility(View.GONE);
                        button2.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });

                anim.start();
            }
            flag = true;
        }
    }
}
