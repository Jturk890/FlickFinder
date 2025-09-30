package flickfinder.Controller;

import flickfinder.DAO.FavoritesDAO;
import flickfinder.DAO.WatchlistDAO;
import flickfinder.Model.Movie;
import flickfinder.Model.UserService;
import flickfinder.View.FavoritesView;
import flickfinder.View.UserAccountView;

import javax.swing.*;
import java.util.List;

public class AccountController {
    private final UserAccountView accountView;
    private final UserService userService;
    private final FavoritesDAO favoritesDAO;
    private final WatchlistDAO watchlistDAO;

    public AccountController(UserAccountView accountView, UserService userService) {
        this.accountView = accountView;
        this.userService = userService;
        this.favoritesDAO = new FavoritesDAO();
        this.watchlistDAO = new WatchlistDAO();
        initController();
    }

    private void initController() {
        // Load and display the user's favorites when the Favorites button is pressed.
        accountView.addFavoritesActionListener(e -> {
            int userId = userService.getCurrentUserId();
            List<Movie> favorites = favoritesDAO.getFavorites(userId);
            if (favorites.isEmpty()) {
                JOptionPane.showMessageDialog(accountView, "No favorites found.");
            } else {
                FavoritesView favoritesView = new FavoritesView();
                String[] favoriteTitles = favorites.stream().map(Movie::getTitle).toArray(String[]::new);
                favoritesView.updateFavoritesList(favoriteTitles);
                favoritesView.setVisible(true);
            }
        });

        // Load and display the user's watchlist when the Watchlist button is pressed.
        accountView.addWatchlistActionListener(e -> {
            int userId = userService.getCurrentUserId();
            List<Movie> watchlist = watchlistDAO.getWatchlist(userId);
            if (watchlist.isEmpty()) {
                JOptionPane.showMessageDialog(accountView, "No watchlist items found.");
            } else {
                //WatchlistView watchlistView = new WatchlistView();
                String[] watchlistTitles = watchlist.stream().map(Movie::getTitle).toArray(String[]::new);
                //watchlistView.updateWatchlist(watchlistTitles);
                //watchlistView.setVisible(true);
            }
        });

        // When the Edit Details button is pressed, show a placeholder message.
        accountView.addEditDetailsActionListener(e ->
                JOptionPane.showMessageDialog(accountView, "Edit Details functionality coming soon!")
        );

        // When the Logout button is pressed, confirm logout and then close the account view.
        accountView.addLogoutActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(accountView, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                accountView.dispose();
                // Optionally, you can navigate to the login view here.
            }
        });
    }
}