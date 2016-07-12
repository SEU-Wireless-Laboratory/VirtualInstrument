package com.example.administrator.virtualinstrument;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    private int color;//purple0,blue1,red2
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.first_interface);
    }

    private void chooseColor(float x,float y){
        Intent intent1=new Intent();
        intent1.setClass(FirstInterface.this, SwitchInterface.class);
        Bundle bundle=new Bundle();
        //choose purple
        if(y+x>1634&&y+x<1921&&y-x>1291&&y-x<1574){
            color=0;
            bundle.putInt("color",color);
            intent1.putExtras(bundle);
            FirstInterface.this.startActivity(intent1);
            FirstInterface.this.finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
        //choose blue
        else if(y+x>1960&&y+x<2238&&y-x>1294&&y-x<1577){
            color= 1;
            bundle.putInt("color",color);
            intent1.putExtras(bundle);
            FirstInterface.this.startActivity(intent1);
            FirstInterface.this.finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
        //choose red
        else if(y+x>1955&&y+x<2246&&y-x>962&&y-x<1253){
            color= 2;
            bundle.putInt("color",color);
            intent1.putExtras(bundle);
            FirstInterface.this.startActivity(intent1);
            FirstInterface.this.finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
        else if(x>72&&x<152&&y>52&&y<226){
            ActivityManager activityMgr= (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(getPackageName());
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            float x=event.getX();
            float y=event.getY();
            chooseColor(x,y);
        }
        else if(event.getAction()==MotionEvent.ACTION_UP)
        {

//            Intent intent1=new Intent();
//            intent1.setClass(FirstInterface.this, SwitchInterface.class);
//            Bundle bundle=new Bundle();
//            bundle.putInt("color",color);
//            intent1.putExtras(bundle);
//            FirstInterface.this.startActivity(intent1);
//            FirstInterface.this.finish();
        }
        else if(event.getAction()== MotionEvent.ACTION_MOVE){

        }
//        synchronized(this){
//            try{
//                this.wait(100);     //让事件线程休眠 减少触发次数
//            }catch(InterruptedException e){
//                e.printStackTrace();
//            }
//        }
        return true;
    }
}
