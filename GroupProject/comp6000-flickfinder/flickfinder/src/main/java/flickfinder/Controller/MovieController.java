package flickfinder.Controller;

import flickfinder.Model.Movie;
import flickfinder.Model.MovieService;
import flickfinder.Model.UserService;
import flickfinder.View.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MovieController {

    private final MovieService movieService;
    private final SearchView searchView;
    private final UserService userService;

    private List<Movie> lastSearchResults = new ArrayList<>();

    // In-memory lists (consider persisting later)
    private final List<Movie> favorites = new ArrayList<>();
    private final List<Movie> watchlist = new ArrayList<>();

    // Views that need to live across actions
    private AccountView accountView;

    public MovieController(MovieService movieService, SearchView searchView, UserService userService) {
        this.movieService = movieService;
        this.searchView = searchView;
        this.userService = userService;

        wireSearchViewActions();
    }

    // -------------------- Wiring --------------------
    private void wireSearchViewActions() {
        searchView.addSearchActionListener(e -> handleSearch());
        searchView.addTrendingActionListener(e -> handleTrending());
        searchView.addMovieSelectionListener(e -> handleMovieSelection());
        searchView.addLogoutListener(e -> handleLogout());
        searchView.addRecommendationsListener(e -> handleRecommendations());
        searchView.addAddToFavoritesListener(e -> handleAddToList(favorites, "favorites"));
        searchView.addAddToWatchlistListener(e -> handleAddToList(watchlist, "watchlist"));
        searchView.addGenresActionListener(e -> handleGenreBrowse());
        searchView.addAccountActionListener(e -> handleAccount());
    }

    // -------------------- Handlers --------------------
    private void handleSearch() {
        String query = searchView.getQuery();
        if (query.isBlank()) {
            info("Please enter a movie name!");
            return;
        }
        lastSearchResults = movieService.searchMovies(query);
        updateSearchListOrNotify(lastSearchResults, "No movies found.");
    }

    private void handleTrending() {
        lastSearchResults = movieService.getTrendingMovies();
        updateSearchListOrNotify(lastSearchResults, "No trending movies found.");
    }

    private void handleMovieSelection() {
        String selectedTitle = searchView.getSelectedMovie();
        if (selectedTitle == null) return;

        lastSearchResults.stream()
                .filter(m -> m.getTitle().equals(selectedTitle))
                .findFirst()
                .ifPresentOrElse(
                        m -> searchView.showMovieDetails(movieService.getMovieDetails(m.getId())),
                        () -> info("Selected movie not found."));
    }

    private void handleLogout() {
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

    private void handleAddToList(List<Movie> target, String listName) {
        String selectedTitle = searchView.getSelectedMovie();
        if (selectedTitle == null) {
            info("Please select a movie to add to " + listName + ".");
            return;
        }
        lastSearchResults.stream()
                .filter(m -> m.getTitle().equals(selectedTitle))
                .findFirst()
                .ifPresentOrElse(movie -> {
                    if (target.contains(movie)) {
                        info(selectedTitle + " is already in your " + listName + ".");
                    } else {
                        target.add(movie);
                        info(selectedTitle + " added to your " + listName + "!");
                    }
                }, () -> info("Selected movie not found."));
    }

    private void handleRecommendations() {
        Set<String> preferredGenres = extractGenresFromFavoritesAndWatchlist();
        RecommendationView recommendationView = new RecommendationView(movieService, preferredGenres);
        recommendationView.setVisible(true);
        recommendationView.setLocationRelativeTo(null);
    }

    private void handleGenreBrowse() {
        Map<Integer, String> genresMap = movieService.getGenres();
        if (genresMap.isEmpty() || genresMap.containsKey(-1)) {
            info("No genres found or an error occurred.");
            return;
        }

        String[] genreNames = genresMap.values().toArray(new String[0]);
        String selectedGenre = (String) JOptionPane.showInputDialog(
                searchView,
                "Select a genre:",
                "Genres",
                JOptionPane.PLAIN_MESSAGE,
                null,
                genreNames,
                genreNames[0]
        );

        if (selectedGenre == null) return;

        int selectedGenreId = genresMap.entrySet().stream()
                .filter(e -> e.getValue().equals(selectedGenre))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);

        if (selectedGenreId != -1) {
            lastSearchResults = movieService.getMoviesByGenre(selectedGenreId);
            updateSearchListOrNotify(lastSearchResults, "No movies found for selected genre.");
        }
    }

    private void handleAccount() {
        accountView = new AccountView();
        updateAccountLists();
        accountView.setVisible(true);
        accountView.getBackButton().addActionListener(ev -> accountView.dispose());

        // favorites selection
        accountView.getFavoritesList().addListSelectionListener(ev -> {
            boolean selected = accountView.getFavoritesList().getSelectedIndex() != -1;
            accountView.getRemoveFromFavoritesButton().setEnabled(selected);
            if (selected) showDetailsInAccount(accountView.getFavoritesList().getSelectedValue(), favorites);
        });

        // watchlist selection
        accountView.getWatchlistList().addListSelectionListener(ev -> {
            boolean selected = accountView.getWatchlistList().getSelectedIndex() != -1;
            accountView.getRemoveFromWatchlistButton().setEnabled(selected);
            if (selected) showDetailsInAccount(accountView.getWatchlistList().getSelectedValue(), watchlist);
        });

        accountView.getRemoveFromFavoritesButton().addActionListener(ev -> removeSelectedFromList(
                accountView.getFavoritesList().getSelectedValue(), favorites));

        accountView.getRemoveFromWatchlistButton().addActionListener(ev -> removeSelectedFromList(
                accountView.getWatchlistList().getSelectedValue(), watchlist));
    }

    // -------------------- Helpers --------------------
    private void updateSearchListOrNotify(List<Movie> movies, String emptyMsg) {
        if (movies.isEmpty()) {
            info(emptyMsg);
            return;
        }
        String[] titles = movies.stream().map(Movie::getTitle).toArray(String[]::new);
        searchView.updateMovieList(titles);
    }

    private void updateAccountLists() {
        accountView.updateFavorites(favorites.stream().map(Movie::getTitle).toArray(String[]::new));
        accountView.updateWatchlist(watchlist.stream().map(Movie::getTitle).toArray(String[]::new));
    }

    private void showDetailsInAccount(String title, List<Movie> source) {
        Movie m = source.stream().filter(mm -> mm.getTitle().equals(title)).findFirst().orElse(null);
        if (m == null) return;

        String details = movieService.getMovieDetails(m.getId());
        accountView.showMovieDetails(details);
        setPosterImage(m.getPosterPath());
    }

    private void setPosterImage(String posterPath) {
        if (posterPath == null || posterPath.equals("N/A")) {
            accountView.setPosterImage(null);
            return;
        }
        String imageUrl = "https://image.tmdb.org/t/p/w200" + posterPath;
        try {
            ImageIcon icon = new ImageIcon(new java.net.URL(imageUrl));
            Image scaled = icon.getImage().getScaledInstance(160, 240, Image.SCALE_SMOOTH);
            accountView.setPosterImage(new ImageIcon(scaled));
        } catch (Exception ex) {
            accountView.setPosterImage(null);
        }
    }

    private void removeSelectedFromList(String title, List<Movie> list) {
        if (title == null) return;
        int confirm = JOptionPane.showConfirmDialog(accountView,
                "Remove \"" + title + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            list.removeIf(m -> m.getTitle().equals(title));
            updateAccountLists();
            accountView.showMovieDetails("");
            accountView.setPosterImage(null);
        }
    }

    private Set<String> extractGenresFromFavoritesAndWatchlist() {
        Set<String> genres = new HashSet<>();
        List<Movie> combined = new ArrayList<>();
        combined.addAll(favorites);
        combined.addAll(watchlist);

        for (Movie movie : combined) {
            try {
                String json = movieService.getMovieDetailsRaw(movie.getId());
                org.json.JSONObject obj = new org.json.JSONObject(json);
                org.json.JSONArray arr = obj.optJSONArray("genres");
                if (arr == null) continue;
                for (int i = 0; i < arr.length(); i++) {
                    genres.add(arr.getJSONObject(i).getString("name"));
                }
            } catch (Exception ignored) {
            }
        }
        return genres;
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(searchView, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}