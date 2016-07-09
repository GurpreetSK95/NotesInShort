package com.notesinshort.notesinshort;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


/**
 * Created by mayank on 9/7/16.
 */
public class MainActivity extends AppCompatActivity {

    final private int CAMERA_PERMISSIONS_REQUEST = 123;
    final private int SAVE_IMAGE_PERMISSIONS_REQUEST = 456;
    RecyclerView rv;
    NoteAdapter adapter;
    ArrayList<Note> list = new ArrayList<>();
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
    File f;
    //a regular quality, if you declare with 50 is a worst quality and if you declare with 4000 is the better quality
    //only need to play with this variable (0 to 4000 ... or in other words, worst to better :D)
    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 1000;


    private File root;
    private ArrayList<File> fileList = new ArrayList<File>();
    private ArrayList<String> files = new ArrayList<String>();

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
        //view = (LinearLayout) findViewById(R.id.view);

        getPermissionToSaveImage();
        getPermissionToOpenCamera();

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

                SpotsDialog dialog = new SpotsDialog(MainActivity.this);
                dialog.show();
                getfile(root);
                choose_document.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show_documents();
                        new MaterialDialog.Builder(MainActivity.this)
                                .title("Choose file to share")
                                .items(files)
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        //Toast.makeText(getApplicationContext(), which + " is selected!", Toast.LENGTH_SHORT).show();

                                        //CHANGE API ENDPOINT HERE

                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                        shareIntent.setType("text/*");
                                        startActivity(shareIntent);
                                    }
                                })
                                .show();
                    }
                });
                dialog.dismiss();

                progress.dismiss();

                //show_documents();
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
            uploadFile(Uri.parse("file://" + f.getAbsolutePath()));

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://notesinshort-d46ec.appspot.com");

            Uri u = Uri.fromFile(f);
            StorageReference riversRef = storageRef.child("images/"+u.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(u);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Log.d(TAG, "Download url for firebase image = " + downloadUrl);

                }
            });



        } else {
            Log.d(TAG, "Error in saving file.");
        }
    }

    public void show_documents() {
        getfile(root);

        for (int i = 0; i < fileList.size(); i++) {
            //System.out.println(fileList.get(i).getName());
            if (!fileList.get(i).isDirectory() && fileList.get(i).getName().endsWith(".pdf")) {
                Log.v(TAG, fileList.get(i).getName());
                files.add(fileList.get(i).getName());
                //textView.setTextColor(Color.parseColor("#FF0000"));
            } else {
                //view.addView(textView);
                Log.d(TAG, "Inside else of show documents.");
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
            f = new File(wallpaperDirectory, newPhotoName);

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

    private void uploadFile(Uri fileUri) {
        // create upload service client
        FileUploadService service =
                ServiceGenerator.createService(FileUploadService.class);

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        //File file = FileUtils.getFile(this, fileUri);

        File file = new File(fileUri.getPath());

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public interface FileUploadService {
        @Multipart
        @POST("upload")
        Call<ResponseBody> upload(@Part("description") RequestBody description,
                                  @Part MultipartBody.Part file);
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

}