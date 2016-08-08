package com.example.administrator.virtualinstrument;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Chengzhi_Huang on 2016/7/2.
 */
public class PianoPlay extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;
    protected static final String TAG="PianoPlay";
    private ImageView iv[]=new ImageView[9];
    private Mat                    mRgba;

    private Bitmap bitmap,bitmap1,bitmap2,bitmap3,bitmap4,bitmap5,bitmap6,bitmap7,bitmap8;
    protected int color;//用来记录用户选择的是哪一个颜色
    private Point fingerTip;
    private PianoAcceleration Piano;
    protected static final int KEY[]={0,28,46,63,85,103,120,141,175};
    private boolean play = false ;


    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.play_interface);

        mOpenCvCameraView=(CameraBridgeViewBase)findViewById(R.id.view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        color=bundle.getInt("color");
        BeforePlay();//为了播放音乐之前的准备
        Piano=new PianoAcceleration(KEY,25,12,70);
    }
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba=new Mat();
        initPlay();
    }

    @Override
    public void onCameraViewStopped() {
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
    protected void onDestroy() {
        super.onDestroy();
        ////////////////////////////////////////
        //在退出时释放所有资源
        soundPool.release();
        ////////////////////////////////////////
        mOpenCvCameraView.disableView();
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
            Log.i(TAG, pointXY[i] + "," + pointXY[i + 1]);
            int temp[]=Piano.setXY((int)pointXY[i],(int)pointXY[i + 1]);
            if(temp[0] <0){
                play=false;
            }
            else{
                play=true;
            }
            if (play) playPiano(temp[0],temp[1]);
        }
        return mRgba;
    }

    private void initPlay(){
        Log.i(TAG, "playing piano");

        iv[0] = (ImageView) findViewById(R.id.instrument);
        iv[3]=(ImageView) findViewById(R.id.a);
        iv[2]=(ImageView) findViewById(R.id.b);
        iv[1]=(ImageView) findViewById(R.id.c);
        iv[7]=(ImageView) findViewById(R.id.d);
        iv[6]=(ImageView) findViewById(R.id.e);
        iv[5]=(ImageView) findViewById(R.id.f);
        iv[4]=(ImageView) findViewById(R.id.g);
        iv[8]=(ImageView) findViewById(R.id.doo);
        changeToBitmap();
        iv[0].setImageResource(R.drawable.xylplay);
        iv[1].setVisibility(View.INVISIBLE);
        iv[2].setVisibility(View.INVISIBLE);
        iv[3].setVisibility(View.INVISIBLE);
        iv[4].setVisibility(View.INVISIBLE);
        iv[5].setVisibility(View.INVISIBLE);
        iv[6].setVisibility(View.INVISIBLE);
        iv[7].setVisibility(View.INVISIBLE);
        iv[8].setVisibility(View.INVISIBLE);
    }
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent1=new Intent();
            intent1.setClass(PianoPlay.this, ChoiceInterface.class);
            PianoPlay.this.startActivity(intent1);
            PianoPlay.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
    public Bitmap readBitmap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
    protected void changeToBitmap(){
        bitmap=readBitmap(this,R.drawable.xylplay);
        bitmap1=readBitmap(this,R.drawable.c);
        bitmap2=readBitmap(this,R.drawable.b);
        bitmap3=readBitmap(this,R.drawable.a);
        bitmap4=readBitmap(this,R.drawable.g);
        bitmap5=readBitmap(this,R.drawable.f);
        bitmap6=readBitmap(this,R.drawable.e);
        bitmap7=readBitmap(this,R.drawable.d);
        bitmap8=readBitmap(this, R.drawable.doo);
        iv[1].setImageBitmap(bitmap1);
        iv[2].setImageBitmap(bitmap2);
        iv[3].setImageBitmap(bitmap3);
        iv[4].setImageBitmap(bitmap4);
        iv[5].setImageBitmap(bitmap5);
        iv[6].setImageBitmap(bitmap6);
        iv[7].setImageBitmap(bitmap7);
        iv[8].setImageBitmap(bitmap8);
    }
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            iv[msg.what].setVisibility(View.VISIBLE);
            if(msg.what>0&&msg.what<9) {
                switch (msg.what) {
                    default:
//                        iva.setVisibility(View.VISIBLE);
                        iv[0].setImageBitmap(bitmap);
                        break;
                }
            }
        }
    };

    private void playPiano(final int key,int tone){
        Message msg=new Message();
        msg.what=8-key;
        playMusic(key, tone);
        mHandler.sendMessageAtFrontOfQueue(msg);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if((8-key)!=0) {
                    iv[8-key].setVisibility(View.INVISIBLE);
                }

            }
        },1500);
    }

    //升八度或者降八度
    private void chooseOct(float x,float y){
        //升八度
        if(y+x>1629&&y+x<1806&&y-x>-1411&&y-x<-1234){
            changePitches(true);
            Log.i("badu","+");
        }
        //降八度
        else if(y+x>1833&&y+x<2010&&y-x>-1615&&y-x<-1438){
            changePitches(false);
            Log.i("badu","-");
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            float x=event.getX();
            float y=event.getY();
            chooseOct(x,y);
        }

        else if(event.getAction()==MotionEvent.ACTION_UP) {

        }
        else if(event.getAction()== MotionEvent.ACTION_MOVE){

        }
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //以下为混音器demo
    private SoundPool soundPool;
    private AudioAttributes attributes;
    private HashMap<Integer, Integer> soundPoolMap;
    private AudioManager mgr;

    private int pitches=-1;//用来表示八度，-1，0，1，三个调;
    //这里之所以变成-1是为了初始化BeforePlay()函数方便调用

    private float volume;

    boolean changePitches(boolean high){
        //high=true 就说明升一个八度；high=false就说明降一个八度
        //如果升、降八度成功，就返回true；pitches已经是1的时候再升八度就会
        Toast toast;
        if(pitches == 1 && high == true){
            toast=Toast.makeText(getApplicationContext(),"已到最高音，无法继续升调",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return false;
        }else if(pitches == -1 && high == false){
            toast=Toast.makeText(getApplicationContext(),"已到最低音，无法继续降调",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return false;
        }
        if(high){
            pitches++;
        }else{
            pitches--;
        }
        toast=Toast.makeText(getApplicationContext(),"声音调节完成，请等待5s缓冲",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
        //先清空原本的声音文件
        // 不知道为什么，一旦调用就再也无法发出声音
        //soundPool.release();
        if(soundPoolMap == null){
            soundPoolMap = new HashMap<Integer, Integer>();
        }
        soundPoolMap.clear();
        if(pitches == 1){
            soundPoolMap.put(0, soundPool.load(this, R.raw.la_2, 1));
            soundPoolMap.put(1, soundPool.load(this, R.raw.si_2, 1));
            soundPoolMap.put(2, soundPool.load(this, R.raw.dou_3, 1));
            soundPoolMap.put(3,soundPool.load(this,R.raw.re_3,1));
            soundPoolMap.put(4,soundPool.load(this,R.raw.mi_3,1));
            soundPoolMap.put(5,soundPool.load(this,R.raw.fa_3,1));
            soundPoolMap.put(6,soundPool.load(this,R.raw.sol_3,1));
            soundPoolMap.put(7,soundPool.load(this,R.raw.la_3,1));
        }else if(pitches == 0){
            soundPoolMap.put(0, soundPool.load(this, R.raw.sol_1, 1));
            soundPoolMap.put(1, soundPool.load(this, R.raw.la_1, 1));
            soundPoolMap.put(2, soundPool.load(this, R.raw.si_1, 1));
            soundPoolMap.put(3,soundPool.load(this,R.raw.dou_2,1));
            soundPoolMap.put(4,soundPool.load(this,R.raw.re_2,1));
            soundPoolMap.put(5,soundPool.load(this,R.raw.mi_2,1));
            soundPoolMap.put(6,soundPool.load(this,R.raw.fa_2,1));
            soundPoolMap.put(7,soundPool.load(this,R.raw.sol_2,1));
        }else if(pitches == -1){
            soundPoolMap.put(0, soundPool.load(this, R.raw.fa_0, 1));
            soundPoolMap.put(1, soundPool.load(this, R.raw.sol_0, 1));
            soundPoolMap.put(2, soundPool.load(this, R.raw.la_0, 1));
            soundPoolMap.put(3,soundPool.load(this,R.raw.si_1,1));
            soundPoolMap.put(4,soundPool.load(this,R.raw.dou_1,1));
            soundPoolMap.put(5,soundPool.load(this,R.raw.re_1,1));
            soundPoolMap.put(6,soundPool.load(this,R.raw.mi_1,1));
            soundPoolMap.put(7,soundPool.load(this,R.raw.fa_1,1));
        }
        return true;
    }
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
        //pitches初始化为-1
        changePitches(true);//初始化音乐为中音
        mgr = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
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
        //play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) ,
        //参数：soundID：资源ID;leftVolume：左频道声音;rightVolume：右频道声音--都是0.0-1.0
        //loop：-1代表循环，0代表不循环;rate：值0.5-2.0设置1为正常,表示播放快慢
    }
    //混音器Demo结束
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
}
