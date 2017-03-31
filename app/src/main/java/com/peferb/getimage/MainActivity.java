package com.peferb.getimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private final int TAKE_PHOTO_OLD_STYLE_CODE = 0;
    private static int count = 0;
    private Uri outputFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());
    }

    @OnClick(R.id.button_take_picture)
    public void takePicture(View view) {
        Timber.d("button_take_picture clicked");
        takePictureOldSchool();
    }

    private void takePictureOldSchool() {
        Timber.d("Taking picture old school style");
        // Here, we are making a folder named picFolder to store
        // pics taken by the camera using this application.
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File pictureDirectory = new File(dir);
        pictureDirectory.mkdirs();

        // Here, the counter will be incremented each time, and the
        // picture taken by camera will be stored as 1.jpg,2.jpg
        // and likewise.
        count++;
        String file = dir + count + ".jpg";
        File newFile = new File(file);
        try {
            newFile.createNewFile();
        }
        catch (IOException e) {
            Timber.e(e, "Cannot create file for picture");
        }

        outputFileUri = Uri.fromFile(newFile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(cameraIntent, TAKE_PHOTO_OLD_STYLE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onActivityResult picture taken");

        if (requestCode == TAKE_PHOTO_OLD_STYLE_CODE && resultCode == RESULT_OK) {
            displayImageOldSchoolStyle(outputFileUri);
        }
    }

    private void displayImageOldSchoolStyle(Uri result) {
        Timber.d("Fetch and display image old school style");

        Bitmap mBitmap = null;
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageView view = (ImageView) findViewById(R.id.imageView);
        view.setImageBitmap(mBitmap);
    }
}
