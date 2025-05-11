package flickfinder.Model;

import flickfinder.Model.Movie;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

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

    /**
     * Returns a list of recommended movies based on the provided genres.
     *
     * @param genres a set of genres (keywords) derived from the user's favorites and watchlist.
     * @return a list of recommended Movie objects.
     */
    public List<Movie> getRecommendations(Set<String> genres) {
        // For simplicity, we fetch trending movies and then filter by checking if
        // the movie title contains any of the genre keywords.
        // (A real implementation should use proper genre data.)
        List<Movie> trendingMovies = getTrendingMovies();
        List<Movie> recommendations = new ArrayList<>();

        for (Movie movie : trendingMovies) {
            // Simple check: if the movie title (or category) contains any of the genre keywords.
            for (String genre : genres) {
                if (movie.getTitle().toLowerCase().contains(genre.toLowerCase())) {
                    recommendations.add(movie);
                    break;
                }
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
}