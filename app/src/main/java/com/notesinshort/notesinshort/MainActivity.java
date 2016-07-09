package com.notesinshort.notesinshort;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by mayank on 9/7/16.
 */
public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    NoteAdapter adapter;
    ArrayList<Note> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new NoteAdapter(list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(manager);
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
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

    }

    public void prepareMovieData(){

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

    public interface ClickListener {
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
}