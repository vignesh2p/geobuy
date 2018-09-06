package apps.codette.forms;

import java.util.Arrays;
import java.util.List;

public class Product {
    private String id;
    private String orgname;
    private String orgid;
    private String title;
    private String shortdesc;
    private String code;
    private String[] image;
    private List<Review> reviews;
    private String searchkey;
    private int offer;
    private String brand;
    private List<String> highlights;
    private List<Rating> ratings;
    private int price;
    private float rating;
    private List<Product> productDetails;
    private String masterid;
    private int quanity =1;
    private float amountToBePaid;
    private int gpriority;
    private String orderStatus="O";

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Product(String id, int quanity){
        this.id = id;
        this.quanity = quanity;
    }
    public Product(){
    }

    public String getMasterid() {
        return masterid;
    }

    public void setMasterid(String masterid) {
        this.masterid = masterid;
    }

    public List<Product> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<Product> productDetails) {
        this.productDetails = productDetails;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    @Override

    public String toString() {
        return "Product{" +
                "id=" + id +
                ", orgid='"+orgid + '\'' +
                ", title='" + title + '\'' +
                ", shortdesc='" + shortdesc + '\'' +
                ", code='" + code + '\'' +
                ", image=" + Arrays.toString(image) +
                ", reviews=" + reviews +
                ", searchkey='" + searchkey + '\'' +
                ", offer=" + offer +
                ", brand='" + brand + '\'' +
                ", highLights='" +highlights + '\'' +
                '}';
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getOffer() {
        return offer;
    }

    public void setOffer(int offer) {
        this.offer = offer;
    }

    public String getSearchkey() {
        return searchkey;
    }

    public void setSearchkey(String searchkey) {
        this.searchkey = searchkey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortdesc() {
        return shortdesc;
    }

    public void setShortdesc(String shortdesc) {
        this.shortdesc = shortdesc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String[] getImage() {
        return image;
    }

    public void setImage(String[] image) {
        this.image = image;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public void setHighLights(List<String> highlights) {
        this.highlights = highlights;
    }

    public void setHighlights(List<String> highlights) {
        this.highlights = highlights;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public int getQuanity() {
        return quanity;
    }

    public void setQuanity(int quanity) {
        this.quanity = quanity;
    }

    public float getAmountToBePaid() {
        return amountToBePaid;
    }

    public void setAmountToBePaid(float amountToBePaid) {
        this.amountToBePaid = amountToBePaid;
    }

    public int getGpriority() {
        return gpriority;
    }

    public void setGpriority(int gpriority) {
        this.gpriority = gpriority;
    }
}
