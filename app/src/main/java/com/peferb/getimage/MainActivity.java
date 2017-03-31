package com.peferb.getimage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 64564161;
    private final int TAKE_PHOTO_OLD_STYLE_CODE = 0;
    private static final int IMAGE_REQUEST_NEW_SCHOOL_CODE = 1;
    private static int count = 0;
    private Uri outputFileUri;
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.peferb.getimage.provider";


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
//        takePictureOldSchool();
        takePictureNewSchool();
    }

    private void takePictureNewSchool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Timber.d("Asking for camera permission");
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_REQUEST_CODE);
            } else {
                pictureTakingGrantedGoGoGo();
            }
        } else {
            takePictureOldSchool();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pictureTakingGrantedGoGoGo();
                }
            }
        }
    }

    private void pictureTakingGrantedGoGoGo() {
        File path = new File(this.getFilesDir(), "images/recipe/");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, "image.jpg");
        Uri imageUri = FileProvider.getUriForFile(this, CAPTURE_IMAGE_FILE_PROVIDER, image);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_REQUEST_NEW_SCHOOL_CODE);
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
//        if (directory.mkdirs()) {
//            return directory;
//        } else {
//            Timber.e("Cannot create image directory, returns null");
//            return null;
//        }
        directory.mkdirs();
        return directory;
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
        Timber.d("onActivityResult requestCode " + requestCode + ", resultCode " + resultCode);

        if (requestCode == TAKE_PHOTO_OLD_STYLE_CODE && resultCode == RESULT_OK) {
            displayImageOldSchoolStyle(outputFileUri);
        }

        if (requestCode == IMAGE_REQUEST_NEW_SCHOOL_CODE) {
            Timber.d((null != data) ? data.toString() : "null");
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
