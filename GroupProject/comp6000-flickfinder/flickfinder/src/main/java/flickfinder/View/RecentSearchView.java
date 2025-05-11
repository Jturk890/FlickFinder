package flickfinder.View;

import javax.swing.*;
import java.awt.*;

public class RecentSearchView extends JFrame {
    private final DefaultListModel<String> listModel;
    private final JList<String> recentSearchList;

    public RecentSearchView() {
        setTitle("Recent Searches");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only disposes this window
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        recentSearchList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(recentSearchList);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateRecentSearches(String[] searches) {
        listModel.clear();
        for (String search : searches) {
            listModel.addElement(search);
        }
    }
}