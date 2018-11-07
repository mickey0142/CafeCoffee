package Model;

import java.io.Serializable;

public class User implements Serializable {
    private String email;
    private String username;
    private String type;
    private String pictureName;

    public User()
    {

    }

    public User(String email, String username, String type)
    {
        this.email = email;
        this.username = username;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }
}
