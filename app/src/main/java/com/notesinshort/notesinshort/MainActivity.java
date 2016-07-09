package com.notesinshort.notesinshort;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.frosquivel.magicalcamera.MagicalCamera;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mayank on 9/7/16.
 */
public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    NoteAdapter adapter;
    ArrayList<Note> list = new ArrayList<>();

    String TAG = MainActivity.class.getSimpleName();
    FirebaseUser user;
    FloatingActionButton camera, choose_document;
    FloatingActionMenu menu;
    MagicalCamera magicalCamera;
    ImageView imageView;
    long unixTime;
    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 1000;
    final private int CAMERA_PERMISSIONS_REQUEST = 123;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new NoteAdapter(list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(manager);
        //rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rv.setAdapter(adapter);

        rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), rv, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Note movie = list.get(position);
                Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareMovieData();

        menu = (FloatingActionMenu) findViewById(R.id.menu);
        camera = (FloatingActionButton) findViewById(R.id.camera);
        choose_document = (FloatingActionButton) findViewById(R.id.choose_document);
        imageView = (ImageView) findViewById(R.id.imageView);

        magicalCamera = new MagicalCamera(this, RESIZE_PHOTO_PIXELS_PERCENTAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToOpenCamera();
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
                open_document();
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
        magicalCamera.selectedPicture("my_header_name");

    }


    public void prepareMovieData() {

        Note movie = new Note("Mad Max: Fury Road", "Action & Adventure", "2015");
        list.add(movie);
        movie = new Note("Inside Out", "Animation, Kids & Family", "2015");
        list.add(movie);
        movie = new Note("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        list.add(movie);
        movie = new Note("Shaun the Sheep", "Animation", "2015");
        list.add(movie);
        movie = new Note("The Martian", "Science Fiction & Fantasy", "2015");
        list.add(movie);
        movie = new Note("Mission: Impossible Rogue Nation", "Action", "2015");
        list.add(movie);
        movie = new Note("Up", "Animation", "2009");
        list.add(movie);
        movie = new Note("Star Trek", "Science Fiction", "2009");
        list.add(movie);
        movie = new Note("The LEGO Note", "Animation", "2014");
        list.add(movie);
        movie = new Note("Iron Man", "Action & Adventure", "2008");
        list.add(movie);
        movie = new Note("Aliens", "Science Fiction", "1986");
        list.add(movie);
        movie = new Note("Chicken Run", "Animation", "2000");
        list.add(movie);
        movie = new Note("Back to the Future", "Science Fiction", "1985");
        list.add(movie);
        movie = new Note("Raiders of the Lost Ark", "Action & Adventure", "1981");
        list.add(movie);
        movie = new Note("Goldfinger", "Action & Adventure", "1965");
        list.add(movie);
        movie = new Note("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014");
        list.add(movie);

        adapter.notifyDataSetChanged();

    }

    interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }

    public void scanImage(View v) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        walkdir(file);
        Toast.makeText(this, "FAB", Toast.LENGTH_SHORT).show();
    }

    public void walkdir(File dir) {
        String pdfPattern = ".pdf";

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (File aListFile : listFile) {
                if (aListFile.isDirectory()) {
                    walkdir(aListFile);
                } else {
                    if (aListFile.getName().endsWith(pdfPattern)) {
                        //Do what ever u want
                        Intent intent = new Intent(this, PDFviewerActivity.class);
                        intent.putExtra("File", aListFile);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    public void getPermissionToOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //call this method ever
        magicalCamera.resultPhoto(requestCode, resultCode, data);

        //with this form you obtain the bitmap
        imageView.setImageBitmap(magicalCamera.getMyPhoto());

        //if you need save your bitmap in device use this method
        unixTime = System.currentTimeMillis() / 1000L;

        try {
            if (magicalCamera.savePhotoInMemoryDevice(magicalCamera.getMyPhoto(), String.valueOf(unixTime), "NotesInShort", MagicalCamera.JPEG, true)) {
                Toast.makeText(MainActivity.this, "The photo is save in device, please check this", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Sorry your photo dont write in devide, please contact with fabian7593@gmail and say this error", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Didn\'t take image", Toast.LENGTH_SHORT).show();
        }
    }

    public void open_document() {

    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}