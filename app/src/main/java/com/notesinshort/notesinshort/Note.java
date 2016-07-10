package com.notesinshort.notesinshort;

import android.widget.ImageView;

/**
 * Created by Gurpreet on 09/07/16.
 */
public class Note {

    private String summary, image, keywords, entities, text;
    String  reaction;
    public Note(){
    }

    public Note(String summary, String text, String image, String reaction, String keywords, String entities) {
        this.summary = summary;
        this.image = image;
        this.reaction = reaction;
        this.entities = entities;
        this.text = text;
        this.keywords = keywords;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getText() {
        return text;
    }

    public void setText(String summary) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getEntities() {
        return entities;
    }

    public void setEntities(String entities) {
        this.entities = entities;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }
}