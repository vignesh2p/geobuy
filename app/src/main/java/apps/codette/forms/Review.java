package apps.codette.forms;

public class Review {

    private String id;
    private String heading;
    private String review;
    private String time;
    private float ratings;
    private String user;
    private String userName;

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", heading='" + heading + '\'' +
                ", review='" + review + '\'' +
                ", time='" + time + '\'' +
                ", ratings=" + ratings +
                ", user='" + user + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public Review(String id, String heading, String review, String time, float ratings, String user){
        this.id = id;
        this.heading = heading;
        this.review = review;
        this.time = time;
        this.ratings = ratings;
        this.user = user;
    }



    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getRatings() {
        return ratings;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
