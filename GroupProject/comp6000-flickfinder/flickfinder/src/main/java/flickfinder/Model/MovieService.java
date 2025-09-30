package flickfinder.Model;

import flickfinder.Model.Movie;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;


@Service
public class MovieService {

    private static final String API_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5NWYzNmUwN2ZiMTA2NTE1ZWFiMzJjMmNhNTQ2YmMwNCIsIm5iZiI6MTczMzY4MzYwNy40NzQsInN1YiI6IjY3NTVlOTk3NzY0ZmQ5NGU0MjJmNTdlOCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.OLG4MZYCi-Su7Jzx8KsYkYwIhE-ysn652GBlkj6jQbs";
    private static final String API_SEARCH_URL = "https://api.themoviedb.org/3/search/movie?query=";
    private static final String API_TRENDING_URL = "https://api.themoviedb.org/3/trending/movie/day";
    private static final String API_MOVIE_DETAILS_URL = "https://api.themoviedb.org/3/movie/";

    /**
     * Search for movies by title using TMDb API.
     */
    public List<Movie> searchMovies(String query) {
        List<Movie> movies = new ArrayList<>();
        try {
            String formattedQuery = query.replace(" ", "%20");
            String requestUrl = API_SEARCH_URL + formattedQuery + "&include_adult=false&language=en-US&page=1";
            movies = fetchMovies(requestUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }

    /**
     * Fetch trending movies from TMDb API.
     */
    public List<Movie> getTrendingMovies() {
        List<Movie> movies = new ArrayList<>();
        try {
            movies = fetchMovies(API_TRENDING_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }

    /**
     * Fetch detailed movie information by movie ID.
     */
    public String getMovieDetails(int movieId) {
        try {
            String requestUrl = API_MOVIE_DETAILS_URL + movieId;
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String title = jsonResponse.getString("title");
            String overview = jsonResponse.optString("overview", "No description available.");
            String releaseDate = jsonResponse.optString("release_date", "Unknown");
            double rating = jsonResponse.optDouble("vote_average", 0.0);

            return "Title: " + title + "\nRelease Date: " + releaseDate + "\nRating: " + rating + "\n\n" + overview;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching movie details.";
        }
    }

    public List<Movie> getRecommendations(Set<String> userGenres) {
        List<Movie> trendingMovies = getTrendingMovies();
        List<Movie> recommendations = new ArrayList<>();

        for (Movie movie : trendingMovies) {
            try {
                String detailsJson = getMovieDetailsRaw(movie.getId());
                JSONObject json = new JSONObject(detailsJson);
                JSONArray genreArray = json.getJSONArray("genres");

                for (int i = 0; i < genreArray.length(); i++) {
                    String genreName = genreArray.getJSONObject(i).getString("name");

                    for (String userGenre : userGenres) {
                        if (genreName.equalsIgnoreCase(userGenre)) {
                            recommendations.add(movie);
                            break;
                        }
                    }

                    // Stop looping genres if already added
                    if (recommendations.contains(movie)) break;
                }
            } catch (Exception e) {
                System.err.println("Failed to get genre info for: " + movie.getTitle());
            }
        }

        return recommendations;
    }   


    /**
     * Helper method to fetch movies from an API URL.
     */
    private List<Movie> fetchMovies(String requestUrl) throws Exception {
        List<Movie> movies = new ArrayList<>();
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
        conn.setRequestProperty("Accept", "application/json");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray results = jsonResponse.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject movieJson = results.getJSONObject(i);
            int id = movieJson.getInt("id");
            String title = movieJson.getString("title");
            String posterPath = movieJson.optString("poster_path", "N/A");
            String overview = movieJson.optString("overview", "No description available.");
            String releaseDate = movieJson.optString("release_date", "Unknown");
            double popularity = movieJson.optDouble("popularity", 0.0);
            double rating = movieJson.optDouble("vote_average", 0.0);

            // Pass an empty list for genres since the API call does not provide genre data.
            movies.add(new Movie(id, title, posterPath, releaseDate, overview, popularity, rating, "Search Result", new ArrayList<>()));
        }
        return movies;
    }

    public Map<Integer, String> getGenres() {
    Map<Integer, String> genres = new LinkedHashMap<>();
    try {
        String urlStr = "https://api.themoviedb.org/3/genre/movie/list?language=en";
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
        conn.setRequestProperty("Accept", "application/json");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray genresArray = jsonResponse.getJSONArray("genres");

        for (int i = 0; i < genresArray.length(); i++) {
            JSONObject genre = genresArray.getJSONObject(i);
            int id = genre.getInt("id");
            String name = genre.getString("name");
            genres.put(id, name);
        }
    } catch (Exception e) {
        e.printStackTrace();
        genres.put(-1, "Failed to fetch genres.");
    }
    return genres;
}


    public List<Movie> getMoviesByGenre(int genreId) {
        List<Movie> movies = new ArrayList<>();
        try {
            String url = "https://api.themoviedb.org/3/discover/movie?with_genres=" + genreId + "&language=en-US&sort_by=popularity.desc";
            movies = fetchMovies(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }

    public String getMovieDetailsRaw(int movieId) {
        try {
            String requestUrl = API_MOVIE_DETAILS_URL + movieId;
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString(); // ← Raw JSON string
        } catch (Exception e) {
            e.printStackTrace();
            return "{}"; // Return empty JSON on error
        }
    }
}