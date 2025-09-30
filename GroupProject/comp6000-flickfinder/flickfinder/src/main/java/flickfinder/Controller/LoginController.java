package flickfinder.Controller;

import flickfinder.Model.MovieService;
import flickfinder.Model.UserService;
import flickfinder.View.LoginView;
import flickfinder.View.SearchView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private final LoginView loginView;
    private final SearchView searchView;
    private final MovieService movieService;
    private final UserService userService;

    public LoginController(LoginView loginView, SearchView searchView, MovieService movieService, UserService userService) {
        this.loginView = loginView;
        this.searchView = searchView;
        this.movieService = movieService;
        this.userService = userService;

        this.loginView.addLoginListener(new LoginAction());
        this.loginView.addRegListener(new RegisterAction());

        loginView.setVisible(true);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            if (userService.validateLogin(username, password)) {
                JOptionPane.showMessageDialog(loginView, "Login Successful!");
                // Dispose of the login window so it no longer appears
                loginView.dispose();
                // Show the movie page
                searchView.setVisible(true);
                // Create MovieController with searchView and also pass userService if needed by MovieController for logout re-login
                new MovieController(movieService, searchView, userService);
            } else {
                JOptionPane.showMessageDialog(loginView, "Invalid username or password. Please register first.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginView, "Username and password cannot be empty!",
                        "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userService.registerUser(username, password)) {
                JOptionPane.showMessageDialog(loginView, "Registration successful! You can now log in.");
            } else {
                JOptionPane.showMessageDialog(loginView, "Username already exists. Try a different one.",
                        "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}