package com.notesinshort.notesinshort;

/**
 * Created by Gurpreet on 09/07/16.
 */

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("")
    void getNotes();

}