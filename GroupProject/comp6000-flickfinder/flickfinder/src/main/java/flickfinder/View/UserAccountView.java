package flickfinder.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UserAccountView extends JFrame {
    private final JLabel lblUsername;
    private final JButton btnFavorites;
    private final JButton btnWatchlist;
    private final JButton btnEditDetails;
    private final JButton btnLogout;

    public UserAccountView(String username) {
        setTitle("User Account");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Top panel: display user details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        lblUsername = new JLabel("Username: " + username);
        lblUsername.setFont(new Font("Arial", Font.BOLD, 16));
        detailsPanel.add(lblUsername);
        // Add additional user detail components here if needed

        // Bottom panel: account action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnFavorites = new JButton("Favorites");
        btnWatchlist = new JButton("Watchlist");
        btnEditDetails = new JButton("Edit Details");
        btnLogout = new JButton("Logout");
        buttonPanel.add(btnFavorites);
        buttonPanel.add(btnWatchlist);
        buttonPanel.add(btnEditDetails);
        buttonPanel.add(btnLogout);

        add(detailsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void addFavoritesActionListener(ActionListener listener) {
        btnFavorites.addActionListener(listener);
    }

    public void addWatchlistActionListener(ActionListener listener) {
        btnWatchlist.addActionListener(listener);
    }

    public void addEditDetailsActionListener(ActionListener listener) {
        btnEditDetails.addActionListener(listener);
    }

    public void addLogoutActionListener(ActionListener listener) {
        btnLogout.addActionListener(listener);
    }
}