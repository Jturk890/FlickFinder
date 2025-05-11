package flickfinder.Model;

import java.util.List;

public class Movie {
    private int id;
    private String title;
    private String posterPath;
    private String releaseDate;
    private String overview;
    private double popularity;
    private double rating;
    private String category;
    private List<String> genres;

    public Movie(int id, String title, String posterPath, String releaseDate, String overview, double popularity, double rating, String category, List<String> genres) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.popularity = popularity;
        this.rating = rating;
        this.category = category;
        this.genres = genres;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public double getRating() {
        return rating;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getGenres() {
        return genres;
    }
}