package com.notesinshort.notesinshort;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.frosquivel.magicalcamera.MagicalCamera;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created by mayank on 9/7/16.
 */
public class MainActivity extends AppCompatActivity {

    String TAG = MainActivity.class.getSimpleName();
    FirebaseUser user;
    FloatingActionButton camera, choose_document;
    FloatingActionMenu menu;
    MagicalCamera magicalCamera;
    ImageView imageView;
    long unixTime;

    //a regular quality, if you declare with 50 is a worst quality and if you declare with 4000 is the better quality
    //only need to play with this variable (0 to 4000 ... or in other words, worst to better :D)

    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = (FloatingActionMenu) findViewById(R.id.menu);
        camera = (FloatingActionButton) findViewById(R.id.camera);
        choose_document = (FloatingActionButton) findViewById(R.id.choose_document);
        imageView = (ImageView) findViewById(R.id.imageView);

        magicalCamera = new MagicalCamera(this, RESIZE_PHOTO_PIXELS_PERCENTAGE);


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //call this method ever
        magicalCamera.resultPhoto(requestCode, resultCode, data);

        //with this form you obtain the bitmap
        imageView.setImageBitmap(magicalCamera.getMyPhoto());

        //if you need save your bitmap in device use this method
        unixTime = System.currentTimeMillis() / 1000L;

        if (magicalCamera.savePhotoInMemoryDevice(magicalCamera.getMyPhoto(), String.valueOf(unixTime), "NotesInShort", MagicalCamera.JPEG, true)) {
            Toast.makeText(MainActivity.this, "The photo is save in device, please check this", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Sorry your photo dont write in devide, please contact with fabian7593@gmail and say this error", Toast.LENGTH_SHORT).show();
        }
    }

    public void open_document() {

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