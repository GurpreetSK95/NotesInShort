package com.notesinshort.notesinshort;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gurpreet on 09/07/16.
 */
public class NotesData {

    @SerializedName("image_text")
    private String summary;
    @SerializedName("relevant_images")
    private String image;
    @SerializedName("positive_sentiment_score")
    private String reaction;
    @SerializedName("keywords")
    private String keywords;
    @SerializedName("useful_entities")
    private String useful_entities;

    public NotesData(String summary, String image, String reaction, String keywords, String useful_entities) {
        this.summary = summary;
        this.image = image;
        this.reaction = reaction;
        this.keywords = keywords;
        this.useful_entities = useful_entities;
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

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getUseful_entities() {
        return useful_entities;
    }

    public void setUseful_entities(String useful_entities) {
        this.useful_entities = useful_entities;
    }
}
