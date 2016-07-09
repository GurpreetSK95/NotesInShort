package com.notesinshort.notesinshort;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frosquivel.magicalcamera.MagicalCamera;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by mayank on 9/7/16.
 */
public class MainActivity extends AppCompatActivity {

    final private int CAMERA_PERMISSIONS_REQUEST = 123;
    final private int SAVE_IMAGE_PERMISSIONS_REQUEST = 456;
    Bitmap.CompressFormat jpeg = MagicalCamera.JPEG;
    Bitmap.CompressFormat png = MagicalCamera.PNG;
    Bitmap.CompressFormat webp = MagicalCamera.WEBP;
    String TAG = MainActivity.class.getSimpleName();
    FirebaseUser user;
    FloatingActionButton camera, choose_document;
    FloatingActionMenu menu;
    MagicalCamera magicalCamera;
    ImageView imageView;
    ProgressDialog progress;


    //a regular quality, if you declare with 50 is a worst quality and if you declare with 4000 is the better quality
    //only need to play with this variable (0 to 4000 ... or in other words, worst to better :D)
    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 1000;


    private File root;
    private ArrayList<File> fileList = new ArrayList<File>();
    private LinearLayout view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = (FloatingActionMenu) findViewById(R.id.menu);
        camera = (FloatingActionButton) findViewById(R.id.camera);
        choose_document = (FloatingActionButton) findViewById(R.id.choose_document);
        imageView = (ImageView) findViewById(R.id.imageView);
        view = (LinearLayout) findViewById(R.id.view);

        //getting SDcard root path
        root = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath());

        magicalCamera = new MagicalCamera(this, RESIZE_PHOTO_PIXELS_PERCENTAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToOpenCamera();
            getPermissionToSaveImage();
        }

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openCamera();
            }
        });

        choose_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close(true);
                progress = new ProgressDialog(getApplicationContext());
                progress.setMessage("Downloading Music");
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setIndeterminate(true);
                show_documents();
            }
        });

        //Removed mAuth get instance code

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            navigateToLogin();
            Log.d(TAG, "User is null.");
        } else {
            Log.d(TAG, "User exists and is signed in.");
        }

    }

    public void openCamera() {

        magicalCamera.takePhoto();

    }

    public void getPermissionToOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.CAMERA)) {
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSIONS_REQUEST);
            }
        }
    }

    public void getPermissionToSaveImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        SAVE_IMAGE_PERMISSIONS_REQUEST);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //call this method ever
        magicalCamera.resultPhoto(requestCode, resultCode, data);

        if (writePhotoFile(magicalCamera.getMyPhoto(), "Test", "NotesInShort", MagicalCamera.JPEG, true)) {
            Log.d(TAG, "File has been saved.");
            imageView.setImageBitmap(magicalCamera.getMyPhoto());
        } else {
            Log.d(TAG, "Error in saving file.");
        }
    }

    public void show_documents() {
        getfile(root);

        for (int i = 0; i < fileList.size(); i++) {
            TextView textView = new TextView(this);
            textView.setText(fileList.get(i).getName());
            textView.setPadding(5, 5, 5, 5);

            //System.out.println(fileList.get(i).getName());

            if (fileList.get(i).isDirectory()) {
                textView.setTextColor(Color.parseColor("#FF0000"));
            } else {
                view.addView(textView);
            }
        }
        progress.dismiss();
    }

    public ArrayList<File> getfile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    fileList.add(listFile[i]);
                    getfile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".pdf"))

                    {
                        fileList.add(listFile[i]);
                    }
                }

            }
        }
        return fileList;
    }

    public void open_document() {

        Log.d(TAG, "Inside open document function.");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("application/pdf");
        startActivity(intent);

    }

    private boolean writePhotoFile(Bitmap bitmap, String photoName, String directoryName,
                                   Bitmap.CompressFormat format, boolean autoIncrementNameByDate) {
        if (bitmap == null) {
            return false;
        } else {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(format, 100, bytes);

            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String date = df.format(Calendar.getInstance().getTime());

            String newPhotoName;
            if (format == png) {
                newPhotoName = autoIncrementNameByDate ? photoName + "_" + date + ".png" : photoName + ".png";
            } else if (format == jpeg) {
                newPhotoName = autoIncrementNameByDate ? photoName + "_" + date + ".jpeg" : photoName + ".jpeg";
            } else if (format == webp) {
                newPhotoName = autoIncrementNameByDate ? photoName + "_" + date + ".webp" : photoName + ".webp";
            } else {
                newPhotoName = photoName;
            }

            File wallpaperDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + directoryName + "/");
            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }
            File f = new File(wallpaperDirectory, newPhotoName);

            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();
                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + f.getAbsolutePath())));

                return true;
            } catch (Exception ev) {
                return false;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}