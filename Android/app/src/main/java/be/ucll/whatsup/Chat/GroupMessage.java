package be.ucll.whatsup.Chat;

import com.google.firebase.firestore.Blob;

public class GroupMessage {

    private String groupID;
    private String senderID;
    private String message;
    private Blob image;
    private Long timestamp = System.currentTimeMillis();
    private String name;

    public GroupMessage() {

    }

    public GroupMessage(String groupID, String senderID, String message, Blob image, String name) {
        this.groupID = groupID;
        this.senderID = senderID;
        this.message = message;
        this.image = image;
        this.name = name;
    }



    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
