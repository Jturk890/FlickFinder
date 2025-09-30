package flickfinder.View;

import flickfinder.Model.Movie;
import flickfinder.Model.MovieService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RecommendationView with poster thumbnails (async-loaded & cached).
 */
public class RecommendationView extends JFrame {

    private static final String IMG_BASE = "https://image.tmdb.org/t/p/w92"; // small thumbs (~92px wide)
    private static final int    THUMB_W  = 62;  // final scaled width
    private static final int    THUMB_H  = 92;  // final scaled height

    private final DefaultListModel<Movie> listModel;
    private final JList<Movie> recommendationList;
    private final MovieService movieService;
    private final JProgressBar loadingBar;

    // cache for posters to avoid re-downloading
    private final Map<Integer, ImageIcon> posterCache = new ConcurrentHashMap<>();
    private final ImageIcon placeholder = createPlaceholder();

    public RecommendationView(MovieService movieService, Set<String> preferredGenres) {
        this.movieService = movieService;

        setTitle("Movie Recommendations");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        // ---------- Header ----------
        add(createHeaderPanel(preferredGenres), BorderLayout.NORTH);

        // ---------- Center list ----------
        listModel = new DefaultListModel<>();
        recommendationList = new JList<>(listModel);
        recommendationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recommendationList.setFixedCellHeight(-1); // allow variable height
        recommendationList.setVisibleRowCount(-1);
        recommendationList.setCellRenderer(new MovieCellRenderer());

        JScrollPane scrollPane = new JScrollPane(recommendationList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // ---------- Bottom buttons ----------
        add(createButtonPanel(), BorderLayout.SOUTH);

        // ---------- Loading bar ----------
        loadingBar = new JProgressBar();
        loadingBar.setIndeterminate(true);
        loadingBar.setVisible(false);
        add(loadingBar, BorderLayout.WEST);

        pack(); // size to preferred
        setSize(getWidth(), 480); // set a nice height
        setLocationRelativeTo(null);

        loadRecommendations(preferredGenres);
    }

    // Header: title + subtitle (genres)
    private JPanel createHeaderPanel(Set<String> genres) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Your Movie Recommendations");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String subtitleText = genres.isEmpty()
                ? "No genres found — results may be generic"
                : "Based on your genres: " + String.join(", ", genres);
        JLabel subtitleLabel = new JLabel(subtitleText);
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, 12f));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        subtitleLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(subtitleLabel);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setOpaque(false);

        JButton saveButton = new JButton("Save to Favorites");
        JButton closeButton = new JButton("Close");

        saveButton.addActionListener(e -> {
            Movie selectedMovie = recommendationList.getSelectedValue();
            if (selectedMovie != null && selectedMovie.getId() != 0) {
                JOptionPane.showMessageDialog(this,
                        "Saved to favorites: " + selectedMovie.getTitle(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a valid movie.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        closeButton.addActionListener(e -> dispose());

        panel.add(saveButton);
        panel.add(closeButton);
        return panel;
    }

    private void loadRecommendations(Set<String> genres) {
        loadingBar.setVisible(true);
        recommendationList.setEnabled(false);

        SwingWorker<List<Movie>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Movie> doInBackground() {
                return movieService.getRecommendations(genres);
            }

            @Override
            protected void done() {
                loadingBar.setVisible(false);
                recommendationList.setEnabled(true);
                try {
                    List<Movie> recommendations = get();
                    updateRecommendations(recommendations);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RecommendationView.this,
                            "Failed to load recommendations.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateRecommendations(List<Movie> recommendations) {
        listModel.clear();
        if (recommendations != null && !recommendations.isEmpty()) {
            recommendations.forEach(listModel::addElement);
            loadPostersAsync(recommendations);
        } else {
            listModel.addElement(new Movie(0, "No recommendations available", "", "", "", 0.0, 0.0, "", List.of()));
            recommendationList.setEnabled(false);
        }
    }

    // Async poster loading to avoid blocking EDT
    private void loadPostersAsync(List<Movie> movies) {
        new SwingWorker<Void, PosterPair>() {
            @Override
            protected Void doInBackground() {
                for (Movie m : movies) {
                    if (m.getPosterPath() == null || "N/A".equals(m.getPosterPath())) continue;
                    if (posterCache.containsKey(m.getId())) continue;
                    try {
                        ImageIcon icon = new ImageIcon(new URL(IMG_BASE + m.getPosterPath()));
                        Image scaled = icon.getImage().getScaledInstance(THUMB_W, THUMB_H, Image.SCALE_SMOOTH);
                        publish(new PosterPair(m.getId(), new ImageIcon(scaled)));
                    } catch (Exception ignored) { }
                }
                return null;
            }

            @Override
            protected void process(List<PosterPair> chunks) {
                for (PosterPair p : chunks) {
                    posterCache.put(p.movieId, p.icon);
                }
                recommendationList.repaint();
            }
        }.execute();
    }

    private ImageIcon createPlaceholder() {
        BufferedImage img = new BufferedImage(THUMB_W, THUMB_H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(220, 220, 220));
        g.fillRect(0, 0, THUMB_W, THUMB_H);
        g.setColor(Color.LIGHT_GRAY.darker());
        g.drawRect(0, 0, THUMB_W - 1, THUMB_H - 1);
        g.dispose();
        return new ImageIcon(img);
    }

    private static class PosterPair {
        final int movieId;
        final ImageIcon icon;
        PosterPair(int movieId, ImageIcon icon) { this.movieId = movieId; this.icon = icon; }
    }

    // -------- Renderer with poster thumbnail --------
    private class MovieCellRenderer extends JPanel implements ListCellRenderer<Movie> {

        private final JLabel posterLabel = new JLabel();
        private final JLabel titleLabel = new JLabel();
        private final JLabel metaLabel = new JLabel();
        private final JLabel overviewLabel = new JLabel();

        public MovieCellRenderer() {
            setLayout(new BorderLayout(8, 8));
            setBorder(new EmptyBorder(8, 8, 8, 8));
            setOpaque(true);

            // text panel
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 15f));
            metaLabel.setFont(metaLabel.getFont().deriveFont(Font.PLAIN, 12f));
            metaLabel.setForeground(new Color(100, 100, 100));

            overviewLabel.setFont(overviewLabel.getFont().deriveFont(Font.PLAIN, 12f));
            overviewLabel.setForeground(new Color(80, 80, 80));
            overviewLabel.setVerticalAlignment(SwingConstants.TOP);

            textPanel.add(titleLabel);
            textPanel.add(Box.createVerticalStrut(2));
            textPanel.add(metaLabel);
            textPanel.add(Box.createVerticalStrut(4));
            textPanel.add(overviewLabel);

            posterLabel.setPreferredSize(new Dimension(THUMB_W, THUMB_H));

            add(posterLabel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Movie> list, Movie movie,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            if (movie == null || movie.getId() == 0) {
                titleLabel.setText("No recommendations available");
                metaLabel.setText("");
                overviewLabel.setText("");
                posterLabel.setIcon(placeholder);
            } else {
                titleLabel.setText(movie.getTitle());
                metaLabel.setText(String.format("%s  •  ⭐ %.1f", movie.getReleaseDate(), movie.getRating()));

                String ov = movie.getOverview();
                if (ov != null && ov.length() > 140) {
                    ov = ov.substring(0, 140).trim() + "...";
                }
                overviewLabel.setText("<html><body style='width:300px;'>" + (ov == null ? "" : ov) + "</body></html>");

                ImageIcon icon = posterCache.get(movie.getId());
                posterLabel.setIcon(icon != null ? icon : placeholder);
            }

            // Selection styling
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                titleLabel.setForeground(list.getSelectionForeground());
                metaLabel.setForeground(list.getSelectionForeground());
                overviewLabel.setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                titleLabel.setForeground(list.getForeground());
                metaLabel.setForeground(new Color(100, 100, 100));
                overviewLabel.setForeground(new Color(80, 80, 80));
            }

            return this;
        }
    }
}