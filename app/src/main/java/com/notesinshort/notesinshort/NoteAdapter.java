package com.notesinshort.notesinshort;

import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gurpreet on 09/07/16.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder>{

    ArrayList<Note> list = new ArrayList<>();

    public NoteAdapter(ArrayList<Note> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note movie = list.get(position);
        holder.title.setText(movie.getTitle());
        //holder.genre.setText(movie.getGenre());
        //holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView title, genre, year;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            //genre = (TextView) itemView.findViewById(R.id.genre);
            //year = (TextView) itemView.findViewById(R.id.year);
        }

    }

}
