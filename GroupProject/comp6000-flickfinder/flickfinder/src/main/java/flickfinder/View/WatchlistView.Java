package flickfinder.View;

import javax.swing.*;
import java.awt.*;

public class WatchlistView extends JFrame {
    private final DefaultListModel<String> listModel;
    private final JList<String> watchlist;

    public WatchlistView() {
        setTitle("My Watchlist");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        watchlist = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(watchlist);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateWatchlist(String[] movies) {
        listModel.clear();
        for (String movie : movies) {
            listModel.addElement(movie);
        }
    }
}