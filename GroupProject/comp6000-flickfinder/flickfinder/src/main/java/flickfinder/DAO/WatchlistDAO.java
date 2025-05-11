package flickfinder.DAO;

import flickfinder.Model.Movie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchlistDAO {

    // Saves the watchlist for the given user.
    public boolean saveWatchlist(int userId, List<Movie> watchlist) {
        if (watchlist == null) {
            return false;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Clear out any existing watchlist entries for the user.
            String deleteSql = "DELETE FROM user_watchlist WHERE user_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, userId);
                deleteStmt.executeUpdate();
            }

            // Insert the new watchlist entries.
            String sql = "INSERT INTO user_watchlist (user_id, movie_id, title, poster_path, release_date, overview, popularity, rating, category) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Movie movie : watchlist) {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, movie.getId());
                    stmt.setString(3, movie.getTitle());
                    stmt.setString(4, movie.getPosterPath());
                    stmt.setString(5, movie.getReleaseDate());
                    stmt.setString(6, movie.getOverview());
                    stmt.setDouble(7, movie.getPopularity());
                    stmt.setDouble(8, movie.getRating());
                    stmt.setString(9, movie.getCategory());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Retrieves the watchlist for the given user from the database.
    public List<Movie> getWatchlist(int userId) {
        List<Movie> watchlist = new ArrayList<>();
        String sql = "SELECT movie_id, title, poster_path, release_date, overview, popularity, rating, category FROM user_watchlist WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getString("poster_path"),
                        rs.getString("release_date"),
                        rs.getString("overview"),
                        rs.getDouble("popularity"),
                        rs.getDouble("rating"),
                        rs.getString("category"),
                        new ArrayList<>() // Using an empty list for genres.
                );
                watchlist.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return watchlist;
    }
}