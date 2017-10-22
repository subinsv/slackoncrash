
package com.grootan.slackoncrash.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Field {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("short")
    @Expose
    private boolean _short;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Field withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Field withValue(String value) {
        this.value = value;
        return this;
    }

    public Boolean getShort() {
        return _short;
    }

    public void setShort(boolean _short) {
        this._short = _short;
    }

    public Field withShort(boolean _short) {
        this._short = _short;
        return this;
    }

}
