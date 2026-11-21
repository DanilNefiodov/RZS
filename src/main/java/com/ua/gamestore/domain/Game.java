package com.ua.gamestore.domain;

import java.math.BigDecimal;

/**
 * Відеогра — доменна модель.
 *
 * ШАР: domain. Звичайний Java-клас без анотацій Spring.
 */
public class Game {

    private Long id;
    private String title;
    private String developer;      // студія-розробник
    private BigDecimal price;
    private Integer releaseYear;
    private String platform;       // PC, PlayStation 5, Xbox Series X|S, Nintendo Switch
    private String genre;
    private Integer ageRating;     // вікове обмеження PEGI: 3, 7, 12, 16, 18
    private String coverUrl;       // обкладинка (для картки)
    private String description;    // опис (для сторінки гри)

    public Game() {
    }

    public Game(Long id, String title, String developer, BigDecimal price,
                Integer releaseYear, String platform, String genre, Integer ageRating,
                String coverUrl, String description) {
        this.id = id;
        this.title = title;
        this.developer = developer;
        this.price = price;
        this.releaseYear = releaseYear;
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
        this.coverUrl = coverUrl;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(Integer ageRating) {
        this.ageRating = ageRating;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
