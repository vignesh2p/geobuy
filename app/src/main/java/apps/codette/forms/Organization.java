package apps.codette.forms;

import java.io.Serializable;
import java.util.List;

/**
 * Created by user on 24-03-2018.
 */

public class Organization implements Serializable{
    private String orgname;
    private String orgphoneno;
    private String orgemail;
    private String orgid;
    private double orgLat;
    private double orgLon;
    private List<Product> products;
    private String[] followers;
    private boolean isPrime = false;
    private String category;
    private String orgaddress;
    private String[] images;
    private String logo;
    private List<Review> reviews;
    private float rating;


    public float getRating() {
        return rating;
    }

    public void setRating(float ratings) {
        this.rating = ratings;
    }

    public Organization (String orgname, String[] images){
        this.orgname = orgname;
        this.images = images;
    }

    public Organization (String orgname, String orgid){
        this.orgname = orgname;
        this.orgid = orgid;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getOrgemail() {
        return orgemail;
    }

    public void setOrgemail(String orgemail) {
        this.orgemail = orgemail;
    }

    public String getOrgaddress() {
        return orgaddress;
    }

    public void setOrgaddress(String orgaddress) {
        this.orgaddress = orgaddress;
    }
    public boolean isPrime() {
        return isPrime;
    }

    public void setPrime(boolean prime) {
        isPrime = prime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getOrgphoneno() {
        return orgphoneno;
    }

    public void setOrgphoneno(String orgphoneno) {
        this.orgphoneno = orgphoneno;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public double getOrgLat() {
        return orgLat;
    }

    public void setOrgLat(double orgLat) {
        this.orgLat = orgLat;
    }

    public double getOrgLon() {
        return orgLon;
    }

    public void setOrgLon(double orgLon) {
        this.orgLon = orgLon;
    }

    public  List<Product> getProducts() {
        return products;
    }

    public void setProducts( List<Product> products) {
        this.products = products;
    }

    public String[] getFollowers() {
        return followers;
    }

    public void setFollowers(String[] followers) {
        this.followers = followers;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "orgname='" + orgname + '\'' +
                ", orgphoneno='" + orgphoneno + '\'' +
                ", orgid='" + orgid + '\'' +
                ", orgLat=" + orgLat +
                ", orgLon=" + orgLon +
                ", isPrime=" + isPrime +
                ", category='" + category + '\'' +
                '}';
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
