package flickfinder.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FavoritesView extends JFrame {
    private final JList<String> favoritesList;
    private final DefaultListModel<String> listModel;
    private final JButton removeFavoriteButton;

    public FavoritesView() {
        setTitle("My Favorites");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Closes just the Favorites window
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        favoritesList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(favoritesList);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for Remove button
        JPanel buttonPanel = new JPanel();
        removeFavoriteButton = new JButton("Remove Selected");
        buttonPanel.add(removeFavoriteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateFavoritesList(String[] favorites) {
        listModel.clear();
        for (String favorite : favorites) {
            listModel.addElement(favorite);
        }
    }

    public void addRemoveFavoriteListener(ActionListener listener) {
        removeFavoriteButton.addActionListener(listener);
    }

    public String getSelectedFavorite() {
        return favoritesList.getSelectedValue();
    }
}