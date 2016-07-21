package com.example.administrator.virtualinstrument;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


import java.io.InputStream;

/**
 * Created by Administrator on 2016/7/14.
 */
public class PositionSet extends AppCompatActivity implements View.OnClickListener {
    private int drumPosition[] = new int[4];
    //四个鼓的顺序,鼓一顺序=drumPosition[0],鼓二顺序=drumposition[1]....
    private int i = 0;
    //    private PositionSet positionSet=new PositionSet(0,0);

    private int color;
    private ImageView drum[] = new ImageView[4];
    private ImageView circle[] = new ImageView[4];
    private Bitmap drumBitmap[] = new Bitmap[4];
    private Bitmap circleBitmap[] = new Bitmap[4];
    private static final int DRUMKEY[] = {0, 46, 85, 120, 175};
    private DrumAcceleration Drum;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.positionset_interface);
//        iv = (ImageView) findViewById(R.id.instrument);
        initPlay();


        //这里的接收应该要改,就先用固定的了
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        color = bundle.getInt("color", 0);
        Drum = new DrumAcceleration(DRUMKEY, 25, 12, 70);

    }

    public Bitmap readBitmap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    protected void changeToBitmap() {
        drumBitmap[0]=readBitmap(this,R.drawable.bassr);
        drumBitmap[1]=readBitmap(this,R.drawable.ridey);
        drumBitmap[2]=readBitmap(this,R.drawable.snareb);
        drumBitmap[3]=readBitmap(this,R.drawable.tomp);
        circleBitmap[0]=readBitmap(this,R.drawable.bassrb);
        circleBitmap[1]=readBitmap(this,R.drawable.rideyb);
        circleBitmap[2]=readBitmap(this,R.drawable.snarebb);
        circleBitmap[3]=readBitmap(this,R.drawable.tompb);
    }

    private void initPlay() {
//        Log.i(TAG, "playing drum");
        //iv.setImageResource(R.drawable.drum);
        changeToBitmap();
        drum[0] = (ImageView) findViewById(R.id.drum11);
        drum[1] = (ImageView) findViewById(R.id.drum12);
        drum[2] = (ImageView) findViewById(R.id.drum13);
        drum[3] = (ImageView) findViewById(R.id.drum14);
//        circle[0] = (ImageView) findViewById(R.id.circle11);
//        circle[1] = (ImageView) findViewById(R.id.circle12);
//        circle[2] = (ImageView) findViewById(R.id.circle13);
//        circle[3] = (ImageView) findViewById(R.id.circle14);
        circle[0] = (ImageView) findViewById(R.id.circlebg1);
        circle[1] = (ImageView) findViewById(R.id.circlebg2);
        circle[2] = (ImageView) findViewById(R.id.circlebg3);
        circle[3] = (ImageView) findViewById(R.id.circlebg4);
//        circle[0].setBackgroundColor(Color.RED);
        circle[0].setImageBitmap(circleBitmap[0]);
        circle[1].setImageBitmap(circleBitmap[1]);
        circle[2].setImageBitmap(circleBitmap[2]);
        circle[3].setImageBitmap(circleBitmap[3]);
        for (int i = 0; i < 4; i++) {
            circle[i].setVisibility(View.INVISIBLE);
            drum[i].setOnClickListener(this);
        }
        circle[0].setVisibility(View.VISIBLE);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent1 = new Intent();
            intent1.setClass(PositionSet.this, ChoiceInterface.class);
            PositionSet.this.startActivity(intent1);
            PositionSet.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.drum11:
                drum[0].setImageBitmap(drumBitmap[i]);
//                drum[0].setBackgroundColor(Color.RED);
//                drum[0].setImageBitmap();
//                circle[i].setBackgroundColor(Color.TRANSPARENT);
                circle[i].setVisibility(View.INVISIBLE);
                drumPosition[i++]=0;
//                circle[i].setBackgroundColor(Color.RED);
                break;
            case R.id.drum12:
                drum[1].setImageBitmap(drumBitmap[i]);
//                drum[1].setBackgroundColor(Color.RED);
                //                drum[0].setImageBitmap();
//                circle[i].setBackgroundColor(Color.TRANSPARENT);
                circle[i].setVisibility(View.INVISIBLE);
                drumPosition[i++]=1;

                break;
            case R.id.drum13:
                drum[2].setImageBitmap(drumBitmap[i]);
//                drum[2].setBackgroundColor(Color.RED);
                //                drum[0].setImageBitmap();
//                circle[i].setBackgroundColor(Color.TRANSPARENT);
                circle[i].setVisibility(View.INVISIBLE);
                drumPosition[i++]=2;
//                circle[i].setBackgroundColor(Color.RED);
                break;
            case R.id.drum14:
                drum[3].setImageBitmap(drumBitmap[i]);
//                drum[3].setBackgroundColor(Color.RED);
                //                drum[0].setImageBitmap();
//                circle[i].setBackgroundColor(Color.TRANSPARENT);
                circle[i].setVisibility(View.INVISIBLE);
                drumPosition[i++]=3;

//                circle[i].setBackgroundColor(Color.RED);
                break;
        }
        if(i==4){
            Intent intent1=new Intent();
            intent1.setClass(PositionSet.this, DrumPlay.class);
            Bundle bundle=new Bundle();
            bundle.putInt("color", color);
            bundle.putInt("drum0",drumPosition[0]);
            bundle.putInt("drum1",drumPosition[1]);
            bundle.putInt("drum2",drumPosition[2]);
            bundle.putInt("drum3",drumPosition[3]);
            intent1.putExtras(bundle);
            PositionSet.this.startActivity(intent1);
        }
        else {
//            circle[i].setBackgroundColor(Color.RED);
            circle[i].setVisibility(View.VISIBLE);
        }
    }
}
