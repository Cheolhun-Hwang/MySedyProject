package hch.hooney.mysedyproject;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingActivity extends AppCompatActivity {
    private final String TAG = SettingActivity.class.getSimpleName();

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishForResultAddEvent(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setUpActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //do whatever
                finishForResultAddEvent(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpActionBar(){
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Setting");
    }

    private void finishForResultAddEvent(int sig){
        setResult(sig);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }


}
