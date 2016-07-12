package com.example.administrator.virtualinstrument;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        Intent intent=getIntent();
        color=intent.getIntExtra("color",0);
        loadImage(color);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.choice_interface);
        initChoiceInterface();
//        iv= (ImageView) findViewById(R.id.imageView2);
//        Intent intent=getIntent();
//        color=intent.getIntExtra("color",0);
//        loadImage(color);
    }
//    private void chooseInstru(float x,float y){
//        //piano kind=0,drum=1
//        Intent intent1=new Intent();
//        intent1.setClass(ChoiceInterface.this, PianoPlay.class);
//        Intent intent2=new Intent();
//        intent2.setClass(ChoiceInterface.this, DrumPlay.class);
//        Bundle bundle=new Bundle();
//        //choose xylophone
//        if(x>632&&x<700&&y>252&&y<826){
//            bundle.putInt("kind",0);
//            bundle.putInt("color",color);
//            intent1.putExtras(bundle);
//            ChoiceInterface.this.startActivity(intent1);
//            ChoiceInterface.this.finish();
//        }
//        //choose drum
//        else if(x>405&&x<472&&y>258&&y<510){
//            bundle.putInt("kind",1 );
//            bundle.putInt("color",color);
//            intent2.putExtras(bundle);
//            ChoiceInterface.this.startActivity(intent2);
//            ChoiceInterface.this.finish();
//
//        }
//        //choose back
//        else if(x>152&&x<222&&y>258&&y<506){
//            Intent intent3=new Intent();
//            intent3.setClass(ChoiceInterface.this, FirstInterface.class);
//            ChoiceInterface.this.startActivity(intent3);
//            ChoiceInterface.this.finish();
//        }
//    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(event.getAction()==MotionEvent.ACTION_DOWN){
//            float x=event.getX();
//            float y=event.getY();
//            chooseInstru(x, y);
//        }
//        else if(event.getAction()==MotionEvent.ACTION_UP)
//        {
//
////            Intent intent1=new Intent();
////            intent1.setClass(FirstInterface.this, SwitchInterface.class);
////            Bundle bundle=new Bundle();
////            bundle.putInt("color",color);
////            intent1.putExtras(bundle);
////            FirstInterface.this.startActivity(intent1);
////            FirstInterface.this.finish();
//        }
//        else if(event.getAction()== MotionEvent.ACTION_MOVE){
//
//        }
//        return true;
//    }
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
//    public Bitmap readBitmap(Context context, int resId){
//        BitmapFactory.Options opt = new BitmapFactory.Options();
//        opt.inPreferredConfig = Bitmap.Config.RGB_565;
//        // 获取资源图片
//        InputStream is = context.getResources().openRawResource(resId);
//        return BitmapFactory.decodeStream(is, null, opt);
//    }
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
            Intent intent1=new Intent();
            intent1.setClass(ChoiceInterface.this, PianoPlay.class);
            Intent intent2=new Intent();
            intent2.setClass(ChoiceInterface.this, DrumPlay.class);
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

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
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
