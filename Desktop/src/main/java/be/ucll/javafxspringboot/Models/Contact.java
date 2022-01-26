package be.ucll.javafxspringboot.Models;

public class Contact {
    private String contactID;
    private String userID;
    private String name;
    private Boolean isSelected = true;

    public Contact(String contactID, String userID, String name) {
        this.contactID = contactID;
        this.userID = userID;
        this.name = name;
    }

    public Contact() {
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String contactName) {
        this.name = contactName;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
