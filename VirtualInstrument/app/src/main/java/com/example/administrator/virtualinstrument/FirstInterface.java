package com.example.administrator.virtualinstrument;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import java.util.concurrent.TimeUnit;

public class FirstInterface extends AppCompatActivity {
    private ImageView p1,p2,p3,p4,p5,redbtn,purplebtn,bluebtn;
    private int screenWidth,sceenHeight;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_interface);
        getMetric();
        initFirstInterface();
    }
    private void initFirstInterface(){
        p1= (ImageView) findViewById(R.id.p1View);
        p2= (ImageView) findViewById(R.id.p2View);
        p3= (ImageView) findViewById(R.id.p3View);
        p4= (ImageView) findViewById(R.id.p4View);
        p5= (ImageView) findViewById(R.id.p5View);
        redbtn= (ImageView) findViewById(R.id.redBtn);
        purplebtn= (ImageView) findViewById(R.id.purpleBtn);
        bluebtn= (ImageView) findViewById(R.id.blueBtn);
        p1.setX((screenWidth * 3) / 11);
        p2.setX((screenWidth * 4) / 11);
        p3.setX((screenWidth * 5) / 11);
        p4.setX((screenWidth * 6) / 11);
        p5.setX((screenWidth * 7) / 11);
        p1.setY((sceenHeight / 5) * 2);
        p2.setY((sceenHeight / 5) * 2);
        p3.setY((sceenHeight / 5) * 2);
        p4.setY((sceenHeight / 5) * 2);
        p5.setY((sceenHeight / 5) * 2);
        p1.setVisibility(View.INVISIBLE);
        p2.setVisibility(View.INVISIBLE);
        p3.setVisibility(View.INVISIBLE);
        p4.setVisibility(View.INVISIBLE);
        p5.setVisibility(View.INVISIBLE);
        redbtn.setVisibility(View.INVISIBLE);
        bluebtn.setVisibility(View.INVISIBLE);
        purplebtn.setVisibility(View.INVISIBLE);
        setAnimation();
        redbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                color=2;
//                DetectionBasedTracker.ChangeColor(color);
                Intent intent1 = new Intent();
                intent1.setClass(FirstInterface.this, ChoiceInterface.class);
                intent1.putExtra("color",2);
                FirstInterface.this.startActivity(intent1);
                FirstInterface.this.finish();
            }
        });
        purplebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                color=0;
//                DetectionBasedTracker.ChangeColor(color);
                Intent intent1 = new Intent();
                intent1.setClass(FirstInterface.this, ChoiceInterface.class);
                intent1.putExtra("color",0);
                FirstInterface.this.startActivity(intent1);
                FirstInterface.this.finish();
            }
        });
        bluebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                color=1;
//                DetectionBasedTracker.ChangeColor(color);
                Intent intent1 = new Intent();
                intent1.setClass(FirstInterface.this, ChoiceInterface.class);
                intent1.putExtra("color",1);
                FirstInterface.this.startActivity(intent1);
                FirstInterface.this.finish();
            }
        });
    }
    private void setAnimation(){
        Animation alphaAnimation=new AlphaAnimation(0.0f,1.0f);
        alphaAnimation.setDuration(1000);
        p1.startAnimation(alphaAnimation);
        p2.startAnimation(alphaAnimation);
        p3.startAnimation(alphaAnimation);
        p4.startAnimation(alphaAnimation);
        p5.startAnimation(alphaAnimation);
        redbtn.startAnimation(alphaAnimation);
        purplebtn.startAnimation(alphaAnimation);
        bluebtn.startAnimation(alphaAnimation);

        p1.setVisibility(View.VISIBLE);
        p2.setVisibility(View.VISIBLE);
        p3.setVisibility(View.VISIBLE);
        p4.setVisibility(View.VISIBLE);
        p5.setVisibility(View.VISIBLE);
        redbtn.setVisibility(View.VISIBLE);
        bluebtn.setVisibility(View.VISIBLE);
        purplebtn.setVisibility(View.VISIBLE);
    }
    private void getMetric(){
        DisplayMetrics displayMetrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        sceenHeight=displayMetrics.heightPixels;
        screenWidth=displayMetrics.widthPixels;
        Log.i("height",sceenHeight+"");
        Log.i("width",screenWidth+"");
    }
}
