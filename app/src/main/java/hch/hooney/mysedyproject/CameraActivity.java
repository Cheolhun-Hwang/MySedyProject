package hch.hooney.mysedyproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.theartofdev.edmodo.cropper.CropImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import hch.hooney.mysedyproject.Application.MySedyApplication;
import hch.hooney.mysedyproject.MyBitmapPack.MyBitMapPack;

public class CameraActivity extends AppCompatActivity
    implements CameraBridgeViewBase.CvCameraViewListener2 {
    private final String TAG = CameraActivity.class.getSimpleName();
    public final static int SIGNAL_PERMISSION = 102;
    public final static int SIGNAL_IMAGE_CROP = 201;
    public final static String[] REQUIRE_PERMISSIONS  = {"android.permission.CAMERA"};

    private FloatingActionButton shootBtn;
    private boolean isShoot;
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput, matResult;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(CameraActivity.this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    mOpenCvCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);

        cameraInit();
    }

    private void cameraInit(){
        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        shootBtn = (FloatingActionButton) findViewById(R.id.Camera_Shoot_BTN);
        isShoot = false;
        setEvent();
    }

    private void setEvent(){
        shootBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isShoot){
                    isShoot = true;
                    new MyBitMapPack().saveTempBitmap(convertMatToBitmap(), "origin");
                    Intent intent = new Intent(getApplicationContext(), ImageCropActivity.class);
                    startActivityForResult(intent, SIGNAL_IMAGE_CROP);
                }
            }
        });
    }

    private Bitmap convertMatToBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(matResult.cols(), matResult.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matResult, bitmap);
        return bitmap;
    }

    private void cameraViewSetUp(){
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkNowPermission();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    /*
    * Permission Check
    * */
    private void checkNowPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(REQUIRE_PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(REQUIRE_PERMISSIONS, SIGNAL_PERMISSION);
            }else{
                cameraViewSetUp();
            }
        }
    }

    private boolean hasPermissions(String[] permissions){
        for (String perms : permissions){
            int result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED){
                //허가 안된 퍼미션 발견
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                          int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case SIGNAL_PERMISSION:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SIGNAL_IMAGE_CROP:
                String translate = data.getStringExtra("translate");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("translate", translate);
                if(resultCode == 1){
                    setResult(1, intent);
                    finish();
                }else if(resultCode == 2){
                    setResult(2, intent);
                    finish();
                }else{
                    isShoot = false;
                }
                break;
            default:
                break;
        }
    }

    private void showDialogForPermission(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder( CameraActivity.this);
        builder.setTitle("Notice");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                checkNowPermission();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }


    /*
    * Camera Implement
    * */
    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.d(TAG, "width : " + width + " / height : " + height);
        matResult = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matInput = inputFrame.rgba();
        ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
//        return matResult;

        return inputFrame.rgba();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
}
