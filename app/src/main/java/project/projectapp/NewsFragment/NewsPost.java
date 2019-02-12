package project.projectapp.NewsFragment;

import android.net.Uri;

public class NewsPost {
    private String title;
    private String content;
    private String username;
    private String profileImage;
    private String date;
    private String time;

    public NewsPost(String title, String content, String username, String profileImage,
                    String date, String time) {
        this.title = title;
        this.content = content;
        this.username = username;
        this.profileImage = profileImage;
        this.date = date;
        this.time = time;
    }

    public NewsPost(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
