package apps.codette.forms;

import java.util.List;

/**
 * Created by user on 25-03-2018.
 */

public class Category {
    private String id;
    private String subtittle;
    private String tittle;
    private String image;
    private String category;
    private boolean isBanner;
    private boolean isOrg;
    private boolean isProducts;
    private String[] linkId;
    private List<SubCategory> subcategory;

    public boolean isOrg() {
        return isOrg;
    }

    public void setOrg(boolean org) {
        isOrg = org;
    }

    public boolean isProducts() {
        return isProducts;
    }

    public void setProducts(boolean products) {
        isProducts = products;
    }

    public String[] getLinkId() {
        return linkId;
    }

    public void setLinkId(String[] linkId) {
        this.linkId = linkId;
    }

    public boolean isBanner() {
        return isBanner;
    }

    public void setBanner(boolean banner) {
        isBanner = banner;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }




    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubtittle() {
        return subtittle;
    }

    public void setSubtittle(String subtittle) {
        this.subtittle = subtittle;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public List<SubCategory> getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(List<SubCategory> subcategory) {
        this.subcategory = subcategory;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", subtittle='" + subtittle + '\'' +
                ", tittle='" + tittle + '\'' +
                '}';
    }
}
