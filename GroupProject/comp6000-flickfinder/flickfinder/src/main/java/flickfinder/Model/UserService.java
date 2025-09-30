package flickfinder.Model;

import org.springframework.stereotype.Service;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private static final String USER_DATA_FILE = "users.txt";
    private final Map<String, String> users = new HashMap<>();
    private String currentUser;  // Track the currently logged-in user

    public UserService() {
        loadUsers();
    }

    private void loadUsers() {
        File file = new File(USER_DATA_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]); // username -> password
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) {
            return false; // User already exists
        }
        users.put(username, password);
        saveUsers();
        return true;
    }

    public boolean validateLogin(String username, String password) {
        boolean valid = users.containsKey(username) && users.get(username).equals(password);
        if (valid) {
            // Set the current user upon a successful login
            currentUser = username;
        }
        return valid;
    }

    public int getCurrentUserId() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }
        // Here we use the hash code of the username as a dummy user ID.
        return currentUser.hashCode();
    }

    public String getCurrentUsername() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }
        return currentUser;
    }
}