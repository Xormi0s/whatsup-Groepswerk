package be.ucll.javafxspringboot.Models;

import com.google.cloud.firestore.Blob;

public class Message {

    private String userID;
    private String contactID;
    private String message;
    private Blob image;
    private Long timestamp = System.currentTimeMillis();

    public Message(String userID, String contactID, String message, Blob image) {
        this.userID = userID;
        this.contactID = contactID;
        this.message = message;
        this.image = image;
    }

    public Message(){}

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
