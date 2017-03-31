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

        File dir = createDirectory();

        File outputFile = createFileInDirectory(dir);

        if (null != outputFile) {
            startCameraEvent(outputFile);
        }
    }

    private File createDirectory() {
        Timber.d("Creating image directory");
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/myRecipePictures/");
        if (directory.mkdirs()) {
            return directory;
        } else {
            Timber.e("Cannot create image directory, returns null");
            return null;
        }
    }

    private File createFileInDirectory(File directory) {
        if (null == directory) {
            Timber.e("Cannot image file, directory was null, returns null");
            return null;
        }
        count++;
        String path = directory + "recipeImage" + count + ".jpg";
        Timber.d("Trying create file " + path);
        File file = new File(path);
        try {
            if (file.createNewFile()) {
                return file;
            } else {
                Timber.d("Cannot create file " + path + "returns null");
                return null;
            }
        } catch (IOException e) {
            Timber.e(e, "Cannot create file " + path + ", returns null");
            return null;
        }
    }

    private void startCameraEvent(File outputFile) {
        Timber.d("Starting Camera for result");
        outputFileUri = Uri.fromFile(outputFile);

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

        Bitmap bitmap = getPictureTaken(result);

        if (null != bitmap) {
            ImageView view = (ImageView) findViewById(R.id.imageView);
            view.setImageBitmap(bitmap);
            Timber.d("Displaying picture " + result + " as " + bitmap);
        }
    }

    private Bitmap getPictureTaken(Uri result) {
        Timber.d("Trying to load picture from " + result);
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), result);
        } catch (IOException e) {
            Timber.e(e, "Cannot load picture " + result + ", returns null");
            return null;
        }
    }
}
