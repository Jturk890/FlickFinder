package flickfinder;

import com.formdev.flatlaf.FlatDarkLaf;
import flickfinder.Controller.LoginController;
import flickfinder.Model.MovieService;
import flickfinder.Model.UserService;
import flickfinder.View.LoginView;
import flickfinder.View.SearchView;
import javax.swing.*;
import java.awt.Color;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "flickfinder")
public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Button.background", new Color(102, 51, 153));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Label.foreground", Color.WHITE);
            UIManager.put("TextField.background", new Color(48, 48, 48));
            UIManager.put("TextField.foreground", Color.WHITE);
            UIManager.put("Panel.background", new Color(32, 32, 32));
        } catch (Exception e) {
            System.out.println("FlatLaf not available, using default.");
        }
        SwingUtilities.invokeLater(() -> {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(App.class);
            LoginView loginView = new LoginView();
            SearchView searchView = new SearchView();
            MovieService movieService = context.getBean(MovieService.class);
            UserService userService = new UserService();
            new LoginController(loginView, searchView, movieService, userService);
            loginView.setVisible(true);
        });
    }
} 