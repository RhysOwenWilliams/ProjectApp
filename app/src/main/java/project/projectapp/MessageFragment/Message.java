package project.projectapp.MessageFragment;

public class Message {

    private String body, time, date, user, userId;

    public Message(String body, String time, String date, String user, String userId){
        this.body = body;
        this.time = time;
        this.date = date;
        this.user = user;
        this.userId = userId;
    }

    public Message(){

    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
