package apps.codette.forms;

/**
 * Created by user on 25-03-2018.
 */

public class Category {
    private int id;
    private String subtittle;
    private String tittle;
    private String image;
    private String category;


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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", subtittle='" + subtittle + '\'' +
                ", tittle='" + tittle + '\'' +
                '}';
    }
}
