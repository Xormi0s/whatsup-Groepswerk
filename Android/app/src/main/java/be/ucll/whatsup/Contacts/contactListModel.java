package be.ucll.whatsup.Contacts;

import java.util.Objects;

public class contactListModel {

    private String contactID;
    private String userID;
    private String name;

    public contactListModel() {
    }

    public contactListModel(String contactID, String userID) {
        this.contactID = contactID;
        this.userID = userID;
    }

    public contactListModel(String contactID, String userID, String name) {
        this.contactID = contactID;
        this.userID = userID;
        this.name = name;
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

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        contactListModel that = (contactListModel) o;
        return Objects.equals(contactID, that.contactID) &&
                Objects.equals(userID, that.userID) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactID, userID, name);
    }
}
