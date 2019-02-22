package hch.hooney.mysedyproject;

import android.content.ContentUris;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import hch.hooney.mysedyproject.MyBitmapPack.MyBitMapPack;

public class ImageCropActivity extends AppCompatActivity {
    private final String TAG = ImageCropActivity.class.getSimpleName();

    private Button cancel_btn, word_btn, sentence_btn;
    private CropImageView cropImageView;
    private String data_path ;
    private TessBaseAPI mTess;
    private Bitmap cropImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        cropImageView.setImageUriAsync(getUriFromPath());
    }

    private void init(){
        cropImageView = (CropImageView) findViewById(R.id.crop_image_view);
        cancel_btn = (Button) findViewById(R.id.crop_cancel_btn);
        word_btn = (Button) findViewById(R.id.crop_translate_word_btn);
        sentence_btn = (Button) findViewById(R.id.crop_translate_sentence_btn);

        //traindata
        data_path =  getFilesDir() + "/tesseract/";
        //트레이닝데이터가 복사되어 있는지 확인
        checkFile(new File(data_path+"tessdata/"));
        //TessBassApi init()
        mTess = new TessBaseAPI();
        mTess.init(data_path, "eng");


        setEvent();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        cancelCrop();
    }

    private void setEvent(){
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCrop();
            }
        });
        word_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crop(1);
            }
        });
        sentence_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crop(2);
            }
        });
    }

    private void cancelCrop(){
        setResult(3);
        finish();
    }

    private void crop(int sig){
        cropImage = cropImageView.getCroppedImage();

        mTess.setImage(cropImage);
        String result = mTess.getUTF8Text();

        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        intent.putExtra("translate", result);

        //Save
        new MyBitMapPack().saveTempBitmap(cropImage, "crop");
        setResult(sig, intent);
        finish();
    }

    //check file on the device
    private void checkFile(File dir) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if(!dir.exists()&& dir.mkdirs()) {
            copyFiles();
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            String datafilepath = data_path+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if(!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try{
            String filepath = data_path + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open("tessdata/eng.traineddata");
            OutputStream os = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            os.flush();
            os.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Uri getUriFromPath(){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/MySedy/origin.jpg");
        Uri fileUri = Uri.parse( myDir.getPath() );
        String filePath = fileUri.getPath();
        Cursor cursor = getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, "_data = '" + filePath + "'", null, null );
        cursor.moveToNext();
        int id = cursor.getInt( cursor.getColumnIndex( "_id" ) );
        Uri uri = ContentUris.withAppendedId( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id );
        return uri;
    }

}
