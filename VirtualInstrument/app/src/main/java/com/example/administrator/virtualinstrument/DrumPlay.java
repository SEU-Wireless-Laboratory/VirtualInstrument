package com.example.administrator.virtualinstrument;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Chengzhi_Huang on 2016/7/2.
 */
public class DrumPlay extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;
    protected static final String TAG="DrumPlay";
    private ImageView drum[]=new ImageView[4];
    private ImageView circle[]=new ImageView[4];
    private Bitmap drumBitmap[]=new Bitmap[4];
    private Bitmap circleBitmap[]=new Bitmap[4];
//    private PositionSet positionSet=new PositionSet(0,0);
    private Mat mRgba;
    private Mat                    mGray;
    protected int color;//用来记录用户选择的是哪一个颜色
    private Point fingerTip;
    private PianoAcceleration Piano;
    private boolean play = false ;
    private static final Scalar FINGER_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    private int drumPos[]=new int[4];
    //因为鼓的位置有自定义，所以这里只是创建了变量
    private static int DRUMKEY[]={0,44,74,118,148,192};
    private DrumAcceleration Drum;

    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {//
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "opencv loaded successfully");
                    System.loadLibrary("ndk_jni_utils");
                    mOpenCvCameraView.setMaxFrameSize(200,200);
                    mOpenCvCameraView.enableView();
                    DetectionBasedTracker.ChangeColor(color);
                    break;
                default:
                    super.onManagerConnected(status);
                    break;

            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.drum_play_interface);
//        iv = (ImageView) findViewById(R.id.instrument);
        initPlay();

        mOpenCvCameraView=(CameraBridgeViewBase)findViewById(R.id.view2);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);

        //这里的接收应该要改,就先用固定的了
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        color=bundle.getInt("color",0);
        drumPos[0]=bundle.getInt("drum0");
        drumPos[1]=bundle.getInt("drum1");
        drumPos[2]=bundle.getInt("drum2");
        drumPos[3]=bundle.getInt("drum3");
        BeforePlay();//为了播放音乐之前的准备
        Drum=new DrumAcceleration(DRUMKEY,25,12,70);

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray=new Mat();
        mRgba=new Mat();
        initPlay();
    }
    public Bitmap readBitmap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
    protected void changeToBitmap(){
        drumBitmap[0]=readBitmap(this,R.drawable.bassr);
        drumBitmap[1]=readBitmap(this,R.drawable.ridey);
        drumBitmap[2]=readBitmap(this,R.drawable.snareb);
        drumBitmap[3]=readBitmap(this,R.drawable.tomp);
        circleBitmap[0]=readBitmap(this,R.drawable.bass);
        circleBitmap[1]=readBitmap(this,R.drawable.ride);
        circleBitmap[2]=readBitmap(this,R.drawable.snare);
        circleBitmap[3]=readBitmap(this,R.drawable.tom);
    }
    private void initPlay(){
//        Log.i(TAG, "playing drum");
        //iv.setImageResource(R.drawable.drum);
        changeToBitmap();
        drum[0]= (ImageView) findViewById(R.id.drum1);
        drum[1]= (ImageView) findViewById(R.id.drum2);
        drum[2]= (ImageView) findViewById(R.id.drum3);
        drum[3]= (ImageView) findViewById(R.id.drum4);
        circle[0]=(ImageView) findViewById(R.id.circle1);
        circle[1]=(ImageView) findViewById(R.id.circle2);
        circle[2]=(ImageView) findViewById(R.id.circle3);
        circle[3]=(ImageView) findViewById(R.id.circle4);
        circle[drumPos[0]].setImageBitmap(circleBitmap[0]);
        circle[drumPos[1]].setImageBitmap(circleBitmap[1]);
        circle[drumPos[2]].setImageBitmap(circleBitmap[2]);
        circle[drumPos[3]].setImageBitmap(circleBitmap[3]);
        drum[drumPos[0]].setImageBitmap(drumBitmap[0]);
        drum[drumPos[1]].setImageBitmap(drumBitmap[1]);
        drum[drumPos[2]].setImageBitmap(drumBitmap[2]);
        drum[drumPos[3]].setImageBitmap(drumBitmap[3]);
        drum[0].setVisibility(View.INVISIBLE);
        drum[1].setVisibility(View.INVISIBLE);
        drum[2].setVisibility(View.INVISIBLE);
        drum[3].setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mOpenCvCameraView!=null){
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG,"Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,this,mLoaderCallback);
        }else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba=inputFrame.rgba();
        Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGRA2RGB);//从RGBA转到RGB
        Imgproc.cvtColor(mRgba,mRgba,Imgproc.COLOR_RGB2HSV);//从RGB转到HSV
        Core.flip(mRgba,mRgba,1);//最后的1表示水平翻转，0表示垂直翻转
        //DetectionBasedTracker.ChangeColor(color);
        int pointXY[]=DetectionBasedTracker.returnXYCoordinate(mRgba);
        Imgproc.cvtColor(mRgba,mRgba,Imgproc.COLOR_HSV2BGR);
        for(int i=0;i<pointXY.length;i=i+2) {
            Point topLeft = new Point(pointXY[i], pointXY[i + 1]);
            fingerTip = new Point();
            ////////////////////////////////////////
            //特别注意这里直接变成了从手机倒放的左上角的坐标
            fingerTip.x = topLeft.x;
            fingerTip.y = topLeft.y;
            //////////////////////////////////////////
            Imgproc.circle(mRgba, topLeft, 10, FINGER_RECT_COLOR);
            Log.i(TAG, fingerTip.x + "," + (fingerTip.y));
            int temp[]=Drum.setXY((int)fingerTip.x,(int)fingerTip.y);
            if(temp[0] <0){
                play=false;
            }
            else{
                play=true;
            }
            if (play) playDrum(temp[0],temp[1],temp[0]);
        }
        return mRgba;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ////////////////////////////////////////
        //在退出时释放所有资源
        soundPool.release();
        ////////////////////////////////////////
        mOpenCvCameraView.disableView();
    }
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what<4)
                drum[msg.what].setVisibility(View.VISIBLE);
//                drum[msg.what].setImageBitmap(drumBitmap[msg.what]);
//            if(msg.what>0&&msg.what<9) {
//                switch (msg.what) {
//                    default:
////                        iva.setVisibility(View.VISIBLE);
//                        iv[0].setImageBitmap(bitmap);
//
//                        break;
//                }
//            }
        }
    };
    private void playDrum(int key,int tone,final int key0){
        final Message msg=new Message();
        msg.what=key;
        key=key+8;
        playMusic(key,tone);

        mHandler.sendMessageAtFrontOfQueue(msg);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((key0)<4) {
                    Log.i("drum",key0+"");
//                    drum[key0].setImageBitmap(drumBitmap[0]);
                    drum[key0].setVisibility(View.INVISIBLE);
                }

            }
        }, 2000);
    }
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent1=new Intent();
            intent1.setClass(DrumPlay.this, ChoiceInterface.class);
            DrumPlay.this.startActivity(intent1);
            DrumPlay.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //以下为混音器demo
    private SoundPool soundPool;
    private AudioAttributes attributes;
    private HashMap<Integer, Integer> soundPoolMap;
    private AudioManager mgr;

    private float streamVolumeMax, volume;

    //SuppressWarnings为了在编译API<=21的时候直接创建SoundPool时使用
    //这个函数要在播放音乐之前调用
    //所以选择在oncreate中调用一次，现在已经加进去了
    @SuppressWarnings("deprecation")
    public void BeforePlay(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //API>=21的时候用
            attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .setMaxStreams(5)
                    .build();
        }else{
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,50);
        }
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(8, soundPool.load(this, R.raw.cymbal, 1));
        soundPoolMap.put(9, soundPool.load(this, R.raw.drumside, 1));
        soundPoolMap.put(10, soundPool.load(this, R.raw.drum, 1));
        soundPoolMap.put(11, soundPool.load(this, R.raw.cymbal, 1));
        soundPoolMap.put(12, soundPool.load(this, R.raw.drum2, 1));
        mgr = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//15
    }

    //volumnToPlay分7档音量
    public void playMusic(int tone,int volumnToPlay){
        if(volumnToPlay==15){
            volume=1.0f;
        }if(volumnToPlay == 8){
            volume=0.5f;
        }
        else{
            volume=0.1f;
        }
        soundPool.play(soundPoolMap.get(tone), (float)volume, (float)volume, 2, 0, 1f);
    }
    //混音器Demo结束
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
}
