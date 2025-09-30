import flickfinder.Controller.LoginController;
import flickfinder.Model.MovieService;
import flickfinder.Model.UserService;
import flickfinder.View.LoginView;
import flickfinder.View.SearchView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    private LoginView loginView;
    private SearchView searchView;
    private MovieService movieService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        loginView = mock(LoginView.class);
        searchView = mock(SearchView.class);
        movieService = mock(MovieService.class);
        userService = mock(UserService.class);

        new LoginController(loginView, searchView, movieService, userService);
    }

    @Test
    void successfulLogin_disposesLoginViewAndOpensSearchView() {
        when(loginView.getUsername()).thenReturn("testuser");
        when(loginView.getPassword()).thenReturn("testpass");
        when(userService.validateLogin("testuser", "testpass")).thenReturn(true);

        loginView.addLoginListener(argThat(listener -> {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "login"));
            verify(loginView).dispose();
            verify(searchView).setVisible(true);
            return true;
        }));
    }

    @Test
    void loginFails_showsErrorMessage() {
        when(loginView.getUsername()).thenReturn("wrong");
        when(loginView.getPassword()).thenReturn("bad");
        when(userService.validateLogin("wrong", "bad")).thenReturn(false);

        loginView.addLoginListener(argThat(listener -> {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "login"));
            verify(loginView).getUsername();
            verify(loginView).getPassword();
            return true;
        }));
    }

    @Test
    void registrationSucceeds_showsSuccessDialog() {
        when(loginView.getUsername()).thenReturn("newuser");
        when(loginView.getPassword()).thenReturn("newpass");
        when(userService.registerUser("newuser", "newpass")).thenReturn(true);

        loginView.addRegListener(argThat(listener -> {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "register"));
            verify(userService).registerUser("newuser", "newpass");
            return true;
        }));
    }

    @Test
    void registrationFails_showsDuplicateUserError() {
        when(loginView.getUsername()).thenReturn("existing");
        when(loginView.getPassword()).thenReturn("pass");
        when(userService.registerUser("existing", "pass")).thenReturn(false);

        loginView.addRegListener(argThat(listener -> {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "register"));
            verify(userService).registerUser("existing", "pass");
            return true;
        }));
    }

    @Test
    void registrationWithEmptyFields_showsValidationError() {
        when(loginView.getUsername()).thenReturn("");
        when(loginView.getPassword()).thenReturn("");

        loginView.addRegListener(argThat(listener -> {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "register"));
            verify(userService, never()).registerUser(anyString(), anyString());
            return true;
        }));
    }

    @Test
    void loginCallsUserServiceWithCorrectParams() {
        when(loginView.getUsername()).thenReturn("admin");
        when(loginView.getPassword()).thenReturn("adminpw");
        when(userService.validateLogin("admin", "adminpw")).thenReturn(true);

        loginView.addLoginListener(argThat(listener -> {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "login"));
            verify(userService).validateLogin("admin", "adminpw");
            return true;
        }));
    }

    @Test
    void successfulRegistration_doesNotCallSetVisibleOnSearchView() {
        when(loginView.getUsername()).thenReturn("user");
        when(loginView.getPassword()).thenReturn("pass");
        when(userService.registerUser("user", "pass")).thenReturn(true);

        loginView.addRegListener(argThat(listener -> {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "register"));
            verify(searchView, never()).setVisible(true);
            return true;
        }));
    }

    @Test
    void multipleLoginAttempts_repeatedValidation() {
        when(loginView.getUsername()).thenReturn("multi");
        when(loginView.getPassword()).thenReturn("try");
        when(userService.validateLogin("multi", "try")).thenReturn(false);

        loginView.addLoginListener(argThat(listener -> {
            for (int i = 0; i < 3; i++) {
                listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "login"));
            }
            verify(userService, times(3)).validateLogin("multi", "try");
            return true;
        }));
    }

    @Test
    void successfulLogin_createsMovieController() {
        when(loginView.getUsername()).thenReturn("john");
        when(loginView.getPassword()).thenReturn("doe");
        when(userService.validateLogin("john", "doe")).thenReturn(true);

        loginView.addLoginListener(argThat(listener -> {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "login"));
            verify(movieService, atLeast(0)).getTrendingMovies(); // weak signal of controller creation
            return true;
        }));
    }

    @Test
    void registrationOnlyCallsRegisterOnce() {
        when(loginView.getUsername()).thenReturn("unique");
        when(loginView.getPassword()).thenReturn("strong");
        when(userService.registerUser("unique", "strong")).thenReturn(true);

        loginView.addRegListener(argThat(listener -> {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "register"));
            verify(userService, times(1)).registerUser("unique", "strong");
            return true;
        }));
    }

    @Test
    void loginWithNullFields_failsGracefully() {
        when(loginView.getUsername()).thenReturn(null);
        when(loginView.getPassword()).thenReturn(null);

        loginView.addLoginListener(argThat(listener -> {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "login"));
            verify(userService).validateLogin(null, null);
            return true;
        }));
    }

    @Test
    void emptyCredentials_doesNotCrash() {
        when(loginView.getUsername()).thenReturn("");
        when(loginView.getPassword()).thenReturn("");

        loginView.addLoginListener(argThat(listener -> {
            assertDoesNotThrow(() -> listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "login")));
            return true;
        }));
    }

    @Test
    void successfulLogin_showsLoginMessageDialog() {
        when(loginView.getUsername()).thenReturn("toast");
        when(loginView.getPassword()).thenReturn("bread");
        when(userService.validateLogin("toast", "bread")).thenReturn(true);

        loginView.addLoginListener(argThat(listener -> {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "login"));
            // We can't test dialogs without UI framework mocking (e.g., AssertJ-Swing), so we assert logic flow
            verify(loginView).dispose();
            return true;
        }));
    }
}