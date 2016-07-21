package com.example.administrator.virtualinstrument;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/3/28.
 */
public class ChoiceInterface extends AppCompatActivity implements View.OnClickListener {
    private ImageView xylbtn,drumbtn,backbtn;
    Animation animationup,animationdown;
//    private ImageView iv;
//    private Bitmap bitmap;
    int color,kind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent intent = getIntent();
        color = intent.getIntExtra("color", 0);
        loadImage(color);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.choice_interface);
        initChoiceInterface();
    }
    private void loadImage(int color){
        switch (color){
            case 0:
                getWindow().setBackgroundDrawableResource(R.drawable.instrupurple1);
//                bitmap=readBitmap(this,R.drawable.instrupurple);
                break;
            case 1:
                getWindow().setBackgroundDrawableResource(R.drawable.instrublue1);
//                bitmap=readBitmap(this,R.drawable.instrublue);
                break;
            case 2:
                getWindow().setBackgroundDrawableResource(R.drawable.instrured1);
//                bitmap=readBitmap(this,R.drawable.instrured);
                break;
        }

//        iv.setImageBitmap(bitmap);
    }

    private void initChoiceInterface(){
        xylbtn= (ImageView) findViewById(R.id.xyl);
        backbtn= (ImageView) findViewById(R.id.back);
        drumbtn= (ImageView) findViewById(R.id.drum);
        xylbtn.setOnClickListener(this);
        drumbtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        animationup= AnimationUtils.loadAnimation(ChoiceInterface.this, R.anim.translateup);
//        animationdown= AnimationUtils.loadAnimation(ChoiceInterface.this, R.anim.translatedown);
        animationup.setAnimationListener(new chooseAnilistener());
        animationup.setFillAfter(false);
//        animationdown.setFillAfter(true);
//        iv.startAnimation(animation);

    }
    private class chooseAnilistener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d("1", "onAnimationEnd: ");
            Handler mHandler=new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent1=new Intent();
                    intent1.setClass(ChoiceInterface.this, PianoPlay.class);
                    Intent intent2=new Intent();
                    intent2.setClass(ChoiceInterface.this, PositionSet.class);
                    Bundle bundle=new Bundle();

                    switch (kind){
                        case 0:
//                    xylbtn.startAnimation(animationdown);
                            bundle.putInt("kind",kind);
                            bundle.putInt("color", color);
                            intent1.putExtras(bundle);
                            ChoiceInterface.this.startActivity(intent1);
                            break;
                        case 1:
                            bundle.putInt("kind",kind );
                            bundle.putInt("color", color);
                            intent2.putExtras(bundle);
                            ChoiceInterface.this.startActivity(intent2);
                            break;
                        case 2:
                            Intent intent3=new Intent();
                            intent3.setClass(ChoiceInterface.this, FirstInterface.class);
                            ChoiceInterface.this.startActivity(intent3);
                            break;
                    }
                    ChoiceInterface.this.finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }


        },100);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent1=new Intent();
            intent1.setClass(ChoiceInterface.this, FirstInterface.class);
            ChoiceInterface.this.startActivity(intent1);
            ChoiceInterface.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
    @Override
    public void onClick(View view) {
//        Intent intent1=new Intent();
//        intent1.setClass(ChoiceInterface.this, PianoPlay.class);
//        Intent intent2=new Intent();
//        intent2.setClass(ChoiceInterface.this, DrumPlay.class);
//        Bundle bundle=new Bundle();
        //piano kind=0,drum=1
        switch (view.getId()){
            case R.id.xyl:
                kind=0;
                xylbtn.startAnimation(animationup);
//                xylbtn.startAnimation(animationdown);

//                bundle.putInt("kind",kind);
//                bundle.putInt("color",color);
//                intent1.putExtras(bundle);
//                ChoiceInterface.this.startActivity(intent1);
//                ChoiceInterface.this.finish();
                break;
            case R.id.drum:
                kind=1;
                drumbtn.startAnimation(animationup);
//                drumbtn.startAnimation(animationdown);

//                bundle.putInt("kind",kind );
//                bundle.putInt("color",color);
//                intent2.putExtras(bundle);
//                ChoiceInterface.this.startActivity(intent2);
//                ChoiceInterface.this.finish();
                break;
            case R.id.back:
                kind=2;
                backbtn.startAnimation(animationup);
//                backbtn.startAnimation(animationdown);
//                Intent intent3=new Intent();
//                intent3.setClass(ChoiceInterface.this, FirstInterface.class);
//                ChoiceInterface.this.startActivity(intent3);
//                ChoiceInterface.this.finish();
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

}
