package project.projectapp.NewsFragment.Comments;

public class Comment {

    private String username, userProfileImage, commentText, commentDate, commentTime;

    public Comment(String username, String userProfileImage, String commentText, String commentDate,
                   String commentTime){
        this.username = username;
        this.userProfileImage = userProfileImage;
        this.commentText = commentText;
        this.commentDate = commentDate;
        this.commentTime = commentTime;
    }

    public Comment(){ }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }
}
