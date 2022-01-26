package be.ucll.whatsup.Contacts;

public class GroupContact {
    private String id;
    private String name;
    private String user1, user2, user3, user4;
    private String name1, name2, name3, name4;

    public GroupContact() {
    }

    public GroupContact(String id, String name, String user1, String user2, String name1, String name2) {
        this.id  = id;
        this.name = name;
        this.user1 = user1;
        this.user2 = user2;
        this.name1 = name1;
        this.name2 = name2;
    }

    public GroupContact(String id, String name, String name1, String name2, String name3, String user1, String user2, String user3 ) {
        this.id  = id;
        this.name = name;
        this.user1 = user1;
        this.user2 = user2;
        this.user3 = user3;
        this.name1 = name1;
        this.name2 = name2;
        this.name3 = name3;
    }

    public GroupContact(String id, String name, String name1, String name2, String name3, String name4, String user1, String user2, String user3, String user4) {
        this.id  = id;
        this.name = name;
        this.user1 = user1;
        this.user2 = user2;
        this.user3 = user3;
        this.user4 = user4;
        this.name1 = name1;
        this.name2 = name2;
        this.name3 = name3;
        this.name4 = name4;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getUser3() {
        return user3;
    }

    public void setUser3(String user3) {
        this.user3 = user3;
    }

    public String getUser4() {
        return user4;
    }

    public void setUser4(String user4) {
        this.user4 = user4;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getName4() {
        return name4;
    }

    public void setName4(String name4) {
        this.name4 = name4;
    }
}
