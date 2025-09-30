package flickfinder.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SearchView extends JFrame {
    private final JTextField searchField;
    private final JButton searchButton;
    private final JButton trendingButton;
    private final JButton logoutButton;
    private final JButton recommendationsButton;
    private final JButton accountButton;  // Account button added here
    private final JButton addToFavoritesButton;
    private final JButton addToWatchlistButton;
    private final JList<String> movieList;
    private final DefaultListModel<String> listModel;
    private final JTextArea movieDetailsArea;
    private final JButton genresButton;

    public SearchView() {
        setTitle("Movie Search");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Area: Search field and top right panel with buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        topPanel.add(searchField, BorderLayout.CENTER);

        // Top Right Panel now only contains Account, Recommendations, and Logout buttons.
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        accountButton = new JButton("Account");      // New Account button
        recommendationsButton = new JButton("Recommendations");
        logoutButton = new JButton("Logout");
        topRightPanel.add(accountButton);
        topRightPanel.add(recommendationsButton);
        topRightPanel.add(logoutButton);
        topPanel.add(topRightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center Area: Buttons and Movie List
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        searchButton = new JButton("Search");
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(searchButton, gbc);

        trendingButton = new JButton("Trending");
        gbc.gridx = 1;
        gbc.gridy = 0;
        centerPanel.add(trendingButton, gbc);

        listModel = new DefaultListModel<>();
        movieList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(movieList);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        centerPanel.add(listScrollPane, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Area: Movie Details and buttons for "Add to Favorites" and "Add to Watchlist"
        JPanel bottomPanel = new JPanel(new BorderLayout());
        movieDetailsArea = new JTextArea(5, 30);
        movieDetailsArea.setEditable(false);
        movieDetailsArea.setLineWrap(true);
        movieDetailsArea.setWrapStyleWord(true);
        JScrollPane detailsScrollPane = new JScrollPane(movieDetailsArea);
        bottomPanel.add(detailsScrollPane, BorderLayout.CENTER);

        JPanel bottomButtonsPanel = new JPanel(new FlowLayout());
        addToFavoritesButton = new JButton("Add to Favorites");
        addToWatchlistButton = new JButton("Add to Watchlist");
        genresButton = new JButton("Genres");
        bottomButtonsPanel.add(addToFavoritesButton);
        bottomButtonsPanel.add(addToWatchlistButton);
        bottomButtonsPanel.add(genresButton);
        bottomPanel.add(bottomButtonsPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.EAST);
    }

    // Getter for search query
    public String getQuery() {
        return searchField.getText().trim();
    }

    // Methods to attach listeners
    public void addSearchActionListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void addTrendingActionListener(ActionListener listener) {
        trendingButton.addActionListener(listener);
    }

    public void addMovieSelectionListener(ActionListener listener) {
        movieList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && movieList.getSelectedValue() != null) {
                listener.actionPerformed(new java.awt.event.ActionEvent(this, java.awt.event.ActionEvent.ACTION_PERFORMED, null));
            }
        });
    }

    public void updateMovieList(String[] movies) {
        listModel.clear();
        for (String movie : movies) {
            listModel.addElement(movie);
        }
    }

    public String getSelectedMovie() {
        return movieList.getSelectedValue();
    }

    public void showMovieDetails(String details) {
        movieDetailsArea.setText(details);
    }

    // Listener attach methods for bottom buttons
    public void addAddToFavoritesListener(ActionListener listener) {
        addToFavoritesButton.addActionListener(listener);
    }

    public void addAddToWatchlistListener(ActionListener listener) {
        addToWatchlistButton.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    // Listener attach method for Recommendations button
    public void addRecommendationsListener(ActionListener listener) {
        recommendationsButton.addActionListener(listener);
    }

    // New listener attach method for Account button
    public void addAccountActionListener(ActionListener listener) {
        accountButton.addActionListener(listener);
    }

    public void addGenresActionListener(ActionListener listener) {
        genresButton.addActionListener(listener);
    }

    // Optional: Add method to display genres
    public void showGenresList(String[] genres) {
        JOptionPane.showMessageDialog(this, String.join("\n", genres), "Available Genres", JOptionPane.INFORMATION_MESSAGE);
    }
}