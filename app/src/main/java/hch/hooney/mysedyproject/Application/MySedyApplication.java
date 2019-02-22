package hch.hooney.mysedyproject.Application;

import android.app.Application;
import android.graphics.Bitmap;

import org.opencv.core.Mat;

public class MySedyApplication extends Application {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    public MySedyApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
