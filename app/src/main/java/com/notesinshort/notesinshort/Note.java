package com.notesinshort.notesinshort;

/**
 * Created by Gurpreet on 09/07/16.
 */
public class Note {

    private String summary, image, keywords, entities;
    int reaction;
    public Note(){
    }

    public Note(String summary, String image, int reaction, String keywords, String entities) {
        this.summary = summary;
        this.image = image;
        this.reaction = reaction;
        this.entities = entities;
        this.keywords = keywords;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }
}