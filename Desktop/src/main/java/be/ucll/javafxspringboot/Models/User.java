package be.ucll.javafxspringboot.Models;

public class User {

    private String name;
    private String number;
    private String uid;

    public User() {
    }

    public User(String name, String number, String uid) {
        this.name = name;
        this.number = number;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
