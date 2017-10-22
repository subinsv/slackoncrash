
package com.grootan.slackoncrash.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attachment {

    @SerializedName("fields")
    @Expose
    private List<Field> fields = null;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("mrkdwn_in")
    @Expose
    private List<String> mrkdwnIn = null;

    public List<Field> getFields() {
        if(fields == null)
        {
            fields =new ArrayList<>();
        }
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Attachment withFields(List<Field> fields) {
        this.fields = fields;
        return this;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Attachment withColor(String color) {
        this.color = color;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Attachment withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Attachment withText(String text) {
        this.text = text;
        return this;
    }

    public List<String> getMrkdwnIn() {
        if(mrkdwnIn == null)
        {
            mrkdwnIn = new ArrayList<>();
        }
        return mrkdwnIn;
    }

    public void setMrkdwnIn(List<String> mrkdwnIn) {
        this.mrkdwnIn = mrkdwnIn;
    }

    public Attachment withMrkdwnIn(List<String> mrkdwnIn) {
        this.mrkdwnIn = mrkdwnIn;
        return this;
    }

}
