package com.example.administrator.virtualinstrument;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/3/28.
 */
public class ChoiceInterface extends AppCompatActivity implements View.OnClickListener {
    private ImageView pbtn,bbtn;
    int color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_interface);
        initChoiceInterface();
        Intent intent=getIntent();
        color=intent.getIntExtra("color",0);
    }
    private void initChoiceInterface(){
        pbtn= (ImageView) findViewById(R.id.pianobtn);
        bbtn= (ImageView) findViewById(R.id.drumbtn);
        pbtn.setOnClickListener(this);
        bbtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent1=new Intent();
        intent1.setClass(ChoiceInterface.this, PianoPlay.class);
        Intent intent2=new Intent();
        intent2.setClass(ChoiceInterface.this, DrumPlay.class);
        Bundle bundle=new Bundle();
        //piano kind=0,drum=1
        switch (view.getId()){
            case R.id.pianobtn:
                bundle.putInt("kind",0);
                bundle.putInt("color",color);
                intent1.putExtras(bundle);
                ChoiceInterface.this.startActivity(intent1);
                ChoiceInterface.this.finish();
                break;
            case R.id.drumbtn:
                bundle.putInt("kind",1 );
                bundle.putInt("color",color);
                intent2.putExtras(bundle);
                ChoiceInterface.this.startActivity(intent2);
                ChoiceInterface.this.finish();
                break;
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
}
