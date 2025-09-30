package flickfinder.View;

import javax.swing.*;
import java.awt.*;

public class AccountView extends JFrame {
    private final DefaultListModel<String> favoritesModel;
    private final DefaultListModel<String> watchlistModel;
    private final JList<String> favoritesList;
    private final JList<String> watchlistList;
    private final JTextArea movieDetailsArea;
    private final JButton removeFromFavoritesButton;
    private final JButton removeFromWatchlistButton;
    private final JLabel posterLabel;
    private final JButton backButton;


    public AccountView() {
        setTitle("My Account");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Center: two lists side by side
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        favoritesModel = new DefaultListModel<>();
        favoritesList = new JList<>(favoritesModel);
        JPanel favPanel = new JPanel(new BorderLayout());
        favPanel.add(new JLabel("Favorites"), BorderLayout.NORTH);
        favPanel.add(new JScrollPane(favoritesList), BorderLayout.CENTER);
        removeFromFavoritesButton = new JButton("Remove from Favorites");
        removeFromFavoritesButton.setEnabled(false); // Initially disabled
        favPanel.add(removeFromFavoritesButton, BorderLayout.SOUTH);

        watchlistModel = new DefaultListModel<>();
        watchlistList = new JList<>(watchlistModel);
        JPanel watchPanel = new JPanel(new BorderLayout());
        watchPanel.add(new JLabel("Watchlist"), BorderLayout.NORTH);
        watchPanel.add(new JScrollPane(watchlistList), BorderLayout.CENTER);
        removeFromWatchlistButton = new JButton("Remove from Watchlist");
        removeFromWatchlistButton.setEnabled(false); // Initially disabled
        watchPanel.add(removeFromWatchlistButton, BorderLayout.SOUTH);

        listsPanel.add(favPanel);
        listsPanel.add(watchPanel);
        add(listsPanel, BorderLayout.CENTER);

        // Right: poster and details
        JPanel detailPanel = new JPanel(new BorderLayout());
        posterLabel = new JLabel();
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailPanel.add(posterLabel, BorderLayout.NORTH);

        movieDetailsArea = new JTextArea();
        movieDetailsArea.setEditable(false);
        movieDetailsArea.setLineWrap(true);
        movieDetailsArea.setWrapStyleWord(true);
        detailPanel.add(new JScrollPane(movieDetailsArea), BorderLayout.CENTER);
        detailPanel.setPreferredSize(new Dimension(250, 0));

        add(detailPanel, BorderLayout.EAST);

        // Bottom: Back to Search button
        backButton = new JButton("Back to Search");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void updateFavorites(String[] favorites) {
        favoritesModel.clear();
        for (String movie : favorites) {
            favoritesModel.addElement(movie);
        }
    }

    public void updateWatchlist(String[] watchlist) {
        watchlistModel.clear();
        for (String movie : watchlist) {
            watchlistModel.addElement(movie);
        }
    }

    public JList<String> getFavoritesList() {
        return favoritesList;
    }

    public JList<String> getWatchlistList() {
        return watchlistList;
    }

    public JButton getRemoveFromFavoritesButton() {
        return removeFromFavoritesButton;
    }

    public JButton getRemoveFromWatchlistButton() {
        return removeFromWatchlistButton;
    }

    public void showMovieDetails(String details) {
        movieDetailsArea.setText(details);
    }

    public void setPosterImage(ImageIcon icon) {
        posterLabel.setIcon(icon);
    }

    public JButton getBackButton() {
    return backButton;
    }

}
