import java.math.BigDecimal;

public class Dish {
    private String title;
    private BigDecimal price;
    private int preparationTime;
    private String imageUrl;
    private static final String reg = "/";

    public Dish(String title, BigDecimal price, int preparationTime, String imageUrl) throws RestException {
        setTitle(title);
        setPrice(price);
        setPreparationTime(preparationTime);
        setImageUrl(imageUrl);
    }
    public Dish(String title, BigDecimal price, int preparationTime) throws RestException {
        setTitle(title);
        setPrice(price);
        setPreparationTime(preparationTime);
        setImageUrl("blanc");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws RestException {
        if (title != null && !title.isEmpty()) {
            this.title = title;
        } else {
            throw new RestException("nazev nesmi byt prazdny");
        }
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) throws RestException {
        if (price.compareTo(BigDecimal.ZERO) > 0) {
            this.price = price;
        } else {
            throw new RestException("cena nemuze byt zaporna");
        }
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) throws RestException {
        if (preparationTime > 0) {
            this.preparationTime = preparationTime;
        } else {
            throw new RestException("cas pripraveni nesmi byt zaporny");
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "{" + title + reg+'\t'  + price + reg +"\t" + preparationTime + reg +"\t" + imageUrl+ "}";
    }
    public static String getReg() {
        return reg;
    }

}
