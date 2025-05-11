package flickfinder.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RecommendationView extends JFrame {

    private final DefaultListModel<String> listModel;
    private final JList<String> recommendationList;

    public RecommendationView() {
        // Set up the frame properties
        setTitle("Movie Recommendations");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Add padding around the content
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Initialize the list model and JList for recommendations
        listModel = new DefaultListModel<>();
        recommendationList = new JList<>(listModel);
        recommendationList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        recommendationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recommendationList.setFixedCellHeight(30);

        JScrollPane scrollPane = new JScrollPane(recommendationList);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 0, 5, 0),
            BorderFactory.createLineBorder(new Color(200, 200, 200))
        ));

        // Add the scroll pane to the frame
        add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        //panel.setBackground(new Color(240, 240, 240));
        
        JLabel titleLabel = new JLabel("Your Movie Recommendations");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(5, 5, 15, 5));
        
        JLabel subtitleLabel = new JLabel("Based on your preferences and watch history");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        //subtitleLabel.setForeground(Color.GRAY);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        
        JButton saveButton = new JButton("Save to Favorites");
        saveButton.addActionListener(e -> {
            String selected = recommendationList.getSelectedValue();
            if (selected != null) {
                // Add logic to save selected recommendation to favorites
                JOptionPane.showMessageDialog(this, 
                    "Saved to favorites: " + selected, 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a movie first", 
                    "No Selection", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        //Style for buttons
        Font buttonFont = new Font("SansSerif", Font.BOLD, 12);
        
        closeButton.setFont(buttonFont);
        
        saveButton.setFont(buttonFont);
        
        panel.add(saveButton);
        panel.add(closeButton);
        
        return panel;
    }

    /**
     * Updates the view with the provided recommendations.
     * @param recommendations an array of recommended movie titles.
     */
    public void updateRecommendations(String[] recommendations) {
        listModel.clear();
        if (recommendations != null && recommendations.length > 0) {
            for (String rec : recommendations) {
                listModel.addElement(rec);
            }
        } else {
            listModel.addElement("No recommendations available");
            recommendationList.setEnabled(false);
        }
    }
}