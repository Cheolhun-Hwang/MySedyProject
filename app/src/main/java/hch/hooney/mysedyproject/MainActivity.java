package hch.hooney.mysedyproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import hch.hooney.mysedyproject.Application.MySedyApplication;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    private final int SIG_SETTING = 103;
    private final int SIG_CAMERA = 202;

    private LinearLayout tab_two, tab_three, tab_four, tab_five;
    private TextView word_count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpActionBar();

        init();
    }

    private void init(){
        word_count = (TextView) findViewById(R.id.main_word_count);
        tab_two = (LinearLayout) findViewById(R.id.tab_two_linear);
        tab_three = (LinearLayout) findViewById(R.id.tab_three_linear);
        tab_four = (LinearLayout) findViewById(R.id.tab_four_linear);
        tab_five = (LinearLayout) findViewById(R.id.tab_five_linear);

        setEvent();
    }

    private void setEvent(){
        tab_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Tab2", Toast.LENGTH_SHORT).show();
            }
        });
        tab_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchWordActivity.class));
            }
        });
        tab_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Tab4", Toast.LENGTH_SHORT).show();
            }
        });
        tab_five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Tab5", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(getApplicationContext(), CameraActivity.class), SIG_CAMERA);
            }
        });
    }

    private void setUpActionBar(){
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(getResources().getDrawable(R.drawable.dictionary_64x64));
        bar.setTitle(getString(R.string.main_title));
        bar.setSubtitle(stringFromJNI());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_setting:
                startActivityForResult(new Intent(getApplicationContext(), SettingActivity.class), SIG_SETTING);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SIG_SETTING:
                if(resultCode == 1){

                }
                break;
            case SIG_CAMERA :
                if(resultCode == 1){
                    //word
                    Intent intent = new Intent(getApplicationContext(), SearchWordActivity.class);
                    intent.putExtra("word", data.getStringExtra("translate"));
                    startActivity(intent);
                }if(resultCode == 2){
                    //sentence

                }else{

                }
                break;
        }
    }

    public native String stringFromJNI();
}
