package be.ucll.javafxspringboot.Models;

import com.google.cloud.firestore.Blob;

public class GroupMessages {

    private String groupID;
    private String message;
    private String senderID;
    private Blob image;
    private Long timestamp = System.currentTimeMillis();

    public GroupMessages(String groupID, Blob image, String message, String senderID) {
        this.groupID = groupID;
        this.image = image;
        this.message = message;
        this.senderID = senderID;
    }

    public GroupMessages(){}

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
