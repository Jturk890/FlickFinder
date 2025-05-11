package flickfinder.Controller;

import flickfinder.Model.MovieService;
import flickfinder.Model.UserService;
import flickfinder.View.LoginView;
import flickfinder.View.RecommendationView;
import flickfinder.View.SearchView;
import flickfinder.View.FavoritesView;
//import flickfinder.View.WatchlistView;
import flickfinder.Model.Movie;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MovieController {
    private final MovieService movieService;
    private final SearchView searchView;
    private RecommendationView recommendationView;
    private final UserService userService;
    private List<Movie> lastSearchResults;

    // In-memory favorites list
    private final List<Movie> favorites = new ArrayList<>();

    // In-memory watchlist
    private final List<Movie> watchlist = new ArrayList<>();

    public MovieController(MovieService movieService, SearchView searchView, UserService userService) {

        this.movieService = movieService;
        this.searchView = searchView;
        this.userService = userService;

        this.searchView.addSearchActionListener(new SearchActionListener());
        this.searchView.addTrendingActionListener(new TrendingMoviesActionListener());
        this.searchView.addMovieSelectionListener(new MovieSelectionListener());
        this.searchView.addLogoutListener(new LogoutActionListener());
        this.searchView.addRecommendationsListener(new RecommendationsListener());

        this.searchView.addAddToFavoritesListener(new AddToFavoritesListener());
        //this.searchView.addFavoritesListener(new FavoritesListener());

        // Watchlist functionality listeners
        this.searchView.addAddToWatchlistListener(new AddToWatchlistListener());
        //this.searchView.addWatchlistListener(new WatchlistListener());
    }

    // Listener for the search button
    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String query = searchView.getQuery();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(searchView, "Please enter a movie name!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            lastSearchResults = movieService.searchMovies(query);
            if (lastSearchResults.isEmpty()) {
                JOptionPane.showMessageDialog(searchView, "No movies found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] movieTitles = lastSearchResults.stream().map(Movie::getTitle).toArray(String[]::new);
            searchView.updateMovieList(movieTitles);
        }
    }

    // Listener for the trending movies button
    private class TrendingMoviesActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            lastSearchResults = movieService.getTrendingMovies();
            if (lastSearchResults.isEmpty()) {
                JOptionPane.showMessageDialog(searchView, "No trending movies found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] movieTitles = lastSearchResults.stream().map(Movie::getTitle).toArray(String[]::new);
            searchView.updateMovieList(movieTitles);
        }
    }

    // Listener for movie list selection to show movie details
    private class MovieSelectionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedMovie = searchView.getSelectedMovie();
            if (selectedMovie == null) return;
            for (Movie movie : lastSearchResults) {
                if (movie.getTitle().equals(selectedMovie)) {
                    String details = movieService.getMovieDetails(movie.getId());
                    searchView.showMovieDetails(details);
                    return;
                }
            }
        }
    }

    // Listener for logout action
    private class LogoutActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int confirm = JOptionPane.showConfirmDialog(
                    searchView,
                    "Are you sure you want to log out?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                searchView.dispose();
                LoginView newLoginView = new LoginView();
                SearchView newSearchView = new SearchView();
                new LoginController(newLoginView, newSearchView, movieService, userService);
            }
        }
    }

    // Listener for "Add to Favorites" button
    private class AddToFavoritesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedMovieTitle = searchView.getSelectedMovie();
            if (selectedMovieTitle == null) {
                JOptionPane.showMessageDialog(searchView, "Please select a movie to add to favorites.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (Movie movie : lastSearchResults) {
                if (movie.getTitle().equals(selectedMovieTitle)) {
                    if (!favorites.contains(movie)) {
                        favorites.add(movie);
                        JOptionPane.showMessageDialog(searchView, selectedMovieTitle + " added to favorites!");
                    } else {
                        JOptionPane.showMessageDialog(searchView, selectedMovieTitle + " is already in favorites.");
                    }
                    return;
                }
            }
            JOptionPane.showMessageDialog(searchView, "Selected movie not found.");
        }
    }

    // Listener for "Favorites" button to open the Favorites view
    private class FavoritesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            FavoritesView favoritesView = new FavoritesView();
            String[] favoriteTitles = favorites.stream().map(Movie::getTitle).toArray(String[]::new);
            favoritesView.updateFavoritesList(favoriteTitles);
            favoritesView.setVisible(true);
        }
    }

    // Listener for "Add to Watchlist" button
    private class AddToWatchlistListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedMovieTitle = searchView.getSelectedMovie();
            if (selectedMovieTitle == null) {
                JOptionPane.showMessageDialog(searchView, "Please select a movie to add to your watchlist.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (Movie movie : lastSearchResults) {
                if (movie.getTitle().equals(selectedMovieTitle)) {
                    if (!watchlist.contains(movie)) {
                        watchlist.add(movie);
                        JOptionPane.showMessageDialog(searchView, selectedMovieTitle + " added to your watchlist!");
                    } else {
                        JOptionPane.showMessageDialog(searchView, selectedMovieTitle + " is already in your watchlist.");
                    }
                    return;
                }
            }
            JOptionPane.showMessageDialog(searchView, "Selected movie not found.");
        }
    }

    // Listener for "Watchlist" button to open the Watchlist view
    private class WatchlistListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //WatchlistView watchlistView = new WatchlistView();
            String[] watchlistTitles = watchlist.stream().map(Movie::getTitle).toArray(String[]::new);
            //watchlistView.updateWatchlist(watchlistTitles);
            //watchlistView.setVisible(true);
        }
    }

    // Listener for "Recommendations" button to open the Recommendations view
private class RecommendationsListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Create the Recommendations view
        RecommendationView recommendationView = new RecommendationView();
        
        // If you need to pass data to the view (similar to favoritesTitles)
        // For example, if you have recommended movies:
        // String[] recommendedTitles = recommendations.stream()
        //     .map(Movie::getTitle)
        //     .toArray(String[]::new);
        // recommendationView.updateRecommendationsList(recommendedTitles);
        
        // Show the view
        recommendationView.setVisible(true);
        
        // Optional: Center the window
        recommendationView.setLocationRelativeTo(null);
    }
}
}