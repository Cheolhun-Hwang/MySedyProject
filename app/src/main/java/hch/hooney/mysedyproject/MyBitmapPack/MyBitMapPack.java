package hch.hooney.mysedyproject.MyBitmapPack;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class MyBitMapPack {
    public void saveTempBitmap(Bitmap bitmap, String filename) {
        if (isExternalStorageWritable()) {
            saveImage(bitmap, filename);
        }else{
            //prompt the user or do something
        }
    }

    private void saveImage(Bitmap finalBitmap, String filename) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/MySedy");
        myDir.mkdirs();

        String fname = filename+".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
