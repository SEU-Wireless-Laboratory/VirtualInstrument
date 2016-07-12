package com.example.administrator.virtualinstrument;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/7/11.
 */
public class SwitchInterface extends AppCompatActivity {
    private ImageView iv;
    private Bitmap bitmap;
    int color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.switch_interface);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        iv= (ImageView) findViewById(R.id.imageView);
//        iv.setImageResource(R.drawable.blue);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        color=bundle.getInt("color");
        loadImage(color);
        Animation animation= AnimationUtils.loadAnimation(SwitchInterface.this, R.anim.scale);
        animation.setAnimationListener(new switchAnilistener());
        animation.setFillAfter(true);
        iv.startAnimation(animation);
    }
    private void loadImage(int color){
        switch (color){
            case 0:
                bitmap=readBitmap(this,R.drawable.purple);
                break;
            case 1:
                bitmap=readBitmap(this,R.drawable.blue);
                break;
            case 2:
                bitmap=readBitmap(this,R.drawable.red);
                break;
        }
        iv.setImageBitmap(bitmap);
    }
    public Bitmap readBitmap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
    private class switchAnilistener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Intent intent1=new Intent();
            intent1.setClass(SwitchInterface.this, ChoiceInterface.class);
            Bundle bundle=new Bundle();
            bundle.putInt("color",color);
            intent1.putExtras(bundle);
            SwitchInterface.this.startActivity(intent1);
            SwitchInterface.this.finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
