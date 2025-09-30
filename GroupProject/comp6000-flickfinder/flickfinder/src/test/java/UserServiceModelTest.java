import flickfinder.Model.UserService;
    
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
    
import static org.junit.jupiter.api.Assertions.*;
    
/**
* Unit tests for {@link UserService}.
* NOTE: Tests touch the real users.txt file. We back it up and restore after each run.
*/
class UserServiceModelTest {
    
    private static final Path DATA_FILE   = Paths.get("users.txt");
    private static final Path BACKUP_FILE = Paths.get("users_backup_test.tmp");
    
    private UserService service;
    
    @BeforeEach
    void setUp() throws IOException {
        // Backup if users.txt already exists
        if (Files.exists(DATA_FILE)) {
            Files.move(DATA_FILE, BACKUP_FILE, StandardCopyOption.REPLACE_EXISTING);
        }
        // Ensure clean slate
        Files.deleteIfExists(DATA_FILE);
    
        service = new UserService(); // will call loadUsers()
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up test file
        Files.deleteIfExists(DATA_FILE);
        // Restore backup
        if (Files.exists(BACKUP_FILE)) {
            Files.move(BACKUP_FILE, DATA_FILE, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // 1. New registration succeeds & persists
    @Test
    void registerUser_newUser_persistsAndReturnsTrue() throws IOException {
        assertTrue(service.registerUser("alice", "pw1"));
        assertTrue(Files.exists(DATA_FILE));
        assertTrue(Files.readString(DATA_FILE).contains("alice:pw1"));
    }

    // 2. Duplicate registration fails, original remains
    @Test
    void registerUser_duplicate_returnsFalse_noOverwrite() throws IOException {
        service.registerUser("bob", "first");
        assertFalse(service.registerUser("bob", "second"));
        String file = Files.readString(DATA_FILE);
        assertTrue(file.contains("bob:first"));
        assertFalse(file.contains("bob:second"));
    }

    // 3. Valid login returns true and sets current user
    @Test
    void validateLogin_correctCredentials_setsCurrentUser() {
        service.registerUser("carol", "secret");
        assertTrue(service.validateLogin("carol", "secret"));
        assertEquals("carol", service.getCurrentUsername());
    }

    // 4. Wrong password returns false, no current user
    @Test
    void validateLogin_wrongPassword_false_noCurrentUser() {
        service.registerUser("dave", "pw");
        assertFalse(service.validateLogin("dave", "nope"));
        assertThrows(IllegalStateException.class, service::getCurrentUsername);
    }

    // 5. Non-existent user returns false
    @Test
    void validateLogin_userNotFound_false() {
        assertFalse(service.validateLogin("ghost", "pw"));
    }

    // 6. getCurrentUsername without login throws
    @Test
    void getCurrentUsername_notLoggedIn_throws() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, service::getCurrentUsername);
        assertTrue(ex.getMessage().contains("No user"));
    }

    // 7. getCurrentUserId without login throws
    @Test
    void getCurrentUserId_notLoggedIn_throws() {
        assertThrows(IllegalStateException.class, service::getCurrentUserId);
    }

    // 8. getCurrentUserId equals hashCode of username
    @Test
    void getCurrentUserId_matchesHashCode() {
        service.registerUser("erin", "pw");
        service.validateLogin("erin", "pw");
        assertEquals("erin".hashCode(), service.getCurrentUserId());
    }

    // 9. loadUsers reads existing persisted users
    @Test
    void loadUsers_readsFromFile() throws IOException {
        Files.writeString(DATA_FILE, "frank:123\njane:abc\n");
        UserService reload = new UserService();
        assertTrue(reload.validateLogin("frank", "123"));
        assertTrue(reload.validateLogin("jane", "abc"));
    }

    // 10. saveUsers writes all entries
    @Test
    void saveUsers_writesAllUsers() throws IOException {
        service.registerUser("g1", "p1");
        service.registerUser("g2", "p2");
        List<String> lines = Files.readAllLines(DATA_FILE);
        assertEquals(2, lines.size());
        assertTrue(lines.contains("g1:p1"));
        assertTrue(lines.contains("g2:p2"));
    }

    // 11. Null password registration: current code allows it; login should fail (and not crash)
    @Test
    void registerUser_nullPassword_allowedButLoginFails() {
        assertTrue(service.registerUser("nullpw", null));
        assertDoesNotThrow(() -> service.validateLogin("nullpw", null));
        assertFalse(service.validateLogin("nullpw", null));
    }

    // 12. Logging in again switches current user
    @Test
    void validateLogin_secondUser_overwritesCurrentUser() {
        service.registerUser("i1", "a");
        service.registerUser("i2", "b");
        service.validateLogin("i1", "a");
        service.validateLogin("i2", "b");
        assertEquals("i2", service.getCurrentUsername());
    }

    // 13. Persistence across new instance
    @Test
    void persistence_restart_keepsUsers() {
        service.registerUser("kate", "pw");
        UserService reload = new UserService();
        assertTrue(reload.validateLogin("kate", "pw"));
    }

    // 14. Malformed lines are ignored (no crash, no load)
    @Test
    void loadUsers_ignoresMalformedLines() throws IOException {
        Files.writeString(DATA_FILE, "good:line\nmalformed\n:bad\nname:pass\n");
        UserService reload = new UserService();
        assertTrue(reload.validateLogin("good", "line"));
        assertTrue(reload.validateLogin("name", "pass"));
        assertFalse(reload.validateLogin("malformed", ""));
    }

    // 15. Many users persist and reload successfully
    @Test
    void registerManyUsers_allReloadable() {
        for (int i = 0; i < 30; i++) {
            assertTrue(service.registerUser("user" + i, "pw" + i));
        }
        UserService reload = new UserService();
        for (int i = 0; i < 30; i++) {
            assertTrue(reload.validateLogin("user" + i, "pw" + i));
        }
    }

    // 16. Empty username is accepted & persisted (documents current behavior)
    @Test
    void registerUser_emptyUsername_allowedAndLoginWorks() throws IOException {
        assertTrue(service.registerUser("", "blankpw"));
        assertTrue(service.validateLogin("", "blankpw"));
        assertEquals("", service.getCurrentUsername());
        assertTrue(Files.readString(DATA_FILE).contains(":blankpw")); // starts with colon
    }

    // 17. Null username login returns false and does not throw
    @Test
    void validateLogin_nullUsername_false_noException() {
        assertDoesNotThrow(() -> assertFalse(service.validateLogin(null, "pw")));
        assertThrows(IllegalStateException.class, service::getCurrentUsername);
    }

    // 18. Current user does NOT persist across reloads
    @Test
    void currentUser_notPersisted_afterRestart_gettersThrow() {
        service.registerUser("persistUser", "pw");
        service.validateLogin("persistUser", "pw");
        UserService reloaded = new UserService();
        assertThrows(IllegalStateException.class, reloaded::getCurrentUsername);
        assertThrows(IllegalStateException.class, reloaded::getCurrentUserId);
    }

    // 19. Lines with extra colons are ignored (only first two parts are accepted)
    @Test
    void loadUsers_lineWithExtraColon_isIgnored() throws IOException {
        Files.writeString(DATA_FILE, "good:pass\nbad:colon:extra\n");
        UserService reloaded = new UserService();
        assertTrue(reloaded.validateLogin("good", "pass"));
        assertFalse(reloaded.validateLogin("bad", "colon")); // ignored because parts.length != 2
    }

    // 20. Concurrent registrations do not corrupt data
    @Test
    void concurrentRegistrations_allPersistCorrectly() throws Exception {
        java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(4);
        for (int i = 0; i < 20; i++) {
            final int idx = i;
            pool.submit(() -> assertTrue(service.registerUser("cUser" + idx, "pw" + idx)));
        }
        pool.shutdown();
        pool.awaitTermination(3, java.util.concurrent.TimeUnit.SECONDS);

        UserService reloaded = new UserService();
        for (int i = 0; i < 20; i++) {
            assertTrue(reloaded.validateLogin("cUser" + i, "pw" + i));
        }
        List<String> lines = Files.readAllLines(DATA_FILE);
        assertEquals(20, lines.size());
    }
}