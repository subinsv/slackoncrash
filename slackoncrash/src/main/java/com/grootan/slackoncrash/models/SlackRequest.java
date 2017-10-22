
package com.grootan.slackoncrash.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SlackRequest {

    @SerializedName("attachments")
    @Expose
    private List<Attachment> attachments = null;

    public List<Attachment> getAttachments() {
        if(attachments == null)
        {
            attachments =new ArrayList<>();
        }
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public SlackRequest withAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
        return this;
    }

}
