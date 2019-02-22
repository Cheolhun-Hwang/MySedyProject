package hch.hooney.mysedyproject;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import hch.hooney.mysedyproject.Application.MySedyApplication;

public class LoadingActivity extends AppCompatActivity {
    private final String TAG = LoadingActivity.class.getSimpleName();
    private Thread loading;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 101:
                    intentPermission();
                    break;
                case 102:
                    errorExit();
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loading.start();
    }

    @Override
    protected void onStop() {
        stopLoading();
        super.onStop();
    }

    private void init(){
        loading = initLoading();
    }

    private boolean onSettingsEnv(){
        try{
            //Env

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private Thread initLoading(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                try {
                    if(onSettingsEnv()){
                        Thread.sleep(3500);
                        msg.what = 101;
                    }else{
                        msg.what = 102;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    msg.what = 102;
                }
                mHandler.sendMessage(msg);
            }
        });
    }

    private void stopLoading(){
        if(loading != null){
            if(loading.isAlive()){
                loading.interrupt();
            }
            loading = null;
        }
    }

    private void intentPermission(){
        startActivity(new Intent(getApplicationContext(), PermissionActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void errorExit(){
        Toast.makeText(getApplicationContext(), "에러로 인한 실행 불가.",
                Toast.LENGTH_SHORT).show();
        finish();
    }

}
