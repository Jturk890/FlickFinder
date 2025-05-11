import org.junit.jupiter.api.Test; 
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
 
import flickfinder.DAO.UserDAO;
import flickfinder.Model.LoginModel;

/** 
 * Unit tests for the {@link LoginModel} class.
 * This class contains test cases for the authentication and registration 
 * functionality using mocked {@link UserDAO} objects.
 * @author Joshua Turkson
 */
class LoginModelTest {

    /**
     * Tests the successful authentication of a user.
     * The mock {@link UserDAO} is set up to return {@code true} when the valid credentials
     * are provided to the authenticate method.
     */
    @Test
    void testAuthenticateSuccess() {
        // Arrange: Create a mock UserDao
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);

        // Define behavior for the mock: authentication succeeds
        when(mockUserDao.authenticate("validUser", "validPass")).thenReturn(true);

        // Create the LoginModel and inject the mock
        LoginModel model = new LoginModel(mockUserDao);

        // Act: Test the authenticate method
        boolean result = model.authenticate("validUser", "validPass");

        // Assert: Manually throw an exception if the result is not true
        if (!result) {
            throw new AssertionError("Authentication failed for valid user and password.");
        }
    }

    /**
     * Tests the failure of user authentication with invalid credentials.
     * The mock {@link UserDAO} is set up to return {@code false} when invalid credentials
     * are provided to the authenticate method.
     */
    @Test
    void testAuthenticateFailure() {
        // Arrange: Create a mock UserDao
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);

        // Define behavior for the mock: authentication fails
        when(mockUserDao.authenticate("invalidUser", "wrongPass")).thenReturn(false);

        // Create the LoginModel and inject the mock
        LoginModel model = new LoginModel(mockUserDao);

        // Act: Test the authenticate method
        boolean result = model.authenticate("invalidUser", "wrongPass");

        // Assert: Verify the result
        if (result) {
            throw new RuntimeException("Authentication should fail for invalid credentials");
        }
    }

    /**
    * Tests the authentication method with empty username and password inputs.
    * Ensures that authentication fails when no credentials are provided.
    */
    @Test
    void testAuthenticateWithEmptyInput() {
        // Arrange: Create a mock UserDAO and the LoginModel
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);
        LoginModel model = new LoginModel(mockUserDao);

        // Act: Attempt to authenticate with empty username and password
        boolean result = model.authenticate("", "");

        // Assert: Verify that authentication fails for empty inputs
        assertFalse(result, "Authentication should fail with empty inputs");
    }

    /**
    * Tests the authentication method for case sensitivity.
    * Ensures that authentication fails when username and/or password do not match the exact case.
    */
    @Test
    void testAuthenticateCaseSensitivity() {
        // Arrange: Create a mock UserDAO and define behavior for case-sensitive credentials
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);
        when(mockUserDao.authenticate("User", "Pass")).thenReturn(false); // Mock case-sensitive behavior

        // Create the LoginModel and inject the mocked UserDAO
        LoginModel model = new LoginModel(mockUserDao);

        // Act: Attempt to authenticate with case-mismatched credentials
        boolean result = model.authenticate("user", "pass");

        // Assert: Verify that authentication fails due to case mismatch
        assertFalse(result, "Authentication should fail for case-mismatched credentials");
    }

    /**
    * Tests the authentication method against SQL injection attempts.
    * Ensures that authentication fails when provided with potentially malicious inputs.
    */
    @Test
    void testAuthenticateWithSqlInjection() {
        // Arrange: Create a mock UserDAO and the LoginModel
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);
        LoginModel model = new LoginModel(mockUserDao);

        // Act: Attempt to authenticate with an SQL injection string as the username
        boolean result = model.authenticate("'; DROP TABLE Users; --", "password");

        // Assert: Verify that authentication fails with SQL injection attempts
        assertFalse(result, "Authentication should fail with SQL injection attempts");
    }

    /**
    * Tests that the authenticate method in the UserDAO is called exactly once
    * with the correct parameters when the LoginModel's authenticate method is invoked.
    */
    @Test
    void testAuthenticateMethodCall() {
        // Arrange: Create a mock UserDAO and the LoginModel
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);
        LoginModel model = new LoginModel(mockUserDao);

        // Act: Call the authenticate method on the LoginModel
        model.authenticate("user", "password");

        // Assert: Verify that the UserDAO's authenticate method was called once with the correct arguments
        verify(mockUserDao, times(1)).authenticate("user", "password");
    }

    /**
     * Tests the successful registration of a new user.
     * The mock {@link UserDAO} is set up to return {@code true} when a new user is registered.
     */
    @Test
    void registerUserSuccess() {
        // Arrange: Create a mock UserDAO
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);

        // Define behavior for the mock: registration succeeds
        when(mockUserDao.registerUser("newUser", "securePass")).thenReturn(true);

        // Create the LoginModel and inject the mock
        LoginModel model = new LoginModel(mockUserDao);

        // Act: Test the registerUser method
        boolean result = model.registerUser("newUser", "securePass");

        // Assert: Verify the result
        assertTrue(result, "The user should be successfully registered");
    }

    /**
     * Tests the failure of user registration when the username already exists.
     * The mock {@link UserDAO} is set up to return {@code false} when an existing username is used.
     */
    @Test
    void registerUserFailure() {
        // Arrange: Create a mock UserDAO
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);

        // Define behavior for the mock: registration fails
        when(mockUserDao.registerUser("existingUser", "securePass")).thenReturn(false);

        // Create the LoginModel and inject the mock
        LoginModel model = new LoginModel(mockUserDao);

        // Act: Test the registerUser method
        boolean result = model.registerUser("existingUser", "securePass");

        // Assert: Verify the result
        assertFalse(result, "Registration should fail for an already existing username");
    }

    /**
    * Tests the registration method with empty username and password inputs.
    * Ensures that the registration process fails when provided with invalid data.
    */
    @Test
    void testRegisterWithEmptyInput() {
        // Arrange: Create a mock UserDAO and the LoginModel
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);
        LoginModel model = new LoginModel(mockUserDao);

        // Act: Attempt to register a user with empty username and password
        boolean result = model.registerUser("", "");

        // Assert: Verify that the registration fails
        assertFalse(result, "Registration should fail with empty username and password");
    }

   /**
    * Tests the registration process with a duplicate username.
    * Ensures that registration fails when attempting to register a username
    * that already exists in the system.
    */
    @Test
    void testRegisterDuplicateUser() {
        // Arrange: Create a mock UserDAO and define behavior for duplicate username registration
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);
        when(mockUserDao.registerUser("duplicateUser", "password")).thenReturn(false);

        // Create the LoginModel and inject the mocked UserDAO
        LoginModel model = new LoginModel(mockUserDao);

        // Act: Attempt to register a user with an already existing username
        boolean result = model.registerUser("duplicateUser", "password");

        // Assert: Verify that the registration fails for a duplicate username
        assertFalse(result, "Registration should fail for duplicate usernames");
    }

    /**
    * Tests the registration process with a weak password.
    * Ensures that registration fails when a password that does not meet security requirements
    * (e.g., too short) is provided.
    */
    @Test
    void testRegisterWithWeakPassword() {
        // Arrange: Create a mock UserDAO and the LoginModel
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);
        LoginModel model = new LoginModel(mockUserDao);

        // Act: Attempt to register a user with a weak password
        boolean result = model.registerUser("newUser", "123");

        // Assert: Verify that the registration fails with a weak password
        assertFalse(result, "Registration should fail with a weak password");
    }

    /**
    * Tests that the registerUser method in the UserDAO is called exactly once
    * with the correct parameters when the LoginModel's registerUser method is invoked.
    */
    @Test
    void testRegisterMethodCall() {
        // Arrange: Create a mock UserDAO and the LoginModel
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);
        LoginModel model = new LoginModel(mockUserDao);

        // Act: Call the registerUser method on the LoginModel
        model.registerUser("user", "password");

        // Assert: Verify that the UserDAO's registerUser method was called once with the correct arguments
        verify(mockUserDao, times(1)).registerUser("user", "password");
    }

    /**
    * This test simulates concurrent registration requests for the same username
    * to verify that the system handles them correctly. It checks that the UserDAO's
    * registerUser method is called exactly twice, once for each thread, ensuring
    * proper interaction in a concurrent environment.
    */
    @Test
    void testConcurrentRegistration() throws InterruptedException {
        // Arrange: Create a mock UserDAO and define behavior for successful registration
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);
        when(mockUserDao.registerUser("newUser", "password")).thenReturn(true);

        // Create the LoginModel and inject the mocked UserDAO
        LoginModel model = new LoginModel(mockUserDao);

        // Create a task to register the same user, to be executed by multiple threads
        Runnable task = () -> model.registerUser("newUser", "password");

        // Create two threads to execute the task concurrently
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        // Act: Start both threads
        thread1.start();
        thread2.start();

        // Wait for both threads to finish execution
        thread1.join();
        thread2.join();

        // Assert: Verify that the UserDAO's registerUser method was called twice
        verify(mockUserDao, times(2)).registerUser("newUser", "password");
    }

    /**
    * This test ensures that the LoginModel properly propagates an exception thrown
    * by the UserDAO during the authentication process (e.g., due to a database error).
    * It validates that the method does not suppress the exception, allowing it to
    * be handled at a higher level in the application.
    */
    @Test
    void testAuthenticateWithDaoException() {
        // Arrange: Create a mock UserDAO and define behavior to throw a RuntimeException
        UserDAO mockUserDao = Mockito.mock(UserDAO.class);
        when(mockUserDao.authenticate(anyString(), anyString()))
            .thenThrow(new RuntimeException("Database error")); // Simulate a database error

        // Create the LoginModel and inject the mocked UserDAO
        LoginModel model = new LoginModel(mockUserDao);

        // Act & Assert: Verify that a RuntimeException is thrown when authenticate is called
        assertThrows(RuntimeException.class, () -> model.authenticate("user", "password"));
    }

}